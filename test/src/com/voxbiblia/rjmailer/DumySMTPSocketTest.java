package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Tests DummySMTPSocket
 */
public class DumySMTPSocketTest
    extends TestCase
{
    public void testReadEnd()
            throws IOException
    {
        DummySMTPSocket dss = new DummySMTPSocket(new String[] {"foo", "bar"});
        InputStream is = dss.getInputStream();
        OutputStream os = dss.getOutputStream();
        assertEquals('f', is.read());
        assertEquals('o', is.read());
        assertEquals('o', is.read());
        assertEquals('\r', is.read());
        assertEquals('\n', is.read());

        try {
            //noinspection ResultOfMethodCallIgnored
            is.read();
            fail("should have gotten IAE");
        } catch (IllegalArgumentException e) {

        }
        os.write('b');
        os.write('a');
        os.write('r');
        os.write('\r');
        os.write('\n');        
        try {
            //noinspection ResultOfMethodCallIgnored
            is.read();
        } catch (IllegalArgumentException e) {
            fail("have written, should not give IAE");
        }
        assertEquals(-1, is.read());
    }

    public void testWriteWrong()
            throws IOException
    {
        DummySMTPSocket dss = new DummySMTPSocket(new String[] {"foo", "bar"});
        InputStream is = dss.getInputStream();
        OutputStream os = dss.getOutputStream();
        assertEquals('f', is.read());
        assertEquals('o', is.read());
        assertEquals('o', is.read());
        assertEquals('\r', is.read());
        assertEquals('\n', is.read());

        try {
            //noinspection ResultOfMethodCallIgnored
            is.read();
            fail("should have gotten IAE");
        } catch (IllegalArgumentException e) {

        }
        try {
            os.write('X');
            fail("should have gotten IAE");
        } catch (IllegalArgumentException e) {
            
        }

    }
}
