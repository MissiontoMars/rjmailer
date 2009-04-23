package com.voxbiblia.rjmailer;

import java.util.List;

/**
 * Tests ResolverProxy
 */
public class ResolverProxyTest
    extends TestBase
{

    public void testResolveMX()
    {
        // we use opendns.com servers, they can handle some extra requests.
        ResolverImpl rp = new ResolverImpl("208.67.222.222", 1);
        List l = rp.resolveMX("mxtest.voxbiblia.com");
        assertEquals(2, l.size());
        assertEquals("adam.voxbiblia.com", l.get(0));
        assertEquals("eve.voxbiblia.com", l.get(1));
    }

    public void testResolveDomainExistsNoMX()
    {
        // we use opendns.com servers, they can handle some extra requests.
        ResolverImpl rp = new ResolverImpl("208.67.222.222", 1);
        try {
            rp.resolveMX("www.voxbiblia.com");
            fail("should have thrown RJMException");
        } catch (RJMException e) {
            assertEquals(e.getExactCause(), ExactCause.DOMAIN_INVALID);
        }

    }
}
