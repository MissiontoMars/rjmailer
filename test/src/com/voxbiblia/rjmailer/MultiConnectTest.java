package com.voxbiblia.rjmailer;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests the deferring feature on temporary errors or timeouts.
 */
public class MultiConnectTest
    extends TestBase
{
    public void testSimple()
    {
        RJMSender sender = new RJMSender("test.ehlo.host");

        sender.setResolver(new DummyResolver());

    }
}
