package com.voxbiblia.rjmailer;

import java.util.List;

/**
 * Tests RJMMessage
 */
public class RJMMessageTest
    extends TestBase
{
    public void testAddTo()
    {
        RJMMessage m = new RJMMessage();
        m.addTo("Greger Lår", "a@b.c");
        List<String> to = m.getTo();
        assertEquals(1, to.size());
        assertEquals("\"Greger Lår\" <a@b.c>", to.get(0));

        m.addTo("Lillen \"Storis\" Tadam", "d@e.f");
        to = m.getTo();
        assertEquals(2, to.size());
        assertEquals("\"Greger Lår\" <a@b.c>", to.get(0));
        assertEquals("\"Lillen \\\"Storis\\\" Tadam\" <d@e.f>", to.get(1));        
    }
}
