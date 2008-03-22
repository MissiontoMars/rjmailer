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
}
