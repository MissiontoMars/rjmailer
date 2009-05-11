package com.voxbiblia.rjmailer;

import java.io.File;
import java.util.HashMap;
import java.util.List;
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
        String ehloHost = "localhost";
        SMTPConversation ch = new SMTPConversation(ehloHost,"host");
        Map<String,String> m = new HashMap<String,String>();

        FieldGenerator fg = new FieldGenerator(ehloHost);
        ch.setFieldGenerator(fg);
        m.put("@@MSG_ID@@", fg.getNextMessgeId());
        m.put("@@DATE@@", fg.getNextDate());
        m.put("@@VERSION@@", SMTPConversation.getVersion());

        DummySMTPSocket s =  new DummySMTPSocket(new String[] {"220 OK",
                "EHLO localhost", "250-smtpd.voxbiblia.com\r\n" +
                    "250-VRFY\r\n250 8BITMIME",
                "MAIL FROM: <sender@sender.com>", "250 Ok",
                "RCPT TO: <reciever@reciever.com>", "250 Ok",
                "DATA", "354 End data with <CR><LF>.<CR><LF>",
                "IN_FILE",
                "250 Ok: queued as 62B14FFD8"
        }, new File("test/data/test1.txt"), m);
        DummySocketFactory sf = new DummySocketFactory(s);
        ch.setSocketFactory(sf);



        RJMMessage rmm = new RJMMessage();
        rmm.setFrom("sender@sender.com");
        rmm.setText("email data");
        rmm.setSubject("rågrut");
        rmm.setTo("reciever@reciever.com");
        List<String> to = AddressUtil.getToAddresses(rmm);
        SendState ss = new SendState(new DummyResolver(), to);
        ch.sendMail(rmm, to, ss);
        Map<String,SendResult> results = ss.getResults();
        RJMResult r = (RJMResult)results.get(to.get(0));
        assertEquals("Ok: queued as 62B14FFD8",r.getResult());
        assertTrue("more data to read from the server", s.hasFinished());
    }

    public void testSendBodyError()
            throws Exception
    {
        SMTPConversation ch = new SMTPConversation("localhost", "host");
        DummySMTPSocket s = new DummySMTPSocket(new String[] {"220 OK",
                "EHLO localhost", "250-smtpd.voxbiblia.com\r\n250-VRFY\r\n" +
                    "250 8BITMIME",
                "MAIL FROM: <sender@sender.com>", "250 Ok",
                "RCPT TO: <reciever@reciever.com>", "250 Ok",
                "DATA", "354 End data with <CR><LF>.<CR><LF>",
                "IN_FILE",
                "250 Ok: queued as 62B14FFD8"
        }, new File("test/data/test1.txt"));
        DummySocketFactory dsf = new DummySocketFactory(s);
        ch.setSocketFactory(dsf);

        RJMMessage rmm = new RJMMessage();
        rmm.setFrom("sender@sender.com");
        rmm.setText("email dataa");
        rmm.setSubject("rågrut");

        List<String> to = AddressUtil.getToAddresses(rmm);

        try {
            ch.sendMail(rmm, to, new SendState(new DummyResolver(), to));
            fail("should have thrown IAE");
        } catch (IllegalArgumentException e) {
            // ignore
        }

    }

    public void testSend2()
            throws Exception
    {
        String ehloHost = "localhost";
        SMTPConversation ch = new SMTPConversation(ehloHost, "ignored");
        Map<String,String> m = new HashMap<String,String>();
        FieldGenerator fg = new FieldGenerator(ehloHost);
        ch.setFieldGenerator(fg);
        m.put("@@MSG_ID@@", fg.getNextMessgeId());
        m.put("@@DATE@@", fg.getNextDate());
        m.put("@@VERSION@@", SMTPConversation.getVersion());
        DummySMTPSocket s = new DummySMTPSocket(new String[] {"220 OK",
                "EHLO localhost", "250-smtpd.voxbiblia.com\r\n250-VRFY\r\n" +
                    "250 8BITMIME",
                "MAIL FROM: <sender@sender.com>", "250 Ok",
                "RCPT TO: <reciever@reciever.com>", "250 Ok",
                "DATA", "354 End data with <CR><LF>.<CR><LF>",
                "IN_FILE",
                "250 Ok: queued as 62B15FFD8"
        }, new File("test/data/test2.txt"), m);
        DummySocketFactory dsf = new DummySocketFactory(s);
        ch.setSocketFactory(dsf);
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


        List<String> to = AddressUtil.getToAddresses(rmm);
        SendState ss = new SendState(new DummyResolver(), to);
        ch.sendMail(rmm, to, ss);

        Map<String,SendResult> results = ss.getResults();
        RJMResult r = (RJMResult)results.get(to.get(0));
        assertEquals("Ok: queued as 62B14FFD8",r.getResult());
        assertTrue("more data to read from the server", s.hasFinished());
    }

    public void testGetStatus()
    {
        assertEquals(100, SMTPConversation.getStatus("100 Hello"));
        try {
            SMTPConversation.getStatus("foo");
            fail();
        } catch (NumberFormatException e) {
            // exception expected
        }

    }

    public void testGetVersion()
    {
        assertNotNull(SMTPConversation.getVersion());
    }
}
