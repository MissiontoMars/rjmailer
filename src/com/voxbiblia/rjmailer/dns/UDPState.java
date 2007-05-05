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
    private DatagramPacket query;

    public byte[] getResponse()
    {
        return response;
    }

    public void setResponse(byte[] response)
    {
        this.response = response;
    }

    public DatagramPacket getQuery()
    {
        return query;
    }

    public void setQuery(DatagramPacket query)
    {
        this.query = query;
    }
}
