package com.voxbiblia.rjmailer;

import java.io.UnsupportedEncodingException;

/**
 * Encodes non-ascii strings in the Quoted Printable encoding.
 *
 *  
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


    private static char[] key = 
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();


    // as documented in RFC2045 6.8
    public static String encodeBase64(byte[] data)
    {
        StringBuffer sb = new StringBuffer((int)(data.length * 1.33) + 2);
        int i = 0;
        for (; i < (data.length - 2); i += 3) {
            output((pos(data[i]) << 16) + (pos(data[i + 1]) << 8) + pos(data[i + 2]), sb, 0);
        }
        if (i == data.length - 2) {
            output((pos(data[i]) << 16) + (pos(data[i + 1]) << 8), sb, 1);
        } else if (i == data.length - 1) {
            output(pos(data[i]) << 16, sb, 2);
        }
        
        return sb.toString();
    }


    private static void output(int quantum, StringBuffer sb, int pad)
    {
        sb.append(key[(quantum >> 18) & 0x3f]);
        sb.append(key[(quantum >> 12) & 0x3f]);
        if (pad == 2) {
            sb.append("==");
        } else if (pad == 1) {
            sb.append(key[(quantum >> 6) & 0x3f]);
            sb.append('=');
        } else {
            sb.append(key[(quantum >> 6) & 0x3f]);
            sb.append(key[quantum & 0x3f]);
        }
    }

    private static int pos(byte b)
    {
        return b < 0 ? b + 0x100 : b;
    }
}
