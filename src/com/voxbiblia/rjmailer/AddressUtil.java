package com.voxbiblia.rjmailer;

import java.util.List;
import java.util.ArrayList;

/**
 * A small utility clas that with convinience methods for handling RFC2822
 * addresses.
 *
 * @author Noa Resare (noa@resare.com)
 */
class AddressUtil
{
    /**
     * Extracts an email address from the contents of an email address field.
     * In the terminology used in RFC2822 it extracts the addr-spec from the
     * address, removing display-name.
     *
     * The behavior of this method is specified in RFC2822 section 3.4
     *
     * @param field the complete addres field, possibly with display-name
     * @return just the addr-spec
     */
    public static String getAddress(String field)
    {
        // special case, if the field only consists of an email address
        // the short form without angle brackets is sometimes used
        if (!field.contains("<")) {
            return field;
        }
        char[] chars = field.toCharArray();
        boolean inDQUOTE = false;
        boolean escaped = false;
        int ltPos = -1;
        int gtPos = -1;
        for (int i = 0 ; i < chars.length; i++) {
            char c = chars[i];
            if (escaped) {
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                inDQUOTE = !inDQUOTE;
            } else if (c == '<' && !inDQUOTE) {
                ltPos = i;
            } else if (c == '>' && !inDQUOTE) {
                gtPos = i;
            }
        }
        if (ltPos != -1) {
            return field.substring(ltPos + 1, gtPos);
        }
        return field;
    }

    /**
     * This method returns the display name part of an address. Please note
     * that it only supports display names enclosed in double quotes, and not
     * the unquoted single word variant.
     *
     * @param field the header body to extract the display name from.
     * @return the display name
     */
    public static String getDisplayName(String field)
    {
        if (!field.contains("\"")) {
            return null;
        }
        boolean escaped = false;
        char[] chars = field.toCharArray();
        int firstQuote = -1;
        for (int i = 0 ; i < chars.length; i++) {
            char c = chars[i];
            if (escaped) {
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                if (firstQuote == -1) {
                    firstQuote = i + 1;
                } else {
                    return field.substring(firstQuote, i);
                }
            }
        }
        throw new Error("malformed field: "+ field);
    }

    public static String[] getToAddresses(RJMMessage msg)
    {
        List l = new ArrayList();
        String[] ss = msg.getTo();
        if (ss != null) {
            for (int i = 0; i < ss.length; i++) {
                l.add(getAddress(ss[i]));
            }
        }
        ss = msg.getBcc();
        if (ss != null) {
            for (int i = 0; i < ss.length; i++) {
                l.add(getAddress(ss[i]));
            }
        }
        return (String[]) l.toArray(new String[l.size()]);
    }

    /**
     * Returns the domain part of the address specified in field.
     *
     * @param field the field to find the email address to look for the domain
     * in
     * @return the domain
     */
    public static String getDomain(String field)
    {
        String address = getAddress(field);
        int atPos = address.indexOf('@');
        return address.substring(atPos + 1);
    }

    static String encodeAddressHeader(String field, String address)
    {
        return encodeAddressHeader(field, new String[] {address});
    }

    static String encodeAddressHeader(String field, String[] addresses)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(field);
        sb.append(": ");
        int available = 78 - field.length() - 4;
        appendAddress(addresses[0], available, sb);
        for (int i = 1 ; i < addresses.length; i++) {
            sb.append(", ");
            appendAddress(addresses[i], available, sb);
        }
        sb.append("\r\n");
        return sb.toString();

    }

    private static void appendAddress(String address, int available,
                                      StringBuffer sb)
    {
        String dn = getDisplayName(address);
        String charset = TextEncoder.getCharset(dn);
        if (!charset.equals("US-ASCII")) {
            String s = TextEncoder.encodeHeaderWord(dn, available);
            sb.append('"').append(s).append("\" <");
            sb.append(getAddress(address)).append(">");
        } else {
            sb.append(address);
        }

    }
}
