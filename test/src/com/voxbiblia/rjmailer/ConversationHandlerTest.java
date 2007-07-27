package com.voxbiblia.rjmailer;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;

/**
 * Tests ConversationHandler 
 */
public class ConversationHandlerTest
    extends TestCase
{
    public void testSend()
            throws Exception            
    {
        ConversationHandler ch = new ConversationHandler("localhost");
        DummySMTPSocket s = new DummySMTPSocket(new String[] {"220 OK",
                "EHLO localhost", "250-smtpd.voxbiblia.com\r\n250-VRFY\r\n250 8BITMIME",
                "MAIL FROM: <noa@resare.com>"
        });
        ch.send(null, s);
    }

    public void testGetStatus()
    {
        assertEquals(100, ConversationHandler.getStatus("100 Hello"));
        try {
            ConversationHandler.getStatus("foo");
            fail();
        } catch (NumberFormatException e) {
            // exception expected
        }

    }

    public void testCheckStatus()
            throws Exception
    {
        byte[] resp = "220 OK\r\nX".getBytes("US-ASCII");

        ByteArrayInputStream bais = new ByteArrayInputStream(resp);
        ConversationHandler.checkStatus(bais, new byte[100], 220);
        assertEquals('X', bais.read());

        resp = "250-FIRST\r\n250 second\r\nX".getBytes("US-ASCII");
        bais = new ByteArrayInputStream(resp);
        ConversationHandler.checkStatus(bais, new byte[100], 250);
        assertEquals('X', bais.read());
    }
}
