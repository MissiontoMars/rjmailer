package com.voxbiblia.rjmailer.dns;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;

/**
 * Tests the query class. 
 */
public class QueryTest
    extends TestCase
{
    public void testGetHeader()
    {
        for (int i = 0 ; i < 1; i++) {
            Query q = new Query("test");
            byte[] bytes = q.toWire();
            assertNotNull(bytes);
            int id = q.getId();
            assertEquals((byte)(id >> 8 & 0xff), bytes[0]);
            assertEquals((byte)(id & 0xff), bytes[1]);
            assertEquals((byte)1, bytes[2]);
            assertEquals(0, bytes[3]);
            assertEquals(0, bytes[4]);
            assertEquals((byte)1, bytes[5]);
            assertEquals(0, bytes[6]);
            assertEquals(0, bytes[7]);
            assertEquals(0, bytes[8]);
            assertEquals(0, bytes[9]);
            assertEquals(0, bytes[10]);
            assertEquals(0, bytes[11]);
        }

    }

    public void testNameToWire()
            throws Exception
    {
        Query q = new Query("resare.com");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        q.nameToWire(baos);
        compareArrays(new byte[]{6,'r','e','s','a','r','e',3,'c','o','m',0},baos.toByteArray());
        baos = new ByteArrayOutputStream();
        q.nameToWire(baos);
        compareArrays(new byte[]{6,'r','e','s','a','r','e',3,'c','o','m',0},baos.toByteArray());
    }

    private void compareArrays(byte[] b0, byte[] b1)
    {
        assertEquals(b0.length, b1.length);
        for(int i = 0; i < b0.length; i++) {
            assertEquals("array differs at offset " + i, b0[i], b1[i]);
        }
    }


}
