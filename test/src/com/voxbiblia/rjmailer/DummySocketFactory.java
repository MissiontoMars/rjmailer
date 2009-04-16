package com.voxbiblia.rjmailer;

import java.io.IOException;
import java.net.Socket;

/**
 * Returns a dummy sockets
 */
public class DummySocketFactory
    implements SocketFactory
{
    private Socket s;
    
    public DummySocketFactory(Socket s)
    {
        this.s = s;
    }

    public Socket createSocket(String serverName, int port) throws IOException
    {
        return s;
    }
}
