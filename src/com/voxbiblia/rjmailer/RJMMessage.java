package com.voxbiblia.rjmailer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Represents an Email that can be sent via the MailSender.
 *
 * The fields bcc, cc, from, replyTo and to holds email addresses, or
 * email addresses with a real name part in double quotes with the
 * actual email address in '<' '>' afterwards.
 *
 * The format of the email address is a simplified version of the
 * RFC2822 addr-spec where the localpart and domain part consists only
 * of a dot-atom, not a quoted-string or domain-literal.
 */
public class RJMMessage
{
    private List<String> bcc;
    private List<String> cc;
    private String from;
    private String replyTo;
    private Date sentDate;
    private String subject;
    private String text;
    private List<String> to = new ArrayList<String>();

    public List<String> getBcc()
    {
        return bcc;
    }

    public void setBcc(List<String> bcc)
    {
        this.bcc = bcc;
    }

    public void setBcc(String bcc)
    {
        if (this.bcc == null) {
            this.bcc = new ArrayList<String>();
        }
        this.bcc.add(bcc);
    }

    public List<String> getCc()
    {
        return cc;
    }

    public void setCc(List<String> cc)
    {
        this.cc = cc;
    }

    public void setCc(String cc)
    {
        if (this.cc == null) {
            this.cc = new ArrayList<String>();
        }
        this.cc.add(cc);
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

    public List<String> getTo()
    {
        if (to.size() == 0) {
            return null;
        }
        return to;
    }

    public void setTo(String[] to)
    {
        this.to.clear();
        this.to.addAll(Arrays.asList(to));
    }

    public void addTo(String displayName, String addr)
    {
        addTo("\"" + displayName.replace("\"", "\\\"") + "\" <" + addr + ">");
    }

    public void addTo(String to)
    {
        this.to.add(to);
    }

    public void setTo(String to)
    {
        this.to.clear();
        this.to.add(to);
    }
}
