package com.voxbiblia.rjmailer.dns;

import com.voxbiblia.rjmailer.RJMParseException;

import java.util.Random;
import java.io.ByteArrayOutputStream;

/**
 * Represents a query sent to the DNS server.
 *
 *
 */
public class Query
{
    private String name, type;
    private int id;

    private static Random random = new Random();

    public Query()
    {
        this.id = random.nextInt(0xffff); 
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public int getId()
    {
        return id;
    }

    public byte[] toWire()
    {
        // see RFC1035 4.1.1
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] header = new byte[12];
        return null;
    }

    byte[] getHeader()
    {
        byte[] header = new byte[12];
        header[0] = (byte)(id >> 8 & 0xff);
        header[1] = (byte)(id & 0xff);
        // flags, bit 0 and 7 is 1 all others 0
        header[2] = (byte)(1 + (1 << 7));
        // qdcount == 0
        // always contains one (1) query. The rest
        // of the sizes are zero.
        header[5] = 1;
        
        return header;
    }

    byte[] nameToWire()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int start = 0;
        char[] nameChars = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            char c = nameChars[i];
            if (c > 0x80) {
                throw new RJMParseException("illegal char in domain name: " + c);
            }
            if (c == '.') {
                if (i - start > 63) {
                    throw new RJMParseException("contains label longer than 63 chars: " + name);
                }
                baos.write(i - start);
                for (int j = start; j < i; j++) {
                    baos.write(nameChars[j]);
                }
                i++;
                start = i;
            }
        }
        if (start < nameChars.length) {
            baos.write(nameChars.length - start);
            for (int j = start; j < nameChars.length; j++) {
                baos.write(nameChars[j]);
            }
        }
        baos.write(0);
        byte[] result = baos.toByteArray();
        if (result.length > 255) {
            throw new RJMParseException("name " + name + " too long.");
        }
        return result;
    }
}
