package com.voxbiblia.rjmailer;

import java.util.Random;

/**
 * Generates Message-ID and Date fields.
 *
 * @author Noa Resare (noa@resare.com)
 */
public class FieldGenerator
{
    private String ehloHostname, nextMessageId;
    private Random random = new Random();

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
     * generation algorithm is described in RFC2822 3.6.4.
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

    private String generateMessageId()
    {
        byte[] bs = new byte[12];
        random.nextBytes(bs);
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

    public String getDate()
    {
        return null;
    }
}
