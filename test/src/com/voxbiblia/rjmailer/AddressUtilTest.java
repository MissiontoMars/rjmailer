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
                AddressUtil.getAddress("Test <another@test.address>"));
        assertEquals("third@test",
                AddressUtil.getAddress("\"Tricky <\" <third@test>"));
        // example from RFC2822 A.1.2
        assertEquals("a@b.c",
                AddressUtil.getAddress("\"Giant; \\\"Big\\\" Box\" <a@b.c>"));
    }

    public void testGetDisplayName()
    {
        assertNull(AddressUtil.getDisplayName("a@b.c"));
        assertEquals("Tor",
                AddressUtil.getDisplayName("\"Tor\" <tor@tor.con>"));
        assertEquals("G; \\\"Big\\\" Box",
                AddressUtil.getDisplayName("\"G; \\\"Big\\\" Box\" <a@b.c>"));
        assertEquals("Harald Blåtand",
                AddressUtil.getDisplayName("\"Harald Blåtand\" <h@b.con>"));
    }

    public void testGetAddresses()
    {
        RJMMessage m = new RJMMessage();
        m.setTo(new String[] {"test0@test.co", "\"Foo Bar\" <test1@test.co>"});
        String[] addresses = AddressUtil.getToAddresses(m);
        List l = Arrays.asList(addresses);
        assertTrue(l.contains("test0@test.co"));
        assertFalse(l.contains("doesnt@exist"));
        assertTrue(l.contains("test1@test.co"));
    }

    public void testGetAddressBcc()
    {
        RJMMessage m = new RJMMessage();
        m.setBcc(new String[] {"test0@test.co", "\"Foo Bar\" <test1@test.co>"});
        String[] addresses = AddressUtil.getToAddresses(m);
        List l = Arrays.asList(addresses);
        assertTrue(l.contains("test0@test.co"));
        assertFalse(l.contains("doesnt@exist"));
        assertTrue(l.contains("test1@test.co"));
    }

    public void testGetDomain()
    {
        assertEquals("foo.bar", AddressUtil.getDomain("foo@foo.bar"));
    }

    public void testEncodeAddressHeader()
    {
        String[] to = new String[] { "a@b.c", "d@e.f"};
        assertEquals("To: a@b.c, d@e.f\r\n",
                AddressUtil.encodeAddressHeader("To", to));
        assertEquals("From: noa@noa.noa\r\n",
                AddressUtil.encodeAddressHeader("From", "noa@noa.noa"));
        assertEquals("To: \"Tester Testson\" <test@test.con>\r\n",
                AddressUtil.encodeAddressHeader("To", 
                        "\"Tester Testson\" <test@test.con>"));
        assertEquals("To: \"=?ISO-8859-1?Q?Harald_Bl=E5tand?=\" <h@b.con>\r\n",
                AddressUtil.encodeAddressHeader("To",
                        "\"Harald Blåtand\" <h@b.con>"));
    }

    public void testEncodeAddressHeaderLong()
    {
        String  to = "\"En person med ett rikitigt riktigt " +
                "långt display name tralla lalla la\" <efraim@ehud.com>";
        String s = AddressUtil.encodeAddressHeader("To", to);
        String[] lines = s.split("\r\n");
        assertTrue("" + lines[0].length(), lines[0].length() < 79);
    }
}
