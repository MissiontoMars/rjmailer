package com.voxbiblia.rjmailer.dns;

/**
 * TODO: add docs
 *
 * @author Noa Resare (noa@voxbiblia.com)
 */
public class DummyTransportService implements TransportService
{
    public void send(byte[] data)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int recv(byte[] buffer)
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
