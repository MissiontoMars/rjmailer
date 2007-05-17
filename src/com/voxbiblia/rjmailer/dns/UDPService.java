package com.voxbiblia.rjmailer.dns;

import com.voxbiblia.rjmailer.RJMException;
import com.voxbiblia.rjmailer.RJMTimeoutException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.io.IOException;

/**
 * Low level multithreaded UDP Service that sends and recieves UDP messages,
 * pairing requests and responses based on the id value in the first two
 * packet bytes.
 *
 * @author Noa Resare (noa@voxbiblia.com)
 */
class UDPService
{
    private static Logger log = Logger.getLogger(UDPService.class.getName());

    // default timeout value is 20 seconds.
    private int timeout = 20;
    private Map queryMap = Collections.synchronizedMap(new HashMap());
    private InetAddress server;
    private static final int PORT = 53;

    private DatagramSocket socket;
    private UDPScheduler scheduler;

    /**
     * Constructs a new UDPService and spins up the Recieve and Scheduler
     * daemon threads.
     *
     * @param server the address of the server to send data to.
     */
    public UDPService(InetAddress server)
    {
        this.server = server;
        try {
            socket = new DatagramSocket();
            socket.connect(server, PORT);
        } catch (SocketException e) {
            throw new RJMException(e);
        }
        scheduler = new UDPScheduler(socket, queryMap);
        scheduler.setDaemon(true);
        scheduler.start();

        Thread t = new Reciever();
        t.setDaemon(true);
        t.start();
    }

    /**
     * Delegates responsibility for sending the specified query packet to
     * the udp service.
     *
     * @param query query data
     *
     * @return a DatagramPacket recieved from the server with matching id field
     *
     * @throws RJMTimeoutException if the specified timeout has been reached
     * without an answer.
     */
    public byte[] sendRecv(Query query)
    {
        UDPState state = new UDPState();
        byte[] queryBytes = query.toWire();
        state.setQuery(new DatagramPacket(queryBytes, queryBytes.length, server, PORT));
        state.setId(Buffer.parseInt16(queryBytes));
        Integer key = new Integer(query.getId());
        queryMap.put(key, state);
        scheduler.enqueue(state);
        try {
            synchronized(state) {
                state.wait(timeout * 1000);
            }
        } catch (InterruptedException e) {
            throw new Error("someone interruped this thread");
        }
        queryMap.remove(key);
        byte[] response = state.getResponse();
        if (response == null) {
            Throwable t = state.getException();
            if (t != null) {
                throw new RJMException(t);
            }
            throw new RJMTimeoutException("timeout " +
                    "after " + timeout + " seconds.");
        }
        return response;
    }

    public void setTimeout(int seconds)
    {
        this.timeout = seconds;
    }

    private class Reciever extends Thread
    {
        public void run()
        {
            // the size limit for UDP packets according to RFC1035 2.3.4 is 512 octets.
            DatagramPacket dp = new DatagramPacket(new byte[512], 512);
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    socket.receive(dp);
                    int id = Buffer.parseInt16(dp.getData());
                    scheduler.remove(id);
                    UDPState state = (UDPState)queryMap.get(new Integer(id));
                    if (state != null) {
                        byte[] data = new byte[dp.getLength()];
                        System.arraycopy(dp.getData(), 0, data, 0, dp.getLength());
                        state.setResponse(data);
                        synchronized(state) {
                            state.notify();
                        }
                    } else {
                        log.info("no query for packet with id " + id + " ignoring");
                    }
                } catch (IOException e) {
                    throw new RJMException(e);
                }

            }
        }

    }


}
