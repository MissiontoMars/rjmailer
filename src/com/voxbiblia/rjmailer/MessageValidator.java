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

    // Validate address according to RFC2822 section 3.4
    private static void verifyAddress(String email)
    {
        String address = AddressUtil.getAddress(email);
        if (address.isEmpty()) {
            throw new RJMException(ExactCause.ADDRESS_SYNTAX,
                    "The email address part is empty: " + email );
        }
        
    }
}