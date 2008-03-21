package com.voxbiblia.rjmailer;

import java.io.IOException;

/**
 * Sends email.
 *
 * @author Noa Resare (noa@resare.com)
 */
public class RJMSender
{
    private ConversationHandler conversationHandler;
    private String server;

    public RJMSender(String server, String ehloHostname)
    {
        conversationHandler = new ConversationHandler(ehloHostname);
        this.server = server;
    }

    public void send(RJMMessage message)
            throws IOException
    {
        String to = AddressUtil.getAddress(message.getTo()[0]);
        conversationHandler.sendMail(message, new String[] {to}, server);
    }
}
