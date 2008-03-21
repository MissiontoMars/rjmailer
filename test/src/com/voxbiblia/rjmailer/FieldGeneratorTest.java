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
        String ehloHostname = "test.test";
        FieldGenerator fg = new FieldGenerator(ehloHostname);
        String s = fg.getNextMessgeId();

        String s1 = fg.getMessageId();
        assertEquals(s1, s);
        assertFalse (s1.equals(fg.getMessageId()));
    }


}
