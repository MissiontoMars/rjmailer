package com.voxbiblia.rjmailer.dns;

import junit.framework.TestCase;

import java.net.InetAddress;
import java.io.FileOutputStream;

/**
 * TODO: add docs
 *
 * @author Noa Resare (noa@voxbiblia.com)
 */
public class UDPServiceTest
    extends TestCase
{

    public void testSendRecv()
            throws Exception
    {
        UDPService s = new UDPService(InetAddress.getByName("192.168.0.1"));
        byte[] data = s.sendRecv(new Query("resare.com"));
        FileOutputStream fos = new FileOutputStream("test/data/answer.bin");
        fos.write(data);
        fos.close();
    }
}
