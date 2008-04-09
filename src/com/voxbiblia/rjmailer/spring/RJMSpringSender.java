package com.voxbiblia.rjmailer.spring;

import com.voxbiblia.rjmailer.RJMMessage;
import com.voxbiblia.rjmailer.RJMSender;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Implementation of Spring framework's MailSender interface using RJMailer.
 */
public class RJMSpringSender
    extends RJMSender
    implements MailSender
{
    /**
     * Constructs a new RJMSpringSender connecting to the given server with the
     * given ehloHostname.
     *
     * @param server the hostname (or ip number) of the SMTP server used to
     * send email.
     * @param ehloHostname the identification the client gives when connecting
     * to the server 
     */
    public RJMSpringSender(String server, String ehloHostname)
    {
        super(server, ehloHostname);
    }

    public void send(SimpleMailMessage simpleMailMessage) throws MailException
    {
        send(convertSimpleMessage(simpleMailMessage));
    }

    public void send(SimpleMailMessage[] simpleMailMessages) throws MailException
    {
        for (int i = 0; i < simpleMailMessages.length; i++) {
            send(simpleMailMessages[i]);
        }
    }

    static RJMMessage convertSimpleMessage(SimpleMailMessage smm)
    {
        RJMMessage m = new RJMMessage();
        m.setBcc(smm.getBcc());
        m.setCc(smm.getCc());
        m.setFrom(smm.getFrom());
        m.setReplyTo(smm.getReplyTo());
        m.setSubject(smm.getSubject());
        m.setText(smm.getText());
        m.setTo(smm.getTo());
        return m;
    }
}
