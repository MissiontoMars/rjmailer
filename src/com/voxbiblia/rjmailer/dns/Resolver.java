package com.voxbiblia.rjmailer.dns;

import com.voxbiblia.rjmailer.RJMException;

import java.net.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * A Resolver instance resolves DNS queries by relaying them an external
 * name server whose ip is specified in the constructor.
 */
public class Resolver
{
    InetAddress server;
    int timeout;
    private static final int PORT = 53;

    /**
     * Creates a new Resolver instance with the given serverIp as it's
     * name server.
     *
     * @param serverIp
     */
    public Resolver(String serverIp)
    {
        try {
            this.server = InetAddress.getByName(serverIp);
        } catch (UnknownHostException e) {
            throw new RJMException(e);
        }
    }

    public List resolve(Query query)
    {
        try {
            DatagramSocket ds = new DatagramSocket();
            byte[] bs = query.toWire();
            DatagramPacket dp = new DatagramPacket(bs, bs.length, server, PORT);
            try {
                ds.send(dp);
            } catch (IOException e) {
                throw new RJMException(e);
            }
        } catch (SocketException e) {
            throw new RJMException(e);
        }
        return new ArrayList();
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
}
