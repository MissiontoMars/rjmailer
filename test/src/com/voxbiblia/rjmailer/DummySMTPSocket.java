package com.voxbiblia.rjmailer;

import java.net.Socket;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A socket that one can read data from that is set up in the constructor.
 */
public class DummySMTPSocket extends Socket
{
    private String[] responses;
    private int currentResponse, currentPos;

    public DummySMTPSocket(String[] responses)
    {
        this.responses = responses;
    }

    /**
     * An input stream that reads from the canned responses
     */
    private class DSInputStream
            extends InputStream
    {

        public int read() throws IOException
        {
            if ((currentResponse % 2) == 1) {
                throw new IllegalArgumentException("reading when you should be writing");
            }
            if (responses.length == currentResponse) {
                return -1;
            }
            String s = responses[currentResponse];
            if (currentPos == s.length()) {
                currentPos++;
                return '\r';
            } else if (currentPos == s.length() + 1) {
                currentPos = 0;
                currentResponse++;
                return '\n';
            }
            return s.charAt(currentPos++);
        }
    }

    private class DSOutputStream
        extends OutputStream
    {
        public void write(int i) throws IOException
        {
            if ((currentResponse % 2) == 0) {
                throw new IllegalArgumentException("writing when you should be reading");
            }
            String s = responses[currentResponse];
            if (currentPos == s.length()) {
                currentPos++;
                if (i != '\r') {
                    throw new IllegalArgumentException("should have written CR");
                }
            } else if (currentPos == s.length() + 1) {
                currentPos = 0;
                currentResponse++;
                if (i != '\n') {
                    throw new IllegalArgumentException("should have written LF");
                }
            } else if (i != s.charAt(currentPos++)) {
                throw new IllegalArgumentException("got wrong char");
            }

            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public InputStream getInputStream()
    {
        return new DSInputStream();
    }

    public OutputStream getOutputStream()
    {
        return new DSOutputStream();
    }
}
