package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

import java.util.List;
import java.util.Arrays;

/**
 * Tests AddressUtil
 */
public class AddressUtilTest
        extends TestCase
{
    public void testGetAddress()
    {
        assertEquals("foo@foo.bar", AddressUtil.getAddress("foo@foo.bar"));
        assertEquals("another@test.address",
                AddressUtil.getAddress("Test test <another@test.address>"));
        assertEquals("third@test",
                AddressUtil.getAddress("\"Tricky <\" <third@test>"));
        // example from RFC2822 A.1.2
        assertEquals("a@b.c",
                AddressUtil.getAddress("\"Giant; \\\"Big\\\" Box\" <a@b.c>"));
    }

    public void testGetAddresses()
    {
        RJMMessage m = new RJMMessage();
        m.setTo(new String[] {"test0@test.com", "\"Foo Bar\" <test1@test.com>"});
        String[] addresses = AddressUtil.getToAddresses(m);
        List l = Arrays.asList(addresses);
        assertTrue(l.contains("test0@test.com"));
        assertFalse(l.contains("doesnt@exist"));
        assertTrue(l.contains("test1@test.com"));
    }

    public void testGetAddressBcc()
    {
        RJMMessage m = new RJMMessage();
        m.setBcc(new String[] {"test0@test.com", "\"Foo Bar\" <test1@test.com>"});
        String[] addresses = AddressUtil.getToAddresses(m);
        List l = Arrays.asList(addresses);
        assertTrue(l.contains("test0@test.com"));
        assertFalse(l.contains("doesnt@exist"));
        assertTrue(l.contains("test1@test.com"));
    }

    public void testGetDomain()
    {
        assertEquals("foo.bar", AddressUtil.getDomain("foo@foo.bar"));
    }
}
