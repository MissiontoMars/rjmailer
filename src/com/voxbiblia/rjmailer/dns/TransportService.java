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
     * Sends the specified answer to the transport.
     *
     * @param data the data to send.
     */
    void send(byte[] data);

    /**
     * Blocks until answer is recieved from the transport, then returns
     * the number of bytes written to the given buffer.
     *
     * @param buffer the buffer to write the answer to
     * @return the number of bytes recieved
     */
    int recv(byte[] buffer);
}
