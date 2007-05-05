package com.voxbiblia.rjmailer.dns;

import com.voxbiblia.rjmailer.RJMException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * A Resolver instance resolves DNS queries by relaying them an external
 * name server whose ip is specified in the constructor.
 */
public class Resolver
{
    private UDPService udpService;
    int timeout;
    private static final int PORT = 53;

    /**
     * Creates a new Resolver instance with the given serverIp as it's
     * name server.
     *
     * @param serverIp the ip numer, or hostname of the server to use
     */
    public Resolver(String serverIp)
    {
        try {
            InetAddress server = InetAddress.getByName(serverIp);
            udpService = new UDPService(server);
        } catch (UnknownHostException e) {
            throw new RJMException(e);
        }
    }

    public List resolve(Query query)
    {
        return null;
    }

    static List parseResponse(byte[] data)

    {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            System.out.println("id: " + parseBEUInt16(bais));
            int flagByte = bais.read();
            System.out.println((flagByte & 0x80) == 0 ? "query" : "response");
            System.out.println("authoritative: " + ((flagByte & 0x02) == 0));
            flagByte = bais.read();
            int qCount = parseBEUInt16(bais);
            System.out.println("question count: " + qCount);
            int aCount = parseBEUInt16(bais);
            System.out.println("answer count: " + aCount);
            System.out.println("authority count: " + parseBEUInt16(bais));
            System.out.println("additional count: " + parseBEUInt16(bais));
            for (int i = 0; i < qCount; i++) {
                readQuery(bais);
            }
            for (int i = 0; i < aCount; i++) {
                readRecord(bais);
            }

            return null;
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    static void readQuery(InputStream is)
            throws IOException
    {
        readName(is);
        System.out.println("type Code: " + parseBEUInt16(is));
        System.out.println("class: " + parseBEUInt16(is));
    }

    static void readRecord(InputStream is)
            throws IOException
    {
        readName(is);
        int typeCode = parseBEUInt16(is);
        System.out.println("typeCode: " + typeCode);
        switch (typeCode) {
            case 15: // MX
                int preference = parseBEUInt16(is);
                System.out.println("preference: " + preference);
                readName(is);
                break;
        }
    }

    static void readName(InputStream is)
            throws IOException
    {
        StringBuffer sb = new StringBuffer();
        int len = getLen(is);
        if (len < 0) {
            // handle compressed domain name
        }
        if (len != 0) {
            byte[] bytes = new byte[len];
            is.read(bytes);
            sb.append(new String(bytes, "US-ASCII"));
            len = getLen(is);
            if (len < 0) {
                // handle compressed domain name
            }
        }
        while (len != 0) {
            byte[] bytes = new byte[len];
            is.read(bytes);
            sb.append('.');
            sb.append(new String(bytes, "US-ASCII"));
            len = getLen(is);
            if (len < 0) {
                // handle compressed domain name
            }
        }
        System.out.println("name: " + sb.toString());
    }

    static int getLen(InputStream is)
            throws IOException
    {
        int len = is.read();
        if ((len & 0xc0)  != 0) {
            return -((len & 0x3f << 8) + is.read());
        }
        return len;
    }

    /**
     * Sets the timeout, in seconds, used for the queries to this resolver. The
     * default value is 10.
     *
     *
     * @param timeout the number of seconds until a resolve call times out.
     *
     */
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    static int parseBEUInt16(InputStream is)
    {
        try {
            return (is.read() << 8) + is.read();
        } catch (IOException e) {
            throw new Error();
        }
    }

    static int parseBEUInt16(byte[] buf, int offset)
    {
        return (pos(buf[offset]) << 8) + pos(buf[offset + 1]);
    }

    static int pos(byte b) {
        return b < 0 ? b + 0x100 : b;
    }


}
