package com.voxbiblia.rjmailer;

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

    public String send(RJMMessage message)
    {
        String[] tos = AddressUtil.getToAddresses(message);
        return conversationHandler.sendMail(message, tos, server);
    }
}
