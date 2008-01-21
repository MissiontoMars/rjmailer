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
    }
}


