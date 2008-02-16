package com.voxbiblia.rjmailer;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Tools to encode non-ascii data into email content, for example using the
 * Quoted Printable character encoding.
 *
 * @author Noa Resare (noa@resare.com)  
 */
public class TextEncoder
{
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    // as documented in RFC2045 6.7
    static String encodeQP(String indata, String encoding)
            throws UnsupportedEncodingException
    {
        byte[] bytes = indata.getBytes(encoding);
        StringBuffer sb = new StringBuffer();
        int stringLength = 0;

        for (int i = 0; i < bytes.length; i++) {
            if (stringLength++ > 73) {
                sb.append("=\r\n");
                stringLength = 0;
            }
            byte b = bytes[i];
            if (b < 0 || b == 0x3d) {
                int b1 = b < 0 ? b + 0x100 : b;
                sb.append('=');
                sb.append(HEX_DIGITS[b1 >> 4]);
                sb.append(HEX_DIGITS[b1 & 0x0f]);
            } else {
                sb.append((char)b);
            }
        }
        return sb.toString();
    }

    private static final int OUTSIDE = 1;
    private static final int GOT_CR = 2;
    private static final int GOT_LF = 3;

    /**
     * Converts any combination of CR (code point decimal 13) and LF
     * (code point decimal 10) into the sequence CRLF as required by
     * RFC2822
     *
     * @param indata the string to convert newlines in.
     * @return the canonicalized version of indata
     */
    static String canonicalize(String indata)
    {
        StringBuffer sb = new StringBuffer(indata.length());
        char[] chars = indata.toCharArray();
        int state = OUTSIDE;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (state == OUTSIDE) {
                if (c == '\r') {
                    state = GOT_CR;
                } else if (c == '\n') {
                    state = GOT_LF;
                } else {
                    sb.append(c);
                }
            } else if (state == GOT_CR) {
                if (c == '\n') {
                    state = GOT_LF;
                } else if (c == '\r') {
                    sb.append("\r\n");
                } else {
                    sb.append("\r\n");
                    sb.append(c);
                    state = OUTSIDE;
                }
            } else { // GOT_LF
                sb.append("\r\n");
                if (c == '\r') {
                    state = GOT_CR;
                } else if (c != '\n') {
                    sb.append(c);
                    state = OUTSIDE;
                }
            }
        }
        return sb.toString();
    }

    /**
     * Encodes the given header into a String ending with CRLF suitable for
     * inclusion into a RFC2822 message stream. It breaks long headers
     * into multiple lines according to the folding rules in section 3.2.3
     * and encodes non-ascii characters according to RFC1522.
     *
     * @param name the name of the header field
     * @param data the header field data
     * @return an encoded header
     */
    public static String encodeHeader(String name, String data)
    {
        if (name.indexOf(':') != -1) {
            throw new IllegalArgumentException("name may not contain colon (:)");
        }

        if (!data.endsWith("\r\n")) {
            data = data + "\r\n";
        }
        return name + ": " + data;
    }

    static int getNonAsciiPercentage(String data)
    {
        char[] chars = data.toCharArray();
        int nonAsciiCount = 0;
        for (int i = 0; i <chars.length; i++) {
            if (chars[i] > 128) {
                nonAsciiCount++;
            }
        }
        return (int)((nonAsciiCount / (float)chars.length) * 100);
    }

    /**
     * Encodes Strings possibly including non-ascii characters using the
     * algorithm described in RFC1522, suitable for inclusion in email headers.
     *
     * @param data the data to possibly encode
     * @return a possibly encoded version of data
     */
    static String encodeHeaderWord(String data)
    {
        int encoding = check(data,0);
        if (encoding == 0) {
            return data;
        }
        String n = getCharset(encoding).name();
        if (getNonAsciiPercentage(data) > 50) {
            try {
                return "=?" + n + "?B?" +
                        Base64Encoder.encode(data.getBytes(n)) + "?=";
            } catch (UnsupportedEncodingException e) {
                throw new Error(e);
            }
        }
        try {
            return "=?" + n + "?Q?" + encodeQP(data, n).replace(' ', '_') +
                    "?=";
        } catch (UnsupportedEncodingException e) {
            throw new Error("unsupported encodeing: " + n);
        }

    }

    private static final int ASCII = 0;
    private static final int LATIN1 = 1;
    private static final int UTF8 = 2;

    private static Charset getCharset(int val)
    {
        switch(val) {
            case ASCII:
                return Charset.forName("US-ASCII");
            case LATIN1:
                return Charset.forName("ISO-8859-1");
            case UTF8:
                return Charset.forName("UTF-8");
            default:
                throw new Error("unknown charset id " + val);
        }
    }

    static Charset determineCharset(RJMMailMessage msg)
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
        return getCharset(c);
    }

    /**
     * Return the lowest charset constant that is needed to express this
     * string.
     *
     * @param s a String to check for which charset is needed
     * @param previous the highest previous value of the charset constant
     * @return the previous "highest" charset needed
     */
    private static int check(String s, int previous)
    {
        if (previous == UTF8) {
            return previous;
        }
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

}
