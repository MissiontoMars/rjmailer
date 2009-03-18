package com.voxbiblia.rjmailer;

/**
 * The encode() method of this class encodes binary data into an US-ASCII
 * String according to the algorithm described in RFC2045 section 6.8
 *
 * @author Noa Resare (noa@voxbiblia.com)
 */
public class Base64Encoder
{
    private static char[] key =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
                    .toCharArray();

    /**
     * Encodes the given byte array of binary data into a character String
     * according to the algorithm describedin RFC2045 section 6.8. Please
     * note that the resulting String doesn't include line breaks, which
     * will need to be added before it is suitable for inclusion in for example
     * RFC2822 email transmissions.
     *
     * @param data the binary data to encode
     * @return the resulting String
     */
    public static String encode(byte[] data)
    {
        StringBuilder sb = new StringBuilder((int)(data.length * 1.33) + 2);
        int i = 0;
        for (; i < (data.length - 2); i += 3) {
            output((pos(data[i]) << 16) + (pos(data[i + 1]) << 8) +
                    pos(data[i + 2]), sb, 0);
        }
        if (i == data.length - 2) {
            output((pos(data[i]) << 16) + (pos(data[i + 1]) << 8), sb, 1);
        } else if (i == data.length - 1) {
            output(pos(data[i]) << 16, sb, 2);
        }

        return sb.toString();
    }

    /**
     * Writes base64 output to sb using data from the quantum, possibly
     * padding the written string with equals signs to indicate that the
     * quantum corresponds to less than 3 bytes of data. For the exact
     * semantics of this method, please see RFC2045 section 6.8.
     *
     * @param quantum the data to be written
     * @param sb the StringBuffer that the data should be appended to
     * @param pad can indicate that quantum only holds 1 or 2 bytes of data
     * in which case padding occurs according to spec.
     */
    private static void output(int quantum, StringBuilder sb, int pad)
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


    /**
     * Returns the integer value of a byte if it had been unsigned.
     *
     * @param b a java byte (signed) value
     * @return an int that corresponds to the byte if it had been unsigned
     */
    static int pos(byte b)
    {
        return b < 0 ? b + 0x100 : b;
    }

}
