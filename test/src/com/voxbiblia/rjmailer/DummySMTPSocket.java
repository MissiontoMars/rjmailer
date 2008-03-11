package com.voxbiblia.rjmailer;

import java.net.Socket;
import java.io.*;

/**
 * A socket that one can read data from that is set up in the constructor.
 */
public class DummySMTPSocket extends Socket
{
    private String[] responses;
    private int currentResponse, currentPos;
    private String data;
    private byte[] expected;

    public DummySMTPSocket(String[] responses, File dataContent)
    {
        this.responses = responses;
        if (dataContent != null) {
            this.data = read(dataContent);
        }
    }

    private String read(File dataContent)
    {
        byte[] data = new byte[(int)dataContent.length()];
        try {
            FileInputStream fis = new FileInputStream(dataContent);
            if (fis.read(data) != dataContent.length()) {
                throw new Error("read of wrong size");
            }
        } catch (IOException e) {
            throw new Error(e);
        }
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }

    /**
     * An input stream that reads from the canned responses
     */
    private class DSInputStream
            extends InputStream
    {
        int errorOffset = 0;
        byte[] error;

        public DSInputStream() {
            try {
                error = "500 ERROR\r\n".getBytes("US-ASCII");
            } catch (UnsupportedEncodingException e) {
                throw new Error(e);
            }
        }

        public int read() throws IOException
        {
            if (errorMessage != null) {
                return error[errorOffset++];
            }

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

    ByteArrayOutputStream actual = new ByteArrayOutputStream();
    String errorMessage = null;

    private class DSOutputStream
        extends OutputStream
    {
        int newlineCount = 0;


        public void write(int i) throws IOException
        {
            actual.write(i);
            if (errorMessage != null) {
                // if we have a pending error message, just let the client write
                // everything without interference
                return;
            }
            if ((currentResponse % 2) == 0) {
                errorMessage = "writing when you should be reading";
            }
            String s = responses[currentResponse];
            if ("IN_FILE".equals(s)) {
                if (data == null) {
                    errorMessage = "when IN_FILE marker is in place, second argument can not be null";
                }
                s = data;
            }

            if (i == '\r') {
                if (currentPos == s.length()) {
                        currentPos++;
                } else if (s.charAt(currentPos) == '\n') {
                    if (newlineCount == 0) {
                        newlineCount++;
                    } else {
                        expected = s.getBytes("US-ASCII");
                        errorMessage = "got '\\r' in wrong place";
                    }
                }
            } else if (i == '\n') {
                if (currentPos == s.length() + 1) {
                    currentPos = 0;
                    currentResponse++;
                    check();
                    actual.reset();
                } else if (s.charAt(currentPos) == '\n') {
                    if (newlineCount == 1) {
                        newlineCount = 0;
                        currentPos++;
                    } else {
                        expected = s.getBytes("US-ASCII");
                        errorMessage = "got '\\n' in wrong place";
                    }
                }
            } else if (i != s.charAt(currentPos++)) {
                expected = s.getBytes("US-ASCII");
                errorMessage = "got wrong char, expected "
                        + toString(s.charAt(currentPos -1)) + "' got "
                        + toString(i) +" at pos " + (currentPos - 1);
            }
        }
        private String toString(int c)
        {
            return "'" + (char)c + "' (ascii " + c + ")";
        }

    }

    public void check()
    {
        if (errorMessage != null) {
            try {
                writeToFile("actual.txt", actual.toByteArray());
                writeToFile("expected.txt", expected);
            } catch (IOException e) {
                throw new Error(e);
            }
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void writeToFile(String filename, byte[] data)
            throws IOException
    {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filename);
            fos.write(data);
        } finally {
            if (fos != null) {
                fos.close();
            }
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
