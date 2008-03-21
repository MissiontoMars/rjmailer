package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.util.Map;
import java.util.HashMap;

/**
 * Tests DummySMTPSocket
 */
public class DummySMTPSocketTest
    extends TestCase
{
    public void testReadEnd()
            throws IOException
    {
        DummySMTPSocket dss = new DummySMTPSocket(new String[] {"foo", "bar"},
                null);
        InputStream fromServer = dss.getInputStream();
        OutputStream toServer = dss.getOutputStream();
        assertEquals('f', fromServer.read());
        assertEquals('o', fromServer.read());
        assertEquals('o', fromServer.read());
        assertEquals('\r', fromServer.read());
        assertEquals('\n', fromServer.read());

        try {
            //noinspection ResultOfMethodCallIgnored
            fromServer.read();
            fail("should have gotten IAE");
        } catch (IllegalArgumentException e) {

        }
        toServer.write('b');
        toServer.write('a');
        toServer.write('r');
        toServer.write('\r');
        toServer.write('\n');
        try {
            //noinspection ResultOfMethodCallIgnored
            fromServer.read();
        } catch (IllegalArgumentException e) {
            fail("have written, should not give IAE");
        }
        assertEquals(-1, fromServer.read());
    }

    public void testReadMultiline()
            throws IOException
    {
        DummySMTPSocket dss = new DummySMTPSocket(
                new String[] {"foo", "bar\r\nbaz"}, null);
        /*for (int i = 0; i < 5; i++) {
            dss.getInputStream().read();    
        }
          */
        OutputStream os = dss.getOutputStream();
        InputStream is = dss.getInputStream();
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
        os.write('b');
        os.write('a');
        os.write('z');

    }

    public void testWriteWrong()
            throws IOException
    {
        DummySMTPSocket dss = new DummySMTPSocket(
                new String[] {"foo", "bar"}, null);
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

    public void testFromFile()
            throws Exception
    {
        DummySMTPSocket dss = new DummySMTPSocket(
                new String[] {"foo", "IN_FILE"},
                new File("test/data/test0.txt"));
        /*for (int i = 0; i < 5; i++) {
            dss.getInputStream().read();
        }
          */
        OutputStream os = dss.getOutputStream();
        InputStream is = dss.getInputStream();
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
        os.write('b');
        os.write('a');
        os.write('z');

    }

    public void testWithSubstitutions()
            throws Exception
    {
        Map m = new HashMap();
        m.put("@FOO@", "u");
        DummySMTPSocket dss = new DummySMTPSocket(
                new String[] {"foo", "IN_FILE"},
                new File("test/data/test3.txt"), m);
        /*for (int i = 0; i < 5; i++) {
            dss.getInputStream().read();
        }
          */
        OutputStream os = dss.getOutputStream();
        InputStream is = dss.getInputStream();
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
        os.write('b');
        os.write('u');
        os.write('z');

    }

}
