package com.voxbiblia.rjmailer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests the deferring feature on temporary errors or timeouts.
 */
public class MultiConnectTest
    extends TestBase
{
    public void testSimple()
    {
        RJMSender sender = new RJMSender("test.ehlo.host");
        DummyResolver dr = new DummyResolver();
        dr.addData("reciever.com", "mx1.example.con", "mx2.example.con");
        sender.setResolver(dr);
        DummySocketFactory dsf = new DummySocketFactory();
        DummySMTPSocket s = new DummySMTPSocket(new String[] {"220 OK",
                "EHLO test.ehlo.host", "250-smtpd.voxbiblia.com\r\n250-VRFY\r\n" +
                    "250 8BITMIME",
                "MAIL FROM: <sender@sender.com>", "250 Ok",
                "RCPT TO: <reciever@reciever.com>", "450 Temporary error."}, null, null);
        dsf.addSocket("mx1.example.con", s);

        Map<String,String> m = new HashMap<String,String>();
        FieldGenerator fg = sender.getConversationHandler().getFieldGenerator();
        m.put("@@MSG_ID@@", fg.getNextMessgeId());
        m.put("@@DATE@@", fg.getNextDate());
        m.put("@@VERSION@@", ConversationHandler.getVersion());

        s =  new DummySMTPSocket(new String[] {"220 OK",
                "EHLO localhost", "250-smtpd.voxbiblia.com\r\n" +
                    "250-VRFY\r\n250 8BITMIME",
                "MAIL FROM: <sender@sender.com>", "250 Ok",
                "RCPT TO: <reciever@reciever.com>", "250 Ok",
                "DATA", "354 End data with <CR><LF>.<CR><LF>",
                "IN_FILE",
                "250 Ok: queued as 62B14FFD8"
        }, new File("test/data/test1.txt"), m);


        dsf.addSocket("mx2.example.con", s);

        sender.setSocketFactory(dsf);

        RJMMessage rmm = new RJMMessage();
        rmm.setFrom("sender@sender.com");
        rmm.setText("email data");
        rmm.setSubject("rågrut");
        rmm.setTo("reciever@reciever.com");
        RJMResult r = sender.send(rmm);
        assertEquals("Ok: queued as 62B14FFD8", r.getResult());
        assertEquals("mx2.example.con", r.getRecievingServer());

    }
}
