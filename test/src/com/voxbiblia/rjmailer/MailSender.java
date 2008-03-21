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
        m.setFrom("Noa Resare <noa@resare.com>");
        m.setTo("Noa Resare <noa@resare.com>");
        m.setSubject("lårbenshals");
        m.setText("utan svenska tecken");
        s.send(m);
    }
}
