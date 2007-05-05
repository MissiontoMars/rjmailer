package com.voxbiblia.rjmailer.dns;

import com.voxbiblia.rjmailer.RJMParseException;
import com.voxbiblia.rjmailer.RJMException;

import java.util.Random;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a query sent to the DNS server.
 *
 *
 */
public class Query
{
    private String name;
    private int id;

    private static Random random = new Random();

    public Query(String name)
    {
        this.id = random.nextInt(0xffff);
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    private byte Z = (byte)0;

    public byte[] toWire()
    {
        // RFC1035 4.1.2
        ByteArrayOutputStream baos = new ByteArrayOutputStream(10 + calculateNameLength());
        try {
            // the header, 4.1.1
            writeBEUInt16(id, baos);
            baos.write(new byte[] {(byte)(1 + (1 << 7)), Z,
                    Z, (byte)1, Z, Z, Z, Z, Z, Z});
            nameToWire(baos);
            // TYPE MX
            writeBEUInt16(15, baos);
            // CLASS IN
            writeBEUInt16(1, baos);
        } catch (IOException e) {
            throw new RJMException(e);
        }
        return baos.toByteArray();
    }


    static void writeBEUInt16(int i, OutputStream os)
            throws IOException
    {
        os.write((byte)(i >> 8 & 0xff));
        os.write((byte)(i & 0xff));
    }


    int calculateNameLength()
    {
        int len = name.length();
        return name.charAt(len - 1) == '.' ? len : len + 1;
    }

    /**
     * Converts a domain name to bytes suitable for DNS wire transfer.
     *
     */
    void nameToWire(OutputStream os)
            throws IOException
    {
        int start = 0;
        char[] nameChars = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            char c = nameChars[i];
            if (c > 0x80) {
                throw new RJMParseException("illegal char in domain name: " + c);
            }
            if (c == '.') {
                if (i - start > 63) {
                    throw new RJMParseException("contains label longer than " +
                            "63 chars: " + name);
                }
                os.write(i - start);
                for (int j = start; j < i; j++) {
                    os.write(nameChars[j]);
                }
                i++;
                start = i;
            }
        }
        if (start < nameChars.length) {
            os.write(nameChars.length - start);
            for (int j = start; j < nameChars.length; j++) {
                os.write(nameChars[j]);
            }
        }
        os.write(0);
    }
}
