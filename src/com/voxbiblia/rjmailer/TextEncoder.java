package com.voxbiblia.rjmailer;

import java.io.UnsupportedEncodingException;

/**
 * Encodes non-ascii strings in the Quoted Printable encoding.
 *
 *  
 */
public class TextEncoder
{
    private static final char[] HEX_DIGITS = new char[] {'0','1','2','3','4','5','6','7',
            '8','9','A','B','C','D','E','F'};

    static String encodeQP(String indata, String encoding)
            throws UnsupportedEncodingException
    {
        byte[] bytes = indata.getBytes(encoding);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
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
}
