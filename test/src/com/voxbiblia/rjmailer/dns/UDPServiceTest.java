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
    public void testParseBEUInt16()
    {
        byte[] buf = new byte[2];
        buf[0] = 18;
        buf[1] = 19;
        assertEquals(4627, Resolver.parseBEUInt16(buf, 0));

        buf[0] = -1;
        buf[1] = -1;
        assertEquals(65535, Resolver.parseBEUInt16(buf, 0));
    }

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
