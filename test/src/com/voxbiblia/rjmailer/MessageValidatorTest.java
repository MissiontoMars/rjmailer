package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

/**
 * Tests MessageValidator
 */
public class MessageValidatorTest
    extends TestCase
{
    public void testNullMessage()
    {
        try {
            MessageValidator.validate(null);
            fail("Should have thrown exception");
        } catch (RJMException e) {
            assertEquals(ExactCause.INVALID_INPUT, e.getExactCause());
        }
    }

    public void testNoRecipients()
    {
        try {
            RJMMessage m = new RJMMessage();
            MessageValidator.validate(m);
            fail("Should have thrown exception");
        } catch (RJMException e) {
            assertEquals(ExactCause.RECIPIENT_MISSING, e.getExactCause());
        }
    }

    public void testInvalidEmail()
    {
        doTestEmail("\"Gunde Svan\" <>");
        doTestEmail("foobar.svensson");
        doTestEmail("apa bepo@rjmailer.org");
        doTestEmail("malå@adak.de");
        doTestEmail("adak@malå.de");
    }

    private void doTestEmail(String email)
    {
        try {
            RJMMessage m = new RJMMessage();
            m.addTo(email);
            MessageValidator.validate(m);
            fail("Should have thrown exception");
        } catch (RJMException e) {
            assertEquals(ExactCause.ADDRESS_SYNTAX, e.getExactCause());
        }
    }

}
