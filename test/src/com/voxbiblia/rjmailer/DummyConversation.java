package com.voxbiblia.rjmailer;

import java.util.List;

/**
 * For testing.
 */
public class DummyConversation
    implements Conversation
{
    public void sendMail(RJMMessage msg, List<String> to, SendState ss)
    {
        for (String s : to) {
            RJMException e = new RJMException(ExactCause.MAILBOX_UNAVAILABLE,
                    String.format("<%s>: Recipient address rejected: User " +
                            "unknown in virtual alias table", s));
            ss.hardFailure(s, e);
        }
    }
}
