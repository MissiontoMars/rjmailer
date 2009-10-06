package com.voxbiblia.rjmailer;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Returns a dummy sockets
 */
public class DummySocketFactory
    implements SocketFactory
{
    private Socket defaultSocket;
    private Map<String,Socket> ss = new HashMap<String, Socket>();

    public DummySocketFactory()
    {
        
    }

    public DummySocketFactory(Socket s)
    {
        defaultSocket = s;
    }

    public void addSocket(String name, Socket s)
    {
        ss.put(name, s);
    }

    public Socket createSocket(String serverName, int port) throws IOException
    {
        Socket s = ss.get(serverName);
        if (s != null) {
            return s;
        }
        if (defaultSocket == null) {
            throw new UnknownHostException(serverName);
        }
        return defaultSocket;
    }
}
