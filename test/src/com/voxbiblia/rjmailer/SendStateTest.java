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
        Map<String,String[]> m = new HashMap<String,String[]>();
        m.put("domain.con", new String[] {"mx0.domain.con", "mx1.domain.con"});
        DummyResolverProxy drp = new DummyResolverProxy(m);

        SendState ss = new SendState(drp, Collections.singletonList("u@domain.con"));
        MXData mxd = ss.nextMXData();
        assertEquals("mx0.domain.con", mxd.getServer());
        ss.success("u@domain.con", "mx0.domain.con", "Sent as A9F4");
        assertNull(ss.nextMXData());
        Map results = ss.getResults();
        assertEquals(1, results.size());
        RJMResult r = (RJMResult)results.get("u@domain.con");
        assertEquals("mx0.domain.con", r.getRecievingServer());
        assertEquals("Sent as A9F4", r.getResult());
    }

    public void testSoftFailure()
    {
        Map<String, String[]> m = new HashMap<String,String[]>();
        m.put("domain.con", new String[] {"mx0.domain.con", "mx1.domain.con"});
        DummyResolverProxy drp = new DummyResolverProxy(m);

        SendState ss = new SendState(drp, Collections.singletonList("u@domain.con"));
        MXData mxd = ss.nextMXData();
        assertEquals("mx0.domain.con", mxd.getServer());
        ss.softFailure("u@domain.con", "mx0.domain.con", "Timed out");

        mxd = ss.nextMXData();
        assertEquals("mx1.domain.con", mxd.getServer());
        ss.success("u@domain.con", "mx1.domain.con", "Sent as FFEE");

        assertNull(ss.nextMXData());

        Map results = ss.getResults();
        assertEquals(1, results.size());
        RJMResult r = (RJMResult)results.get("u@domain.con");
        assertEquals("mx1.domain.con", r.getRecievingServer());
        assertEquals("Sent as FFEE", r.getResult());
    }

    public void testHardFailure()
    {
        Map<String,String[]> m = new HashMap<String,String[]>();
        m.put("domain.con", new String[] {"mx0.domain.con", "mx1.domain.con"});
        DummyResolverProxy drp = new DummyResolverProxy(m);

        SendState ss = new SendState(drp, Collections.singletonList("u@domain.con"));
        MXData mxd = ss.nextMXData();
        assertEquals("mx0.domain.con", mxd.getServer());
        ss.hardFailure("u@domain.con", "mx0.domain.con", "No such user");

        assertNull(ss.nextMXData());

        Map results = ss.getResults();
        assertEquals(1, results.size());
        SMTPException r = (SMTPException)results.get("u@domain.con");
        assertEquals("mx0.domain.con", r.getServer());
        assertEquals("No such user", r.getMessage());
    }


}
