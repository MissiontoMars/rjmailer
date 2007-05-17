package com.voxbiblia.rjmailer.dns;

import java.net.DatagramPacket;

/**
 * Contains information about the state of an UDP query-response-cycle.
 *
 * @author Noa Resare (noa@voxbiblia.com)
 */
class UDPState
{
    private byte[] response;
    private int id;
    private DatagramPacket query;
    private Throwable exception;

    public byte[] getResponse()
    {
        return response;
    }

    public void setResponse(byte[] response)
    {
        this.response = response;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public DatagramPacket getQuery()
    {
        return query;
    }

    public void setQuery(DatagramPacket query)
    {
        this.query = query;
    }

    public Throwable getException()
    {
        return exception;
    }

    public void setException(Throwable exception)
    {
        this.exception = exception;
    }
}
