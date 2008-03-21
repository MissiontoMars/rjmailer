package com.voxbiblia.rjmailer;

import junit.framework.TestCase;


/**
 * Tests FieldGenerator
 */
public class FieldGeneratorTest
    extends TestCase
{
    public void testGetMessageId()
    {
        String ehloHostname = "test.test";
        FieldGenerator fg = new FieldGenerator(ehloHostname);
        String s = fg.getMessageId();

        assertTrue(s.endsWith(ehloHostname));
        String s1 = fg.getMessageId();
        assertFalse(s.equals(s1));
    }

    public void testGetNextMessageId()
    {
        FieldGenerator fg = new FieldGenerator("test.test");
        String s = fg.getNextMessgeId();

        String s1 = fg.getMessageId();
        assertEquals(s1, s);
        assertFalse (s1.equals(fg.getMessageId()));
    }

    public void testGetDate()
    {
        FieldGenerator fg = new FieldGenerator("test.test");
        assertNotNull(fg.getDate());
    }

    public void testGetNextDate()
    {
        FieldGenerator fg = new FieldGenerator("test.test");
        String s = fg.getNextDate();
        assertEquals(s, fg.getDate());
        try {
            Thread.sleep(1500L);
        } catch (InterruptedException e) {
            throw new Error(e);
        }
        assertFalse(s.equals(fg.getDate()));
    }
}
