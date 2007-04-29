package com.voxbiblia.rjmailer;

import org.springframework.mail.MailMessage;

import java.util.Date;

/**
 * Represents an Email that can be sent via the SyncMailSender.
 */
public class JsmMailMessage implements MailMessage
{
    private String[] bcc;
    private String[] cc;
    private String from;
    private String replyTo;
    private Date sentDate;
    private String subject;
    private String text;
    private String[] to;

    public String[] getBcc()
    {
        return bcc;
    }

    public void setBcc(String[] bcc)
    {
        this.bcc = bcc;
    }

    public void setBcc(String bcc)
    {
        this.bcc = new String[] {bcc};
    }

    public String[] getCc()
    {
        return cc;
    }

    public void setCc(String[] cc)
    {
        this.cc = cc;
    }

    public void setCc(String cc)
    {
        this.cc = new String[] {cc};
    }

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getReplyTo()
    {
        return replyTo;
    }

    public void setReplyTo(String replyTo)
    {
        this.replyTo = replyTo;
    }

    public Date getSentDate()
    {
        return sentDate;
    }

    public void setSentDate(Date sentDate)
    {
        this.sentDate = sentDate;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String[] getTo()
    {
        return to;
    }

    public void setTo(String[] to)
    {
        this.to = to;
    }

    public void setTo(String to)
    {
        this.to = new String[] {to};
    }
}
