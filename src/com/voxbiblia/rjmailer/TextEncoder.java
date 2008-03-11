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
    static String encodeQP(String indata, String encoding )
            throws UnsupportedEncodingException
    {
        byte[] bytes = indata.getBytes(encoding);
        StringBuffer sb = new StringBuffer();
        int stringLength = 0;

        for (int i = 0; i < bytes.length; i++) {
            if (stringLength++ > 76) {
                sb.append("=\r\n");
                stringLength = 0;
            }
            byte b = bytes[i];
            if (b < 0 || b == 0x3d) {
                int b1 = b < 0 ? b + 0x100 : b;
                sb.append('=');
                sb.append(HEX_DIGITS[b1 >> 4]);
                sb.append(HEX_DIGITS[b1 & 0x0f]);
                stringLength += 2;
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

        return  name + ": " + encodeHeaderWord(data, 76 - name.length()) + "\r\n";
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
     * If the resulting header word does not fit into the number of available
     * chars up to the available number of chars are written to the returned
     * String, followed by the CRLF sequence and the rest of data on one or more
     * continuation lines. After the first line, each line is no longer than
     * 78 chars plus the CRLF end of line sequence.  
     *
     * @param data the data to possibly encode
     * @param available the number of chars available for this word before
     * a line break is needed.
     * @return a possibly encoded version of data
     */
    static String encodeHeaderWord(String data, int available)
    {
        int encoding;
        if (data.length() > available) {
            encoding = check(data.substring(0, available), 0);
        } else {
            encoding = check(data,0);
        }
        if (encoding == 0) {
            if (data.length() > available) {
                return data.substring(0, available) + "\r\n " +
                        encodeHeaderWord(data.substring(available), 77);
            }
            return data;
        }
        String n = getCharset(encoding).name();
        try {
            int capacity = available  - n.length() - 7;
            if (getNonAsciiPercentage(data) > 50) {
                int charCount = howMany(data, n, capacity, BASE_64);
                if (charCount < data.length()) {
                    byte[] bytes = data.substring(0,charCount).getBytes(n);
                    String s = Base64Encoder.encode(bytes);
                    return "=?" + n + "?B?" + s + "?=\r\n " +
                            encodeHeaderWord(data.substring(charCount), 77);
                }
                String s = Base64Encoder.encode(data.getBytes(n));
                return "=?" + n + "?B?" + s + "?=";
            }
            int charCount = howMany(data, n, capacity, QP);
            if (charCount < data.length()) {
                String qp = encodeQP(data.substring(0, charCount), n).replace(' ', '_');
                return "=?" + n + "?Q?" + qp + "?=\r\n " +
                        encodeHeaderWord(data.substring(charCount), 77);
            }
            return "=?" + n + "?Q?" + encodeQP(data, n).replace(' ', '_') + "?=";
        } catch (UnsupportedEncodingException e) {
            throw new Error("unsupported encoding: " + n);
        }
    }

    static final int BASE_64 = 0;
    static final int QP = 1;

    /**
     * Returns the largest number of chars from s that results in byteCount
     * number of bytes, or less when encoded with charset.
     *
     * @param s the string to read the bytes from
     * @param charset the character set to encode the characters into
     * @param byteCount the number of bytes byteCount to encode into
     * @param type the type of encoding, Base64 or Quoted Printable
     * @return the number of chars that can be encoded
     */
    static int howMany(String s, String charset, int byteCount, int type)
    {
        if (charset.startsWith("ISO-8859")) {
            if (type == BASE_64) {
                int capacity = byteCount / 4 * 3;
                return s.length() < capacity ? s.length() : capacity;
            }
            char[] chars = s.toCharArray();
            for (int i = 0 ; i < chars.length; i++) {
                if (chars[i] > 0x7f) {
                    byteCount -= 3;
                } else {
                    byteCount--;
                }
                if (byteCount < 0) {
                    return i;
                } else if (byteCount == 0) {
                    return i + 1;
                }
            }
            return chars.length;
        } else if ("UTF-8".equals(charset)) {
            byteCount = byteCount / 4 * 3;
            char[] chars = s.toCharArray();
            for (int i = 0 ; i < chars.length; i++) {
                int needed = 1;
                if (chars[i] > 0x07ff) {
                    needed = 3;
                } else if (chars[i] > 0x007f) {
                    needed = 2;
                }
                if (type == QP) needed *= 3;
                byteCount -= needed;
                if (byteCount < 0) {
                    return i;
                } else if (byteCount == 0) {
                    return i + 1;
                }
            }
            return chars.length;
        }
        throw new Error("don't know how to handle charset "+ charset);
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

    public static String getCharset(String s)
    {
        return getCharset(check(s, 0)).name();
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
