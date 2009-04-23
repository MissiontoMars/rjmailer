package com.voxbiblia.rjmailer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

        List<String> tos = new ArrayList<String>();
        tos.add("u0@domain.con");
        tos.add("u1@domain.con");

        SendState ss = new SendState(drp, tos);
        MXData mxd = ss.nextMXData();
        assertEquals("mx0.domain.con", mxd.getServer());
        ss.softFailure("u0@domain.con",
                new RJMException(ExactCause.SMTP_UNEXPECTED_STATUS,
                        "Some temporary error").setServer("mx0.domain.con"));
        ss.success("u1@domain.con", "mx0.domain.con", "Sent as F0F0");

        mxd = ss.nextMXData();
        List<String> recipients = mxd.getRecipients();
        assertEquals(1, recipients.size());

        assertEquals("mx1.domain.con", mxd.getServer());
        ss.success("u0@domain.con", "mx1.domain.con", "Sent as FFEE");


        assertNull(ss.nextMXData());

        Map results = ss.getResults();

        assertEquals(2, results.size());
        RJMResult r = (RJMResult)results.get("u1@domain.con");
        assertEquals("mx0.domain.con", r.getRecievingServer());
        assertEquals("Sent as F0F0", r.getResult());

        r = (RJMResult)results.get("u0@domain.con");
        assertEquals("mx1.domain.con", r.getRecievingServer());
        assertEquals("Sent as FFEE", r.getResult());
        List<RJMException> l =  r.getSoftFailures();
        assertEquals(1, l.size());
        
    }

    public void testSoftGeneralFailure()
    {
        DummyResolver drp = new DummyResolver();
        drp.addData("domain.con", "mx0.domain.con", "mx1.domain.con");

        List<String> tos = new ArrayList<String>();
        tos.add("u0@domain.con");
        tos.add("u1@domain.con");

        SendState ss = new SendState(drp, tos);
        MXData mxd = ss.nextMXData();
        assertEquals("mx0.domain.con", mxd.getServer());
        ss.softFailure("u0@domain.con", new RJMException(ExactCause.SMTP_UNEXPECTED_STATUS,
                "Some soft failure"));
        ss.softFailure("u1@domain.con", new RJMException(ExactCause.SMTP_UNEXPECTED_STATUS,
                "Some soft failure"));

        mxd = ss.nextMXData();
        assertEquals("mx1.domain.con", mxd.getServer());
        List<String> recipients = mxd.getRecipients();
        assertEquals(2, recipients.size());
        

        ss.success("u0@domain.con", "mx1.domain.con", "Sent as FFEE");
        ss.softFailure("u1@domain.con", new RJMException(ExactCause.SMTP_UNEXPECTED_STATUS,
                "Another soft failure"));

        assertNull(ss.nextMXData());

        Map results = ss.getResults();
        assertEquals(2, results.size());
        RJMResult r = (RJMResult)results.get("u0@domain.con");
        assertEquals("mx1.domain.con", r.getRecievingServer());
        assertEquals("Sent as FFEE", r.getResult());
        RJMException e = (RJMException)results.get("u1@domain.con");
        List softFailures = e.getSoftFailures();
        assertEquals(2, softFailures.size());
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
        assertEquals("No such user", r.getServerLine());
    }


}
