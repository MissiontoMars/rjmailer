package com.voxbiblia.rjmailer;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Tools to encode non-ascii data into email content, for example using the
 * Quoted Printable character encoding.
 *
 * @author Noa Resare (noa@resare.com)  
 */
class TextEncoder
{
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    private static final String EOL = "\r\n";
    // the maxmium length of a line excluding the new line pair
    private static final int MAX_LINE_LENGTH = 76;


    // as documented in RFC2045 6.7
    static String encodeQP(String indata, String encoding)
            throws UnsupportedEncodingException
    {
        byte[] bs = indata.getBytes(encoding);
        StringBuffer sb = new StringBuffer();
        int available = MAX_LINE_LENGTH;
        int endChars = 0;

        for (int i = 0; i < bs.length; i++) {

            byte b = bs[i];
            if (b == '\r') {
                endChars = 1;
            } else if(b == '\n') {
                if (endChars == 1) {
                    available = MAX_LINE_LENGTH;
                }
                endChars = 0;
            } else {
                endChars = 0;
            }

            boolean writeQuoted = false;
            if (b == '\r' || b == '\n') {
                writeQuoted = false;
            } else if (b == ' ' || b == '\t') {
                // special case, check if there is only white space until end
                // of line
                writeQuoted = true;
                for (int j = i; j < bs.length; j++) {
                    byte b0 = bs[j];
                    if (b0 == '\r' && bs.length > j + 1 && bs[j + 1] == '\n') {
                        break;
                    }
                    if (b0 != ' ' && b0 != '\t') {
                        writeQuoted = false;
                        break;
                    }
                }
            }  else if (b < 33 || b == '=' || b > 126) {
                writeQuoted = true;
            }

            if (writeQuoted) {
                int b1 = b < 0 ? b + 0x100 : b;
                if (available < 6 && bs.length > i + 1) {
                    sb.append("=" + EOL);
                    available = MAX_LINE_LENGTH;
                }
                sb.append('=');
                sb.append(HEX_DIGITS[b1 >> 4]);
                sb.append(HEX_DIGITS[b1 & 0x0f]);
                available -= 3;
            } else {
                if (available < 2 && bs.length > i + 1) {
                    sb.append("=" + EOL);
                    available = MAX_LINE_LENGTH;
                }
                sb.append((char)b);
                available--;
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
                    sb.append(EOL);
                } else {
                    sb.append(EOL);
                    sb.append(c);
                    state = OUTSIDE;
                }
            } else { // GOT_LF
                sb.append(EOL);
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
            throw new IllegalArgumentException("name may not contain colon");
        }

        return  name + ": " + encodeHeaderWord(data, 76 - name.length()) + EOL;
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
                return data.substring(0, available) + EOL +
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
                String qp = encodeQP(data.substring(0, charCount), n)
                        .replace(' ', '_');
                return "=?" + n + "?Q?" + qp + "?=\r\n " +
                        encodeHeaderWord(data.substring(charCount), 77);
            }
            return "=?" + n + "?Q?" + encodeQP(data, n).replace(' ', '_') +
                    "?=";
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
