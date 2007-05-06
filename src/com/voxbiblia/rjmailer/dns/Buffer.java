package com.voxbiblia.rjmailer.dns;

import java.util.List;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;

/**
 * Something like a ByteArrayInputStream but with methods for
 * reading DNS query response data.
 *
 * @author Noa Resare (noa@voxbiblia.com)
 */
class Buffer
{
    private final byte[] bytes;
    private int pos;

    /**
     * Constructs a Buffer wrapping the supplied bytes.
     *
     * @param bytes an array of bytes to wrap
     */
    public Buffer(byte[] bytes)
    {
        this.bytes = bytes;
        this.pos = 0;
    }

    /**
     * Reads a 16 bit big endian unsigned integer from the current position
     * of this buffer.
     *
     * @return an int
     */
    public int readInt16()
    {
        return (positive(bytes[pos++]) << 8) + positive(bytes[pos++]);
    }

    public static int parseInt16(byte[] buffer)
    {
        return (positive(buffer[0]) << 8) + positive(buffer[1]);
    }

    static int positive(byte b) {
        return b < 0 ? b + 0x100 : b;
    }


    public int read()
    {
        return bytes[pos++];
    }

    public void skip()
    {
        pos++;
    }

    public void skip(int i)
    {
        pos += i;
    }

    /**
     * Reads a domain name from the buffer in the format described in
     * RFC1035 3.1.
     *
     * @return a domain name
     */
    public String readName()
    {
        List l = new ArrayList();
        readName(l, pos, false);
        StringBuffer sb = new StringBuffer();
        if (l.size() > 0) {
            sb.append(l.get(0));
        }
        for (int i = 1; i < l.size(); i++) {
            sb.append('.');
            sb.append(l.get(i));
        }
        return sb.toString();
    }

    private void readName(List components, int offset, boolean compressed)
    {
        int len = positive(bytes[offset++]);
        int consumed = 1;
        if ((len & 0xc0) > 0) {
            readName(components, ((len & 0x3f) << 8) + positive(bytes[offset]), true);
            consumed++;
        } else if (len > 0) {
            try {
                components.add(new String(bytes, offset, len, "US-ASCII"));
            } catch (UnsupportedEncodingException e) {
                throw new Error("unsupported encoding: US-ASCII");
            }
            consumed += len;
            if (bytes.length > offset + len) {
                readName(components, offset + len, compressed);
            }
        }
        if (!compressed) {
            pos += consumed;
        }
    }
}
