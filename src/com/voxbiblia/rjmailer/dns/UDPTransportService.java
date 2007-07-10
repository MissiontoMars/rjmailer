package com.voxbiblia.rjmailer.dns;

import com.voxbiblia.rjmailer.RJMException;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Implements the TransportService as an UDP datagram socket connected to
 * a specified server on port 53.
 * 
 * @author Noa Resare (noa@voxbiblia.com)
 */
class UDPTransportService implements TransportService
{
    private DatagramSocket socket;
    private static final int PORT = 53;
    private static final Logger log = Logger.getLogger(UDPTransportService.class.getName());

    public UDPTransportService(String serverName)
    {
        try {
            socket = new DatagramSocket();
            socket.connect(InetAddress.getByName(serverName), PORT);
        } catch (Exception e) {
            throw new RJMException(e);
        }
    }

    public void send(byte[] data)
    {
        DatagramPacket dp = new DatagramPacket(data, data.length);
        if (log.isLoggable(Level.FINE)) {
            log.fine("sending packet with id " + Buffer.parseInt16(data));
        }
        try {
            socket.send(dp);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public int recv(byte[] buffer)
    {
        try {
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            socket.receive(dp);
            if (log.isLoggable(Level.FINE)) {
                log.fine("recieving packet with id " + Buffer.parseInt16(buffer));
            }
            return dp.getLength();
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}
