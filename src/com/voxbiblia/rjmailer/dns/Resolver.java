package com.voxbiblia.rjmailer.dns;

import com.voxbiblia.rjmailer.RJMException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A Resolver instance resolves DNS queries by relaying them an external
 * name server whose ip is specified in the constructor.
 */
public class Resolver
{
    InetAddress serverAddress;
    int timeout;

    /**
     * Creates a new Resolver instance with the given serverIp as it's
     * name server.
     *
     * @param serverIp
     */
    public Resolver(String serverIp)
    {
        try {
            this.serverAddress = InetAddress.getByName(serverIp);
        } catch (UnknownHostException e) {
            throw new RJMException(e);
        }
    }

    public void resolve(Query query)
    {
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
