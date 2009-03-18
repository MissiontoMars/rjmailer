package com.voxbiblia.rjmailer;

import java.io.IOException;

/**
 * A simple program that sends an email.
 */
public class MailSender
{
    public static void main(String[] args)
            throws IOException
    {
        RJMSender s = new RJMSender("valfrid.resare.com");
        s.setNameServer("208.67.222.222");
        RJMMessage m = new RJMMessage();
        m.setFrom("\"Greger långhalm\" <noa@resare.com>");
        m.setTo("\"Märy Mauri\" <noa@www.resare.com>");
        m.setSubject("lårbenshals");
        m.setText("Här kommer innehållet, får se om QP kommer till användning");
        RJMResult r = s.send(m);
        System.out.println("result from the server: " + r.getResult());
        System.out.println("connected to : " + r.getRecievingServer());
    }
}
