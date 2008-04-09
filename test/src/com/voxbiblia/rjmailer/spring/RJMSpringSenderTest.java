package com.voxbiblia.rjmailer.spring;

import junit.framework.TestCase;
import com.voxbiblia.rjmailer.RJMMessage;
import org.springframework.mail.SimpleMailMessage;

/**
 *
 */
public class RJMSpringSenderTest
    extends TestCase
{
    public void testConvertSimpleMessage()
    {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setTo("\"Alfred Snorboll\" <helf@hilf.con>");
        m.setSubject("Testing testing, all systems");
        m.setText("This is the body");
        m.setBcc("a@b.c");
        m.setCc("d@e.f");
        m.setFrom("per@albin.con");
        m.setReplyTo("greger@greger.con");
        RJMMessage rjmm = RJMSpringSender.convertSimpleMessage(m);
        assertEquals("Testing testing, all systems", rjmm.getSubject());
        assertEquals("a@b.c", rjmm.getBcc()[0]);
        assertEquals(1, rjmm.getBcc().length);
        assertEquals("d@e.f", rjmm.getCc()[0]);
        assertEquals(1, rjmm.getCc().length);
        assertEquals("per@albin.con", rjmm.getFrom());
        assertEquals("greger@greger.con", rjmm.getReplyTo());
    }
}
