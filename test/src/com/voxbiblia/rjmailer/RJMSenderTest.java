package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Tests RJMSender.
 */
public class RJMSenderTest
    extends TestCase
{
    public void testMakeMXMap()
    {
        RJMSender s = new RJMSender("ehloName");
        Map m = new HashMap();
        m.put("a.con", "mx.a.con");
        m.put("b.con", "mx.b.con");
        s.setResolverProxy(new DummyResolverProxy(m));
        m = s.makeMXMap(new String[] {"meep@a.con", "meep@b.con"});

        assertEquals(2, m.size());
        Iterator i = m.keySet().iterator();
        boolean hasA = false, hasB = false;
        while (i.hasNext()) {
            String mx = (String)i.next();
            if (mx.equals("mx.a.con")) {
                hasA = true;
                List l = (List)m.get(mx);
                assertEquals(1, l.size());
                assertEquals("meep@a.con", l.get(0));
            } else if (mx.equals("mx.b.con")) {
                hasB = true;
                List l = (List)m.get(mx);
                assertEquals(1, l.size());
                assertEquals("meep@b.con", l.get(0));
            }
        }
        assertTrue(hasA);
        assertTrue(hasB);
    }

    public void testMakeMXMapSingle()
    {
        RJMSender s = new RJMSender("ehloName");
        Map m = new HashMap();
        m.put("a.con", "mx.a.con");
        s.setResolverProxy(new DummyResolverProxy(m));
        m = s.makeMXMap(new String[] {"meep@a.con"});

        assertEquals(1, m.size());
        List l = (List)m.get("mx.a.con");
        assertEquals("meep@a.con", l.get(0));
    }

    public void testSendWithMulti()
    {
        RJMMessage m = new RJMMessage();
        m.addTo("a@b.c", "Greger Lundholm");
        m.addTo("d@e.f", "Meepy Stone");
        RJMSender s = new RJMSender("ehloName");
        s.setSmtpServer("localhost");
        try {
            s.send(m);
            fail("send should not accept multiple recipients");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

}
