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
    /*

addr-spec       =       local-part "@" domain

local-part      =       dot-atom / quoted-string / obs-local-part

domain          =       dot-atom / domain-literal / obs-domain

domain-literal  =       [CFWS] "[" *([FWS] dcontent) [FWS] "]" [CFWS]

dcontent        =       dtext / quoted-pair

dtext           =       NO-WS-CTL /     ; Non white space controls

                        %d33-90 /       ; The rest of the US-ASCII
                        %d94-126        ;  characters not including "[",
                                        ;  "]", or "\"
--

atext           =       ALPHA / DIGIT / ; Any character except controls,
                        "!" / "#" /     ;  SP, and specials.
                        "$" / "%" /     ;  Used for atoms
                        "&" / "'" /
                        "*" / "+" /
                        "-" / "/" /
                        "=" / "?" /
                        "^" / "_" /
                        "`" / "{" /
                        "|" / "}" /
                        "~"

atom            =       [CFWS] 1*atext [CFWS]

dot-atom        =       [CFWS] dot-atom-text [CFWS]

dot-atom-text   =       1*atext *("." 1*atext)

--

qtext           =       NO-WS-CTL /     ; Non white space controls

                        %d33 /          ; The rest of the US-ASCII
                        %d35-91 /       ;  characters not including "\"
                        %d93-126        ;  or the quote character

qcontent        =       qtext / quoted-pair

quoted-string   =       [CFWS]
                        DQUOTE *([FWS] qcontent) [FWS] DQUOTE
                        [CFWS]
---
quoted-pair     =       ("\" text) / obs-qp

text            =       %d1-9 /         ; Characters excluding CR and LF
                        %d11 /
                        %d12 /
                        %d14-127 /
                        obs-text

     */

    //private static final String atext = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!#$%&'*+-/=?^_`{|}~";

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
                    CauseDetail.EMAIL_MISSING_AT, "The email address needs " +
                            "to contain an at sign: "+ email);
            e.setEmail(address);
            throw e;
        }
        
    }
}