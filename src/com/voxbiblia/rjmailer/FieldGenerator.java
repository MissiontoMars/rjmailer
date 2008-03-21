package com.voxbiblia.rjmailer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Generates Message-ID and Date fields.
 *
 * @author Noa Resare (noa@resare.com)
 */
public class FieldGenerator
{
    private String ehloHostname, nextMessageId, nextDate;
    private final Random random = new Random();
    private static SimpleDateFormat sdf = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss ZZZZ", Locale.US);


    /**
     * Creates a new FieldGenerator using the given ehloHostname for the domain
     * part of the Message-ID field.  
     *
     * @param ehloHostname the ehloHostname to use for message id generation
     */
    public FieldGenerator(String ehloHostname)
    {
        this.ehloHostname = ehloHostname;
    }

    /**
     * Returns a unique value for use int the Message-ID SMTP field. The
     * generation algorithm is described in RFC2822 3.6.4. This message is
     * thread safe.
     *
     * @return a unique string in Message-ID format
     */
    public String getMessageId()
    {
        if (nextMessageId != null) {
            String s = nextMessageId;
            nextMessageId = null;
            return s;
        }
        return generateMessageId();
    }


    /**
     * Returns the current date and time in the fomat described in RFC2822 3.3
     *
     * @return a String with the current date and time
     */
    public String getDate()
    {
        if (nextDate != null) {
            String s = nextDate;
            nextDate = null;
            return s;
        }
        return sdf.format(new Date());
    }

    /**
     * Return the same String as the next invocation of getMessageId will
     * return. Used for debugging purposes
     *
     * @return the next messageId
     */
    String getNextMessgeId()
    {
        this.nextMessageId = generateMessageId();
        return nextMessageId;
    }

    String getNextDate()
    {
        nextDate = generateDate();
        return nextDate;
    }

    private String generateDate()
    {
        return sdf.format(new Date());
    }


    private String generateMessageId()
    {
        byte[] bs = new byte[12];
        synchronized (random) {
            random.nextBytes(bs);
        }
        long now = System.currentTimeMillis();
        byte[] nowBytes = new byte[6];
        nowBytes[0] = (byte)(now >> 40 & 0xff);
        nowBytes[1] = (byte)(now >> 32 & 0xff);
        nowBytes[2] = (byte)(now >> 24 & 0xff);
        nowBytes[3] = (byte)(now >> 16 & 0xff);
        nowBytes[4] = (byte)(now >> 8 & 0xff);
        nowBytes[5] = (byte)(now & 0xff);

        return Base64Encoder.encode(nowBytes) + "-" + Base64Encoder.encode(bs) +
                "@" + ehloHostname;
    }


}
