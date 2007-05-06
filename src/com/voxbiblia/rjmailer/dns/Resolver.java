package com.voxbiblia.rjmailer.dns;

import com.voxbiblia.rjmailer.RJMException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A Resolver instance resolves DNS queries by relaying them an external
 * name server whose ip is specified in the constructor.
 */
public class Resolver
{
    private UDPService udpService;
    int timeout;

    /**
     * Creates a new Resolver instance with the given server as it's
     * name server.
     *
     * @param server the ip numer, or hostname of the server to use
     */
    public Resolver(String server)
    {
        try {
            InetAddress ia = InetAddress.getByName(server);
            udpService = new UDPService(ia);
        } catch (UnknownHostException e) {
            throw new RJMException(e);
        }
    }

    /**
     * Resolves a Query using the server supplied in the Resolver constructor.
     *
     * @param query
     * @return a sorted List of MXRecord objects, with lowest preference first.
     *
     */
    public List resolve(Query query)
    {
        return parseResponse(udpService.sendRecv(query));
    }

    /**
     * Parses an incoming MX query and returns a sorted list of MXRecord
     * objects (lowest preference first).
     *
     * @param data an array of bytes read from an response packet from a
     * DNS server.
     * @return a sorted List of MXRecord objects, with lowest preference first.
     */
    static List parseResponse(byte[] data)
    {
        // RFC1035 4.1, Message Format
        Buffer buffer = new Buffer(data);

        // 4.1.1 Header format
        // id is already matched, skipping
        buffer.skip(2);
        int flagByte = buffer.read();
        if ((flagByte & 0x80) == 0) {
            throw new RJMException("got response that claimed to be a query");
        }
        buffer.skip();
        int questionCount = buffer.readInt16();
        int answerCount = buffer.readInt16();
        // for now, we're not interested in authority or additional records
        buffer.skip(4);

        List mxRecords = new ArrayList();

        for (int i = 0; i < questionCount; i++) {
            readQuery(buffer);
        }
        for (int i = 0; i < answerCount; i++) {
            readRecord(buffer, mxRecords);
        }

        Collections.sort(mxRecords);

        return mxRecords;
    }

    static void readQuery(Buffer buffer)
    {
        // RFC 1035 4.1.2
        buffer.readName();
        // skipping type and class
        buffer.skip(4);
    }

    static void readRecord(Buffer buffer, List mxRecords)
    {
        // RFC 1035 4.1.3
        // ignoring name
        buffer.readName();
        int typeCode = buffer.readInt16();

        // skipping class and ttl for now
        buffer.skip(6);
        int rLength = buffer.readInt16();
        switch (typeCode) {
            case 15: // MX 3.3.9
                MXRecord mx = new MXRecord();
                mx.setPreference(buffer.readInt16());
                mx.setExchange(buffer.readName());
                mxRecords.add(mx);
                break;
            /*
            case 2:  // NS 3.3.11
                dump("nameserver: " + buffer.readName());
                break;
            */
            default:
                buffer.skip(rLength);
                break;
        }
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
        udpService.setTimeout(timeout);
    }

}
