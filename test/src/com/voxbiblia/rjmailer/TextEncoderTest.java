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
        assertEquals("before\r\nafter",
                TextEncoder.canonicalize("before\nafter"));
        assertEquals("before\r\nafter\r\nanother",
                TextEncoder.canonicalize("before\nafter\ranother"));
        assertEquals("double\r\n\r\nnewlines",
                TextEncoder.canonicalize("double\n\nnewlines"));
        assertEquals("double\r\n\r\nnewlines",
                TextEncoder.canonicalize("double\r\rnewlines"));
        assertEquals("double\r\n\r\nnewlines",
                TextEncoder.canonicalize("double\r\n\rnewlines"));
    }
    

    public void testEncodeHeaderWord()
    {
        assertEquals("=?ISO-8859-1?B?5eT2xcTW?=", TextEncoder.encodeHeaderWord("åäöÅÄÖ"));
        assertEquals("=?ISO-8859-1?Q?Gr=F6t?=", TextEncoder.encodeHeaderWord("Gröt"));
        assertEquals("=?ISO-8859-1?Q?Egon_sover_l=E4nge?=",
                TextEncoder.encodeHeaderWord("Egon sover länge"));
        assertEquals("=?UTF-8?Q?Lots_of_=E2=82=AC!?=",
                TextEncoder.encodeHeaderWord("Lots of €!"));
    }


    public void testEncodeHeader()
    {
        assertEquals("Subject: subjekt\r\n", TextEncoder.encodeHeader("Subject", "subjekt"));
        try {
            TextEncoder.encodeHeader("Subject with colon:", "");
            fail("should have thrown illegal argument exception");
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }

    public void testGetNonAsciiPercentage()
    {
        assertEquals(0, TextEncoder.getNonAsciiPercentage("svenska tecken"));
        assertEquals(25, TextEncoder.getNonAsciiPercentage("låda"));
        assertEquals(100, TextEncoder.getNonAsciiPercentage("åäöÅÄÖ"));
    }
}


