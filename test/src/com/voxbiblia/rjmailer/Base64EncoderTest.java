package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

/**
 * Tests Base64Encoder
 */
public class Base64EncoderTest
    extends TestCase
{
    public void testEncodeBase64()
            throws Exception
    {
    	assertEquals("c3ZhbnNh", Base64Encoder.encode("svansa".getBytes()));
        assertEquals("c3ZhbnNhcg==", Base64Encoder.encode("svansar".getBytes()));
        assertEquals("c3ZhbnNhcnM=", Base64Encoder.encode("svansars".getBytes()));
        assertEquals("D9O/", Base64Encoder.encode(new byte[] {(byte)15, (byte)211, (byte)191}));

    }

    public void testPos()
    {
        assertEquals(42, Base64Encoder.pos((byte)42));
        assertEquals(0, Base64Encoder.pos((byte)0));
        assertEquals(255, Base64Encoder.pos((byte)-1));
        byte b = (byte)201;
        assertTrue(201 != b);
        assertEquals(201, Base64Encoder.pos((byte)201));
    }
}
