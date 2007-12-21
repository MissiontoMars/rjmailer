package com.voxbiblia.rjmailer;

import java.net.Socket;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * A ConversationHandler instance knows how to carry out an SMTP conversation
 * with a remote host.
 *
 * @author Noa Resare (noa@resare.com) 
 */
class ConversationHandler
{
    private String ehloHostname;
    private static final byte[] EOL = {(byte)'\r', (byte)'\n'};

    /**
     * Constructs a ConverstaionHandler that can carry out SMTP conversations
     * to remote SMTP servers. This instance uses the given ehloHostname to
     * identify itself to the remote server.
     *
     * @param ehloHostname used to itentify this ConversationHandler to the
     * remote server during the Client initiation phase (see RFC2821 3.2)
     */
    public ConversationHandler(String ehloHostname)
    {
        this.ehloHostname = ehloHostname;
    }

    /**
     * Sends an email message to a specified server using the SMTP protocol.
     *
     * @param message The message to send
     * @param serverName the name of the server to connect to
     * @return the tracking information recived from the server upon accept
     * @param to array of strings specifying recieving email addresses
     * @throws IOException if communications fail
     */
    public String sendMail(RJMMailMessage message, String[] to, String serverName)
            throws IOException
    {
        Socket s = new Socket(serverName, 25);
        return send(message, to, s);
    }

    String send(RJMMailMessage msg, String[] to, Socket socket)
            throws IOException
    {
        if (msg == null) {
            throw new RJMInputException("Can not send null message");
        }
        byte[] inBuf = new byte[1000];
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        checkStatus(is, inBuf, 220);
        sendCommand("EHLO " + ehloHostname, os);
        checkStatus(is, inBuf, 250);

        String from = msg.getFrom();
        if (from == null || from.length() < 1) {
            throw new RJMInputException("Can not send email with null sender address");
        }
        sendCommand("MAIL FROM: <" + AddressUtil.getAddress(from) + ">", os);
        checkStatus(is, inBuf, 250);

        if (to == null || to.length < 1) {
            throw new RJMInputException("Not enough addresses to send email to, " +
                    "please supply at least one");
        }
        for (int i = 0; i < to.length; i++) {
            sendCommand("RCPT TO: <" + to[i] +">", os);
            checkStatus(is, inBuf, 250);
        }
        sendCommand("DATA", os);
        checkStatus(is, inBuf, 354);
        writeHeaders(msg, os);
        os.write(toBytes("\r\n" + msg.getText() + "\r\n.\r\n"));
        checkStatus(is, inBuf, 250);
        return null;
    }

    private static void writeHeaders(RJMMailMessage msg, OutputStream os)
            throws IOException
    {
        writeHeader("From", msg.getFrom(), os);
    
    }

    private static void writeHeader(String name, String value, OutputStream os)
            throws IOException
    {
        os.write(toBytes(name + ": " + value + "\r\n"));
    }

    private static byte[] toBytes(String s)
    {
        try {
            return s.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new Error("no longer recognising US-ASCII");
        }
    }

    /**
     * Reads one ore more response lines from the server and checks the first
     * three chars returned interpreted as digits against the expected status
     * code. Throws an RJMException if a mismatch is found.
     *
     * @param is where the status is read from
     * @param inBuf a reusable buffer that the result is put into
     * @param expected the integer value of the thre first ascii chars
     * @throws IOException if communication fails for some reason
     * @throws RJMException if there is a status code mismatch
     */
    static void checkStatus(InputStream is, byte[] inBuf, int expected)
            throws IOException
    {
        String line = getServerLine(is, inBuf);
        int status = getStatus(line);
        if (status != expected) {
            throw new RJMException(line);
        }
        while (line.length() > 3 && line.charAt(3) == '-') {
            line = getServerLine(is, inBuf);
        }
    }

    void sendCommand(String cmd, OutputStream out)
            throws IOException
    {
        out.write(cmd.getBytes("US-ASCII"));
        out.write(EOL);
    }

    static String getServerLine(InputStream is, byte[] inBuf)
            throws IOException
    {
        int i = 0;
        int b = is.read();
        int eolCount = 0;
        while (true) {
            if (b == -1) {
                // TODO: fix
                throw new Error("end of stream from server");
            } else if (eolCount == 0 && b == '\r') {
                eolCount++;
            } else if (eolCount == 1 && b == '\n') {
                break;
            } else {
                inBuf[i++] = (byte)b;
            }

            b = is.read();
        }
        return new String(inBuf, 0, i);
    }

    static int getStatus(String s) {
        return Integer.parseInt(s.substring(0,3));
    }
}
