package com.voxbiblia.rjmailer.dns;

import junit.framework.TestCase;

import java.util.List;

/**
 * Tests the resend implementation.
 */
public class ResendTest
    extends TestCase
{
    public void testResend()
    {
        Resolver r = new Resolver(new DummyTransportService());
        List l = r.resolve(new MXQuery("svd.se"));
        assertNotNull(l);
    }

    public void testWithDrop()
    {
        DummyTransportService dts = new DummyTransportService();
        dts.setMode(DummyTransportService.DROP_FIRST);

        Resolver r = new Resolver(dts);
        List l = r.resolve(new MXQuery("svd.se"));
        assertNotNull(l);
        
    }
}
