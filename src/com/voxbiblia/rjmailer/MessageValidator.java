package com.voxbiblia.rjmailer;

/**
 * Validates message contents and email addresses to assure that the message
 * is ready to be sent.
 */
public class MessageValidator
{

    /**
     * Checks that the message is in order to be sent
     *
     * @param message the message to check
     */
    public static void validate(RJMMessage message)
    {
        if (message == null) {
            throw new RJMException(ExactCause.INVALID_INPUT,
                    "Message is null, can not send");
        }

        if (message.getTo() == null) {
            throw new RJMException(ExactCause.RECIPIENT_MISSING,
                    "You can not send a message with no recipients.");
        }

        for (String to : message.getTo()) {
            verifyAddress(to);
        }
    }

    // Validate address using the simplified syntax in RFC2822 section 3.4
    private static void verifyAddress(String email)
    {
        String address = AddressUtil.getAddress(email);
        if (address.isEmpty()) {
            throw new RJMException(ExactCause.ADDRESS_SYNTAX,
                    "The email address part is empty: " + email );
        }
        int atOffset = address.indexOf('@');
        if (atOffset < 0) {
            RJMException e = new RJMException(ExactCause.ADDRESS_SYNTAX,
                    CauseDetail.ADDRESS_MISSING_AT, "The email address needs " +
                            "to contain an at sign: "+ email);
            e.setEmail(address);
            throw e;
        }
        checkValidChars(address.substring(0, atOffset));
    }

    private static final String dot_atom = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz!#$%&'*+-/=?^_`{|}~.";

    private static void checkValidChars(String s)
    {
        char[] cs = s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            if (dot_atom.indexOf(cs[i]) == -1) {
                throw new RJMException(ExactCause.ADDRESS_SYNTAX,
                        CauseDetail.ADDRESS_INVALID_CHAR,
                        "Local part of email address contains invalid " +
                                "character: '" + cs[i] + "'");

            }
        }
    }
}