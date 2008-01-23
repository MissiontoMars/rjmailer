package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

/**
 * Tests TextEncoder
 */
public class TextEncoderTest
        extends TestCase
{

    public void testEncode()
            throws Exception
    {
        assertEquals("foo", TextEncoder.encodeQP("foo", "ISO-8859-1"));
        assertEquals("l=E5da", TextEncoder.encodeQP("låda", "ISO-8859-1"));
        assertEquals("a=3Db", TextEncoder.encodeQP("a=b", "ISO-8859-1"));
        assertEquals("=E2=82=AC", TextEncoder.encodeQP("€", "UTF-8"));
    }

    public void testQPLongLine()
            throws Exception
    {
        String s80chars = "12345678901234567890123456789012345678901234567890" +
                "123456789012345678901234567890";
        assertEquals(80, s80chars.length());
        String s = TextEncoder.encodeQP(s80chars, "UTF-8");
        int index = s.indexOf("\r\n");
        assertTrue("Long QP encoded lines must be broken into shorter lines",
                index != -1);
        assertTrue("Longer than 76 chars is not allowed", index < 76);

    }
}


