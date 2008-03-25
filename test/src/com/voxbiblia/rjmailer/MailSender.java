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
        RJMSender s = new RJMSender("johanna.resare.com", "valfrid.resare.com");
        RJMMessage m = new RJMMessage();
        m.setFrom("\"Greger långhalm\" <noa@resare.com>");
        m.setTo("\"Märy Mauri\" <noa@resare.com>");
        m.setSubject("lårbenshals");
        m.setText("Här kommer innehållet, får se om QP kommer till användning");
        System.out.println("result from the server: " + s.send(m));
    }
}
