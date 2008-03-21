package com.voxbiblia.rjmailer;

import java.net.Socket;
import java.io.*;
import java.util.*;

/**
 * A socket that one can read data from that is set up in the constructor.
 */
public class DummySMTPSocket extends Socket
{
    private static final int WRITING_TO_SERVER = 0;
    private static final int READING_FROM_SERVER = 1;
    private static final int GOT_SERVER_ITEM = 2;

    // indicates which line we are at
    private int state = WRITING_TO_SERVER;

    private List fromServer, toServer, innerTo;
    private String currentExpected;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private Map substitutions;

    public DummySMTPSocket(String[] conversation, File dataContent)
    {
        this(conversation, dataContent, new HashMap());
    }

    public DummySMTPSocket(String[] conversation, File dataContent, Map substitutions)
    {
        this.substitutions = substitutions;
        fromServer = new ArrayList();
        toServer = new ArrayList();
        innerTo = new ArrayList();
        int i = 0;
        while  (true) {
            if (i > conversation.length - 1) {
                break;
            }
            fromServer.add(conversation[i++]);
            if (i > conversation.length - 1) {
                break;
            }
            String s = conversation[i++];
            if ("IN_FILE".equals(s)) {
                s = read(dataContent);
            }
            toServer.add(s);
        }
    }

    /**
     * Returns true if the conversation on this socket has been
     * finished, else false.
     *
     * @return true if the conversation is finshed, else false
     */
    public boolean hasFinished()
    {
        return fromServer.size() == 0 && toServer.size() == 0;
    }

    /**
     * Used by the output stream to save what the client writes to
     * the server, for debug comparisons.
     *
     * @param b an int containing a byte of data
     */
    public void storeWritten(int b)
    {
        baos.write(b);
    }


    public byte[] getToServer()
    {
        String s = pop(innerTo);
        if (s == null) {
            baos.reset();
            s = pop(toServer);
            currentExpected = s;
            if (state == READING_FROM_SERVER) {
                state = GOT_SERVER_ITEM;
            } else if (state == GOT_SERVER_ITEM) {
                state = WRITING_TO_SERVER;
            }
            if (s != null && s.contains("\n")) {
                String[] lines = s.split("\n");
                innerTo.addAll(Arrays.asList(lines));
                s = pop(innerTo);
            }
        }
        Iterator i = substitutions.keySet().iterator();
        if (s != null) {
            while (i.hasNext()) {
                String key = (String)i.next();
                s = s.replaceFirst(key, (String)substitutions.get(key));
            }
        }
        byte[] bs = getNextLine(s);
        if (bs == null) {
            return null;
        }
        if (state == WRITING_TO_SERVER) {
            throw new IllegalArgumentException();
        }
        return bs;
    }

    public byte[] getFromServer()
    {
        byte[] bs = getNextLine(pop(fromServer));
        if (bs == null && toServer.size() == 0) {
            return null;
        }
        if (state == READING_FROM_SERVER) {
            throw new IllegalArgumentException();
        }
        state = READING_FROM_SERVER;
        return bs;
    }

    private String pop(List l)
    {
        if (l.size() == 0) {
            return null;
        }
        return (String)l.remove(0);
    }

    private byte[] getNextLine(String s)
    {
        if (s == null) {
            return null;
        }
        if (s.endsWith("\r")) {
            s = s + "\n";
        } else if (!s.endsWith("\r\n")) {
            s = s + "\r\n";
        }
        try {
            return s.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }

    public void wrongChar(int position)
    {
        try {
            writeToFile("expected.txt", currentExpected.getBytes("US-ASCII"));
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
        writeToFile("actual.txt", baos.toByteArray());
        throw new IllegalArgumentException("got wrong char at position "+ position);
    }

    private void writeToFile(String filename, byte[] data)
    {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filename);
            fos.write(data);
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private static class DSOutputStream
            extends OutputStream
    {
        private DummySMTPSocket parent;
        private byte[] data = null;
        private int pos;
        private int endSeq = 0;

        public DSOutputStream(DummySMTPSocket parent)
        {
            this.parent = parent;
        }

        public void write(int actual)
                throws IOException 
        {
            if (data == null || data.length == pos) {
                data = parent.getToServer();
                if (data == null) {
                    throw new IllegalArgumentException("trying to write to server " +
                            "when there is no data in the dummy socket to match " +
                            "to the written data");
                }
                pos = 0;
            }
            parent.storeWritten(actual);

            byte expected = data[pos++];
            if (actual == '\r') {
                endSeq = 1;
            } else if (actual == '\n') {
                if (endSeq == 1) {
                    data = null;
                }
                endSeq = 0;
            } else if (actual != expected) {
                endSeq = 0;
                parent.wrongChar(pos);
            } else {
                endSeq = 0;
            }
        }
    }

    /**
     * An input stream that reads from the canned conversation
     */
    private static class DSInputStream
            extends InputStream
    {
        private DummySMTPSocket parent;
        private byte[] data = null;
        private int pos = 0;

        public DSInputStream(DummySMTPSocket parent)
        {
            this.parent = parent;
        }

        public int read()
                throws IOException
        {
            if (data == null || pos == data.length) {
                data = parent.getFromServer();
                if (data == null) {
                    return -1;
                }
                pos = 0;
            }
            return data[pos++];
        }
    }

    /*
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
    */

    public InputStream getInputStream()
    {
        return new DSInputStream(this);
    }

    public OutputStream getOutputStream()
    {
        return new DSOutputStream(this);
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

}
