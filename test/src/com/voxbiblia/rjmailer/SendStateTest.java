package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

import java.util.*;

/**
 * Tests SendState.
 */
public class SendStateTest
    extends TestCase
{
    public void testSimple()
    {
        List emails = new ArrayList();
        emails.add("user0@dom0.con");
        emails.add("user1@dom0.con");
        emails.add("user0@dom1.con");


        Map result = new HashMap();
        result.put("dom0.con", "mx0.dom0.con");
        result.put("dom1.con", new String[] {"mx0.dom1.con", "mx1.dom1.con"});

        ResolverProxy rp = new DummyResolverProxy(result);
        

        SendState ss = new SendState(emails, rp);

        MXData d = ss.next();
        assertEquals("mx0.dom0.con", d.getServer());
        List l = d.getRecipients();
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

        d = ss.next();
        String mx1 = d.getServer();
        assertEquals("mx0.dom1.con", mx1);
        ss.deliveryResult(mx1,(String)d.getRecipients().get(0), 250,
                "message accepted 42");

        l = d.getRecipients();
        assertEquals(1, l.size());
        assertEquals("user0@dom1.con", l.get(0));
        ss.deliveryResult(mx1, "user0@dom1.con", 400, "Could not connect to server");

        d = ss.next();
        mx1 = d.getServer();
        assertEquals("mx1.dom1.con", mx1);
        l = d.getRecipients();
        assertEquals(1, l.size());
        assertEquals("user0@dom1.con", l.get(0));
        ss.deliveryResult(mx1, "user0@dom1.con", 250, "message accepted 44");

        assertNull(ss.next());
        List rs = ss.getResults();
        assertEquals(2, rs.size());
        RJMResult r = (RJMResult)rs.get(0);
        assertEquals("mx0.dom1.con", r.getRecievingServer());
        assertEquals("250 message accepted 42", r.getRecievingServer());

        r = (RJMResult)rs.get(1);
        assertEquals("mx0.dom1.con", r.getRecievingServer());
        assertEquals("250 message accepted 42", r.getRecievingServer());

    }

    public void testMultiSoftFailure()
    {
        List emails = new ArrayList();
        emails.add("user0@dom0.con");
        emails.add("user1@dom0.con");
        emails.add("user0@dom1.con");


        Map result = new HashMap();
        result.put("dom0.con", "mx0.dom0.con");
        result.put("dom1.con", new String[] {"mx0.dom1.con", "mx1.dom1.con"});

        ResolverProxy rp = new DummyResolverProxy(result);

        SendState ms = new SendState(emails, rp);

        MXData d = ms.next();
        assertEquals("mx0.dom0.con", d.getServer());
        List l = d.getRecipients();
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

        d = ms.next();
        String mx = d.getServer();
        assertEquals("mx0.dom1.con", mx);
        ms.deliveryResult(mx, (String)l.get(0), 250, "message accepted");

        l = d.getRecipients();
        assertEquals(1, l.size());
        assertEquals("user0@dom1.con", l.get(0));
        ms.deliveryResult(mx, "user0@dom1.con", 400, "Connection failed");


        d = ms.next();
        mx = d.getServer();
        assertEquals("mx1.dom1.con", mx);
        l = d.getRecipients();
        assertEquals(1, l.size());
        assertEquals("user0@dom1.con", l.get(0));
        ms.deliveryResult(mx, "user0@dom1.con", 400, "Connection failed");

        assertNull(ms.next());
    }

}
