package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

/**
 * Tests AddressUtil
 */
public class AddressUtilTest
        extends TestCase
{
    public void testGetAddress()
    {
        assertEquals("foo@foo.bar", AddressUtil.getAddress("foo@foo.bar"));
        assertEquals("another@test.address", AddressUtil.getAddress("Test test <another@test.address>"));
        assertEquals("third@test", AddressUtil.getAddress("\"Tricky <\" <third@test>"));
        // example from RFC2822 A.1.2
        assertEquals("a@b.c", AddressUtil.getAddress("\"Giant; \\\"Big\\\" Box\" <a@b.c>"));
    }

    public void testGetDomain()
    {
        assertEquals("foo.bar", AddressUtil.getDomain("foo@foo.bar"));
    }
}
