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
        int newlineCount = 0;
        StringBuilder sb = new StringBuilder();

        public void write(int i) throws IOException
        {
            sb.append((char)i);
            if ((currentResponse % 2) == 0) {
                throw new IllegalArgumentException("writing when you should be reading [" + sb.toString() + "]");
            }
            String s = responses[currentResponse];
            if (i == '\r') {
                if (currentPos == s.length()) {
                        currentPos++;
                } else if (s.charAt(currentPos) == '\n') {
                    if (newlineCount == 0) {
                        newlineCount++;
                    } else {
                        throw new IllegalArgumentException("got '\\r' in wrong place");
                    }
                }
            } else if (i == '\n') {
                if (currentPos == s.length() + 1) {
                    currentPos = 0;
                    currentResponse++;
                } else if (s.charAt(currentPos) == '\n') {
                    if (newlineCount == 1) {
                        newlineCount = 0;
                        sb = new StringBuilder();
                        currentPos++;
                    } else {
                        throw new IllegalArgumentException("got '\\n' in wrong place");
                    }
                }
            } else if (i != s.charAt(currentPos++)) {
                throw new IllegalArgumentException("got wrong char, expected '"
                        + toString(s.charAt(currentPos -1)) + "' got "+toString(i)+" at pos " + (currentPos - 1) + "; " + s);
            }
        }
        private String toString(int c)
        {
            return "'" + (char)c + "' (ascii " + c + ")";
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

    public boolean hasFinished()
    {
        return currentResponse == responses.length;
    }
}
