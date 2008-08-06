package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

import java.util.List;

/**
 * Tests ResolverProxy
 */
public class ResolverProxyTest
    extends TestCase
{

    public void testHasJresolver()
    {
        assertTrue(ResolverProxy.hasJresolver());
    }

    public void testResolveMX()
    {
        // we use opendns.com servers, they can handle some extra requests.
        ResolverProxy rp = new ResolverProxy("208.67.222.222");
        List l = rp.resolveMX("mxtest.voxbiblia.com");
        assertEquals(2, l.size());
        assertEquals("adam.voxbiblia.com", l.get(0));
        assertEquals("eve.voxbiblia.com", l.get(1));
    }
}
