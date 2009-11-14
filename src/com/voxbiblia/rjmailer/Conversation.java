package com.voxbiblia.rjmailer;

import java.util.List;

/**
 * Classes implementing this interface can carry out an SMTP conversation
 * with a remote server.
 */
public interface Conversation
{

    void sendMail(RJMMessage msg, List<String> to, SendState ss);
}
