package com.voxbiblia.rjmailer;

import java.net.Socket;
import java.io.IOException;

/**
 * Implementations of this interface can create socket from server names and
 * port numbers.
 */
interface SocketFactory
{
    Socket createSocket(String serverName, int port) throws IOException;
}
