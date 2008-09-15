package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

import java.util.*;

/**
 * Tests MessageStatus.
 */
public class MessageStatusTest
    extends TestCase
{
    public void testSimple()
    {
        List emails = new ArrayList();
        emails.add("user0@dom0.con");
        emails.add("user1@dom0.con");
        emails.add("user0@dom1.con");
        MessageStatus ms = new MessageStatus();

        Map result = new HashMap();
        result.put("dom0.con", "mx0.dom0.con");
        result.put("dom1.con", new String[] {"mx0.dom1.con", "mx1.dom1.con"});

        ResolverProxy rp = new DummyResolverProxy(result);
        

        ms.setResolverProxy(rp);
        ms.addRecipients(emails);

        String mx0 = ms.getNextMX();
        assertEquals("mx0.dom0.con", mx0);
        List l = ms.getMXRecipients(mx0);
        assertEquals(2, l.size());
        Iterator li = l.iterator();

        boolean hasUser0 = false;
        boolean hasUser1 = false;

        while (li.hasNext()) {
            String e = (String)li.next();
            if (e.equals("user0@dom0.con")) {
                hasUser0 = true;
            } else if (e.equals("user1@dom0.con")) {
                hasUser1 = true;
            } else {
                fail("unknown email: " + e);
            }
        }
        assertTrue(hasUser0);
        assertTrue(hasUser1);

        String mx1 = ms.getNextMX();
        assertEquals("mx0.dom1.con", mx1);
        ms.success(l, "250 message accepted", mx1);

        l = ms.getMXRecipients(mx1);
        assertEquals(1, l.size());
        assertEquals("user0@dom1.con", l.get(0));
        ms.softRecipientFailure("user0@dom1.con", mx1);

        mx1 = ms.getNextMX();
        assertEquals("mx1.dom1.con", mx1);
        l = ms.getMXRecipients(mx1);
        assertEquals(1, l.size());
        assertEquals("user0@dom1.con", l.get(0));

        assertNull(ms.getNextMX());

    }

    public void testMultiSoftFailure()
    {
        List emails = new ArrayList();
        emails.add("user0@dom0.con");
        emails.add("user1@dom0.con");
        emails.add("user0@dom1.con");
        MessageStatus ms = new MessageStatus();

        Map result = new HashMap();
        result.put("dom0.con", "mx0.dom0.con");
        result.put("dom1.con", new String[] {"mx0.dom1.con", "mx1.dom1.con"});

        ResolverProxy rp = new DummyResolverProxy(result);


        ms.setResolverProxy(rp);
        ms.addRecipients(emails);

        String mx = ms.getNextMX();
        assertEquals("mx0.dom0.con", mx);
        List l = ms.getMXRecipients(mx);
        assertEquals(2, l.size());
        Iterator li = l.iterator();

        boolean hasUser0 = false;
        boolean hasUser1 = false;

        while (li.hasNext()) {
            String e = (String)li.next();
            if (e.equals("user0@dom0.con")) {
                hasUser0 = true;
            } else if (e.equals("user1@dom0.con")) {
                hasUser1 = true;
            } else {
                fail("unknown email: " + e);
            }
        }
        assertTrue(hasUser0);
        assertTrue(hasUser1);

        mx = ms.getNextMX();
        assertEquals("mx0.dom1.con", mx);
        ms.success(l, "250 message accepted", mx);

        l = ms.getMXRecipients(mx);
        assertEquals(1, l.size());
        assertEquals("user0@dom1.con", l.get(0));
        ms.softRecipientFailure("user0@dom1.con", mx);

        mx = ms.getNextMX();
        assertEquals("mx1.dom1.con", mx);
        l = ms.getMXRecipients(mx);
        assertEquals(1, l.size());
        assertEquals("user0@dom1.con", l.get(0));
        ms.softRecipientFailure("user0@dom1.con", mx);

        assertNull(ms.getNextMX());

    }

}
