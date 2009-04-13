package com.voxbiblia.rjmailer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.List;
import java.util.Properties;

/**
 * A ConversationHandler instance knows how to carry out an SMTP conversation
 * with a remote host.
 *
 * @author Noa Resare (noa@voxbiblia.com)
 */
class ConversationHandler
{
    private static final Logger log = LoggerFactory.getLogger(ConversationHandler.class);

    private String ehloHostname;
    private static final byte[] EOL = {(byte)'\r', (byte)'\n'};
    // default access so that test case code can access it
    FieldGenerator fieldGenerator;
    SocketFactory socketFactory;

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
        fieldGenerator = new FieldGenerator(ehloHostname);
    }

    /**
     * Sends an email message to a specified server using the SMTP protocol.
     *
     * @param message The message to send
     * @param server the name of the server to connect to
     * @return the tracking information recived from the server upon accept
     * @param to array of strings specifying recieving email addresses
     */
    public String sendMail(RJMMessage message, List<String> to, String server)
    {
        Socket s0 = null;
        try {
            s0 = socketFactory.createSocket(server, 25);

            if (message == null) {
                throw new RJMInputException("Can not send null message");
            }
            if (to == null || to.isEmpty()) {
                throw new RJMInputException("Not enough addresses to send email " +
                        "to, please supply at least one");
            }

            byte[] inBuf = new byte[1000];
            InputStream is = s0.getInputStream();
            OutputStream os = s0.getOutputStream();
            checkStatus(is, inBuf, 220);
            sendCommand("EHLO " + ehloHostname, os);
            checkStatus(is, inBuf, 250);

            String from = message.getFrom();
            if (from == null || from.length() < 1) {
                throw new RJMInputException("Can not send email with null sender " +
                        "address");
            }
            sendCommand("MAIL FROM: <" + AddressUtil.getAddress(from) + ">", os);
            checkStatus(is, inBuf, 250);

            for (String s : to) {
                sendCommand("RCPT TO: <" + s +">", os);
                checkStatus(is, inBuf, 250);
            }
            sendCommand("DATA", os);
            checkStatus(is, inBuf, 354);
            writeHeaders(message, os);
            String charset = TextEncoder.getCharset(message.getText());
            String data = TextEncoder.canonicalize(message.getText());
            os.write(toBytes("\r\n" + TextEncoder.encodeQP(data, charset)
                    + "\r\n.\r\n"));

            return checkStatus(is, inBuf, 250).substring("250 ".length());
        } catch (IOException e) {
            throw new RJMException(RJMException.ExactCause.SMTP_CONNECT,
                    "Connection to the email server failed")
                    .setServer(server);
        } finally {
            safeClose(s0, server);
        }
    }


    private void safeClose(Socket socket, String server)
    {
        try {
            socket.close();
        } catch (IOException e) {
            log.warn("Closing the of the socket to server '{}' failed " +
                    "with an IOException. Ignoring", server);
        }
    }

    private void writeHeaders(RJMMessage msg, OutputStream os)
            throws IOException
    {
        os.write(toBytes(AddressUtil.encodeAddressHeader("From", msg.getFrom())));
        List<String> to = msg.getTo();
        if (to != null) {
            os.write(toBytes(AddressUtil.encodeAddressHeader("To", to)));            
        }

        String subject = msg.getSubject();
        if (subject != null) {
            os.write(toBytes(TextEncoder.encodeHeader("Subject",
                    msg.getSubject())));
        }
        os.write(toBytes("Date: " + fieldGenerator.getDate() + "\r\n"));
        os.write(toBytes("Message-ID: <" + fieldGenerator.getMessageId() +
                ">\r\n"));
        String charset = TextEncoder.getCharset(msg.getText());
        os.write(toBytes("Mime-Version: 1.0\r\n"));
        os.write(toBytes("X-Mailer: rjmailer (" + getVersion() + ")\r\n"));
        if (!charset.equals("US-ASCII")) {
            os.write(toBytes("Content-Type: text/plain; charset=" + charset
                    +"\r\n"));

        }
        os.write(toBytes("Content-Transfer-Encoding: quoted-printable\r\n"));
    }

    private static String version;

    static String getVersion()
    {
        if (version == null) {
            ClassLoader cl = ClassLoader.getSystemClassLoader();

            InputStream is  = cl.getResourceAsStream("com/voxbiblia/rjmailer/version.properties");
            Properties p = new Properties();
            try {
                p.load(is);
            } catch (IOException e) {
                throw new Error("could not load version from jar");
            }
            version = p.getProperty("rjmailer.version");
        }
        return version;
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
     * @return the last line returned from server
     */
    static String checkStatus(InputStream is, byte[] inBuf, int expected)
            throws IOException
    {
        String line = getServerLine(is, inBuf);
        int status = getStatus(line);
        if (status != expected) {
            throw new RJMException(RJMException.ExactCause.SMTP_UNEXPECTED_STATUS,
                    "Unexpected status value from the server")
                    .setServerLine(line);
        }
        while (line.length() > 3 && line.charAt(3) == '-') {
            line = getServerLine(is, inBuf);
        }
        return line;
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

    void setSocketFactory(SocketFactory socketFactory)
    {
        this.socketFactory = socketFactory;
    }
}
