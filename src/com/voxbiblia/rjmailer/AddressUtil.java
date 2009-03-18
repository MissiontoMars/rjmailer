package com.voxbiblia.rjmailer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A small utility class that with convinience methods for handling RFC2822
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

    public static List<String> getToAddresses(RJMMessage msg)
    {
        List<String> l = new ArrayList<String>();
        List<String> ss = msg.getTo();
        if (ss != null) {
            for (String s : ss) {
                l.add(getAddress(s));
            }
        }
        ss = msg.getBcc();
        if (ss != null) {
            for (String s : ss) {
                l.add(getAddress(s));
            }
        }
        return l;
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
        return encodeAddressHeader(field, Collections.singletonList(address));
    }

    private static final int MAX_LINE = 78;

    static String encodeAddressHeader(String field, List<String> addresses)
    {
        List<String> l = new ArrayList<String>();

        l.add(field + ":");

        for (String s : addresses) {
            appendAddress(s, l);
        }
        removeLastComma(l);

        int available = MAX_LINE;
        StringBuilder sb = new StringBuilder();
        Iterator tokens = l.iterator();
        sb.append(tokens.next());
        while(tokens.hasNext()) {
            String t = (String)tokens.next();
            int consumes = t.length();
            if (t.contains("\r\n")) {
                consumes -= t.lastIndexOf("\r\n") + 2;
            }
            if (available - t.length() < 0) {
                sb.append("\r\n");
                available = MAX_LINE - consumes - 1;
            } else {

            }
            sb.append(" ").append(t);
        }
        sb.append("\r\n");
        return sb.toString();
    }

    private static void removeLastComma(List<String> l)
    {
        String last = l.get(l.size() -1);
        l.set(l.size() - 1, last.substring(0,last.length() - 1));
    }

    private static void appendAddress(String address, List<String> tokenList)
    {
        String dn = getDisplayName(address);
        if (dn != null) {
            if (!"US-ASCII".equals(TextEncoder.getCharset(dn))) {
                dn = TextEncoder.encodeHeaderWord(dn, MAX_LINE - 2);
            }
            tokenList.add('"' + dn + '"');
            String s = '<' + getAddress(address) + ">,";
            if (s.length() > 998) {
                throw new RJMInputException("address is far too long, " +
                        "refusing to create malformed email message");
            }
            tokenList.add(s);
        } else {
            String s = getAddress(address) + ",";
            if (s.length() > 998) {
                throw new RJMInputException("address is far too long, " +
                        "refusing to create malformed email message");
            }
            tokenList.add(s);
        }
    }
}
