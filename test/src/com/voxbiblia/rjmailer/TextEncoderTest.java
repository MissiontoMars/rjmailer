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

    public void testCanonicalize()
    {
        assertEquals("before\r\nafter", TextEncoder.canonicalize("before\nafter"));
        assertEquals("before\r\nafter\r\nanother", TextEncoder.canonicalize("before\nafter\ranother"));
        assertEquals("double\r\n\r\nnewlines", TextEncoder.canonicalize("double\n\nnewlines"));
        assertEquals("double\r\n\r\nnewlines", TextEncoder.canonicalize("double\r\rnewlines"));
        assertEquals("double\r\n\r\nnewlines", TextEncoder.canonicalize("double\r\n\rnewlines"));
    }
    
    public void testEncodeBase64()
            throws Exception
    {
    	assertEquals("c3ZhbnNh", TextEncoder.encodeBase64("svansa".getBytes()));
        assertEquals("c3ZhbnNhcg==", TextEncoder.encodeBase64("svansar".getBytes()));
        assertEquals("c3ZhbnNhcnM=", TextEncoder.encodeBase64("svansars".getBytes()));
        assertEquals("D9O/", TextEncoder.encodeBase64(new byte[] {(byte)15, (byte)211, (byte)191}));

    }

/*
    public void testDebugRunner()
    {


        assertEquals("D9O/", new String(AlternateBase64Encoder.encode(new byte[] {(byte)15, (byte)211, (byte)191})));
        assertEquals("D9O/", TextEncoder.encodeBase64(new byte[] {(byte)15, (byte)211, (byte)191}));
        assertEquals("c3ZhbnNhcnM=", new String(AlternateBase64Encoder.encode("svansars".getBytes())));
        assertEquals("c3ZhbnNhcnM=", TextEncoder.encodeBase64("svansars".getBytes()));

    }
        */
}


