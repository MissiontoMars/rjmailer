package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

/**
 * Tests RJMMessage
 */
public class RJMMessageTest
    extends TestCase
{
    public void testAddTo()
    {
        RJMMessage m = new RJMMessage();
        m.addTo("Greger Lår", "a@b.c");
        String[] to = m.getTo();
        assertEquals(1, to.length);
        assertEquals("\"Greger Lår\" <a@b.c>", to[0]);

        m.addTo("Lillen \"Storis\" Tadam", "d@e.f");
        to = m.getTo();
        assertEquals(2, to.length);
        assertEquals("\"Greger Lår\" <a@b.c>", to[0]);
        assertEquals("\"Lillen \\\"Storis\\\" Tadam\" <d@e.f>", to[1]);        
    }
}
