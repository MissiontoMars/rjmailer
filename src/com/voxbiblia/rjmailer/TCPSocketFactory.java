package com.voxbiblia.rjmailer;

import java.net.Socket;
import java.io.IOException;

/**
 * Default SocketFactory implementation, returns regular TCP sockets.
 */
class TCPSocketFactory implements SocketFactory
{
    public Socket createSocket(String serverName, int port)
            throws IOException
    {
        return new Socket(serverName, port);
    }
}
