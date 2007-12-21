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
                "MAIL FROM: <sender@sender.com>", "250 Ok",
                "RCPT TO: <reciever@reciever.com>", "250 Ok",
                "DATA", "354 End data with <CR><LF>.<CR><LF>",
                "From: sender@sender.com\n\nemail data\n.",
                "250 Ok: queued as 62B14FFD8"
        });

        RJMMailMessage rmm = new RJMMailMessage();
        rmm.setFrom("sender@sender.com");
        rmm.setText("email data");
        ch.send(rmm, new String[] {"reciever@reciever.com"}, s);
        assertTrue("more data to read from the server", s.hasFinished());
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
