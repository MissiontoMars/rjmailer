package com.voxbiblia.rjmailer.dns;

/**
 * An abstraction of some kind of low level transportation mechanism, such
 * as an UDP socket connected to a specific service.
 *
 * @author Noa Resare (noa@voxbiblia.com)
 */
interface TransportService
{
    /**
     * Sends the specified data to the transport.
     *
     * @param data
     */
    void send(byte[] data);

    /**
     * Blocks until data is recieved fro the transport, then returns
     * the number of bytes written to the given buffer.
     *
     * @param buffer the buffer to write the data to
     * @return the number of bytes recieved
     */
    int recv(byte[] buffer);
}
