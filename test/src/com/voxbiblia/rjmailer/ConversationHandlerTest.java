package com.voxbiblia.rjmailer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests ConversationHandler 
 */
public class ConversationHandlerTest
    extends TestBase
{
    public void testSend()
            throws Exception            
    {
        ConversationHandler ch = new ConversationHandler("localhost");
        Map<String,String> m = new HashMap<String,String>();
        m.put("@@MSG_ID@@", ch.fieldGenerator.getNextMessgeId());
        m.put("@@DATE@@", ch.fieldGenerator.getNextDate());
        m.put("@@VERSION@@", ConversationHandler.getVersion());
        DummySMTPSocket s = new DummySMTPSocket(new String[] {"220 OK",
                "EHLO localhost", "250-smtpd.voxbiblia.com\r\n" +
                    "250-VRFY\r\n250 8BITMIME",
                "MAIL FROM: <sender@sender.com>", "250 Ok",
                "RCPT TO: <reciever@reciever.com>", "250 Ok",
                "DATA", "354 End data with <CR><LF>.<CR><LF>",
                "IN_FILE",
                "250 Ok: queued as 62B14FFD8"
        }, new File("test/data/test1.txt"), m);

        RJMMessage rmm = new RJMMessage();
        rmm.setFrom("sender@sender.com");
        rmm.setText("email data");
        rmm.setSubject("rågrut");
        rmm.setTo("reciever@reciever.com");
        assertEquals("Ok: queued as 62B14FFD8", ch.send(rmm,
                Collections.singletonList("reciever@reciever.com"), s));
        assertTrue("more data to read from the server", s.hasFinished());
    }

    public void testSendBodyError()
            throws Exception
    {
        ConversationHandler ch = new ConversationHandler("localhost");
        DummySMTPSocket s = new DummySMTPSocket(new String[] {"220 OK",
                "EHLO localhost", "250-smtpd.voxbiblia.com\r\n250-VRFY\r\n" +
                    "250 8BITMIME",
                "MAIL FROM: <sender@sender.com>", "250 Ok",
                "RCPT TO: <reciever@reciever.com>", "250 Ok",
                "DATA", "354 End data with <CR><LF>.<CR><LF>",
                "IN_FILE",
                "250 Ok: queued as 62B14FFD8"
        }, new File("test/data/test1.txt"));

        RJMMessage rmm = new RJMMessage();
        rmm.setFrom("sender@sender.com");
        rmm.setText("email dataa");
        rmm.setSubject("rågrut");
        try {
            assertEquals("Ok: queued as 62B14FFD8", ch.send(rmm,
                    Collections.singletonList("reciever@reciever.com"), s));
            fail("should have thrown IAE");
        } catch (IllegalArgumentException e) {
            // ignore
        }

    }

    public void testSend2()
            throws Exception
    {
        ConversationHandler ch = new ConversationHandler("localhost");
        Map<String,String> m = new HashMap<String,String>();
        m.put("@@MSG_ID@@", ch.fieldGenerator.getNextMessgeId());
        m.put("@@DATE@@", ch.fieldGenerator.getNextDate());
        m.put("@@VERSION@@", ConversationHandler.getVersion());
        DummySMTPSocket s = new DummySMTPSocket(new String[] {"220 OK",
                "EHLO localhost", "250-smtpd.voxbiblia.com\r\n250-VRFY\r\n" +
                    "250 8BITMIME",
                "MAIL FROM: <sender@sender.com>", "250 Ok",
                "RCPT TO: <reciever@reciever.com>", "250 Ok",
                "DATA", "354 End data with <CR><LF>.<CR><LF>",
                "IN_FILE",
                "250 Ok: queued as 62B15FFD8"
        }, new File("test/data/test2.txt"), m);
        RJMMessage rmm = new RJMMessage();
        rmm.setFrom("sender@sender.com");
        rmm.setTo("\"The Receiver\" <the@receiver.co>");

        // contains space at end of line
        rmm.setText("BWO är ett band som består av tre stycken äggmökar, \n" +
                "varav en blondlockig filur är frontkille och händelsevis\n" +
                "sångare. Det är ju bra, eftersom man knappast får några\n" +
                "tonårstjejsbeundrare genom att ställa fram den lederhosen\n" +
                    "prydda toalettborsten Alexander Bardval vid micken.\n" +
                "Tyvärr kan denna blondlockiga äggmök inte ENGELSKA!!!\n" +
                "VILKET ÄR ETT PRÅBLÄM NÄR MAN FÖRSÖKER SJUNGA PÅ ENGELSKA!\n" +
                // a really long line, requiring a soft linebreak
                "Uttalet påminner om.... åh, vilket sammanträffande... en " +
                "svensk som försöker prata engelska! Dessutom är låten\n" +
                "\"uill (ja, UILL) maj arms by strånginuff to....\" ett\n" +
                "sällan skådat haveri i paraplegisk tysk marschtakt\n" +
                "kombinerat med isande yl från en sk kör och något som\n" +
                "jag antar ska vara romantik. Det låter som en BEGRAVNING!\n" +
                "Det låter som om Frankenstein har DÖDAT SIN BRUD och ska\n" +
                "klättra upp på Empire State Building för att offra henne\n" +
                "till Zeus tillsammans med KING KONG!!");
        rmm.setSubject("Harry Bellafånte har långa ord men inte så långa " +
                "att det räcker.");

        assertEquals("Ok: queued as 62B15FFD8", ch.send(rmm,
                Collections.singletonList("reciever@reciever.com"), s));
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

    public void testGetVersion()
    {
        assertNotNull(ConversationHandler.getVersion());
    }
}
