package com.voxbiblia.rjmailer;

import java.net.Socket;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

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
    
    public ConversationHandler(String ehloHostname)
    {
        this.ehloHostname = ehloHostname;
    }

    public String sendMail(JsmMailMessage message, String serverName)
            throws IOException
    {
        Socket s = new Socket(serverName, 25);
        return send(message, s);
    }

    String send(JsmMailMessage message, Socket socket)
            throws IOException
    {
        byte[] inBuf = new byte[1000];
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        checkStatus(is, inBuf, 220);
        sendCommand("EHLO", ehloHostname, os);
        checkStatus(is, inBuf, 250);
        
        return null;
    }

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

    void sendCommand(String cmd, String param, OutputStream out)
            throws IOException
    {
        out.write(cmd.getBytes("US-ASCII"));
        if(param != null) {
            out.write((byte)' ');
            out.write(param.getBytes("US-ASCII"));
        }
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
