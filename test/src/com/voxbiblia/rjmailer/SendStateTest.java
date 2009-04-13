package com.voxbiblia.rjmailer;

import java.util.Collections;
import java.util.Map;

/**
 * Tests SendState.
 */
public class SendStateTest
    extends TestBase
{
    public void testSimple()
    {
        DummyResolver drp = new DummyResolver();
        drp.addData("domain.con", "mx0.domain.con", "mx1.domain.con");

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
        DummyResolver drp = new DummyResolver();
        drp.addData("domain.con", "mx0.domain.con", "mx1.domain.con");

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
        DummyResolver drp = new DummyResolver();
        drp.addData("domain.con", "mx0.domain.con", "mx1.domain.con");


        SendState ss = new SendState(drp, Collections.singletonList("u@domain.con"));
        MXData mxd = ss.nextMXData();
        assertEquals("mx0.domain.con", mxd.getServer());
        ss.hardFailure("u@domain.con", "mx0.domain.con", "No such user");

        assertNull(ss.nextMXData());

        Map results = ss.getResults();
        assertEquals(1, results.size());
        RJMException r = (RJMException)results.get("u@domain.con");
        assertEquals("mx0.domain.con", r.getServer());
        assertEquals("No such user", r.getMessage());
    }


}
