package com.voxbiblia.rjmailer;

import org.columba.ristretto.smtp.SMTPProtocol;
import org.columba.ristretto.message.*;
import org.columba.ristretto.parser.ParserException;
import org.columba.ristretto.parser.AddressParser;
import org.columba.ristretto.io.CharSequenceSource;
import org.columba.ristretto.composer.MimeTreeRenderer;

import java.net.InetAddress;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * A <code>SyncMailSender</code> is an object that sends email. Create an instance
 * with the default constructor and configure it using the various JavaBean
 * properties.
 * Once configured your <code>SyncMailSender</code> is ready to send mail via the
 * <code>send()</code> method. 
 * 
 * @author Noa Resare (noa@voxbiblia.com)
 */
public class SyncMailSender
{
    private static final int DEFAULT_PORT = 25;

    String server;
    int port = DEFAULT_PORT;

    public void send(JsmMailMessage mailMessage)
    {
        SMTPProtocol protocol = new SMTPProtocol(server, port);
        try {
            protocol.openPort();
            protocol.ehlo(InetAddress.getLocalHost());
            protocol.mail(parse(mailMessage.getFrom()));
            String[] to = mailMessage.getTo();
            for(int i = 0; i < to.length; i++) {
                protocol.rcpt(parse(to[i]));
            }
            String[] bcc = mailMessage.getBcc();
            for(int i = 0; i < bcc.length; i++) {
                protocol.rcpt(parse(bcc[i]));
            }
            
            protocol.data(makeStream(mailMessage));
            protocol.quit();
            
        } catch (Exception e) {
            throw new Error(e);
        }

    }

    static InputStream makeStream(JsmMailMessage mailMessage)
    {
        Header header = new Header();
        BasicHeader basicHeader = new BasicHeader(header);

        String[] mmTo = mailMessage.getTo();
        Address[] to = new Address[mmTo.length];
        for (int i = 0; i < mmTo.length; i++) {
            to[i] = parse(mmTo[i]);
        }
        basicHeader.setTo(to);

        basicHeader.setFrom(parse(mailMessage.getFrom()));

        String[] mmCc = mailMessage.getCc();
        if (mmCc != null) {
            Address[] cc = new Address[mmCc.length];
            for (int i = 0; i < mmCc.length; i++) {
                cc[i] = parse(mmCc[i]);
            }
            basicHeader.setCc(cc);
        }

        Charset cs = determineCharset(mailMessage);
        basicHeader.setSubject(mailMessage.getSubject(), cs);

        MimeHeader mimeHeader = new MimeHeader(header);
        LocalMimePart root = new LocalMimePart(mimeHeader);
        root.setBody(new CharSequenceSource(mailMessage.getText()));
        try {
            return MimeTreeRenderer.getInstance().renderMimePart(root);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private static final int ASCII = 0;
    private static final int LATIN1 = 1;
    private static final int UTF8 = 2;

    static Charset determineCharset(JsmMailMessage msg)
    {
        int c = ASCII;
        String[] mmTo = msg.getTo();
        if (mmTo != null) {
            for (int i = 0; i < mmTo.length; i++) {
                i = check(mmTo[i], i);
            }
        }
        String[] mmCc = msg.getCc();
        if (mmCc != null) {
            for (int i = 0; i < mmCc.length; i++) {
                i = check(mmCc[i], i);
            }
        }
        
        c = check(msg.getReplyTo(), c);
        c = check(msg.getFrom(), c);
        c = check(msg.getSubject(), c);
        c = check(msg.getText(), c);

        Charset charset = null;
        switch(c) {
            case ASCII:
                return Charset.forName("US-ASCII");
            case LATIN1:
                return Charset.forName("ISO-8859-1");
            case UTF8:
                return Charset.forName("UTF-8");
            default:
                throw new Error("unknown charset id " + c);
        }
    }

    /**
     * Return the lowest charset constant that is needed to express this
     * string.
     *
     * @param s
     * @return
     */
    private static int check(String s, int previous)
    {
        int required = ASCII;
        if (s == null) {
            return required;
        }
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] > 0xfe) {
                return UTF8;
            }
            if (chars[i] > 0x7f) {
                required = LATIN1;
            }
        }
        return required > previous ? required : previous;
    }

    static Address parse(String unparsedAddres)
    {
        try {
            return AddressParser.parseAddress(unparsedAddres);
        } catch (ParserException e) {
            throw new Error("Could not parse address '" + unparsedAddres + "'", e);
        }
    }

    public String getServer()
    {
        return server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }
}
