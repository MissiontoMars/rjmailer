package com.voxbiblia.rjmailer;

import java.util.List;

/**
 * Sends email.
 *
 * @author Noa Resare (noa@resare.com)
 */
public class RJMSender
{
    private ConversationHandler conversationHandler;
    private String smtpServer;
    private String nameServer;

    private ResolverProxy resolverProxy;

    public RJMSender(String ehloHostname)
    {
        conversationHandler = new ConversationHandler(ehloHostname);
    }

    public String send(RJMMessage message)
    {
        String[] tos = AddressUtil.getToAddresses(message);
        if (smtpServer == null && nameServer == null) {
            throw new Error("Either one of the properties nameServer or " +
                    "smtpServer must be set");
        }
        if (resolverProxy == null && nameServer != null) {
            if (!ResolverProxy.hasJresolver()) {
                throw new Error("You don't have the jresolver classes in " +
                        "your classpath, download and install or use the " +
                        "smtpServer property instead of nameServer");
            }
            resolverProxy = new ResolverProxy(nameServer);
        }
        if (resolverProxy != null) {
            return resolveAndSend(message, tos);
        }
        return conversationHandler.sendMail(message, tos, smtpServer);
    }

    private String resolveAndSend(RJMMessage message, String[] tos)
    {
        for (int i = 0; i < tos.length; i++) {
            List servers = resolverProxy.resolveMX(AddressUtil.getDomain(tos[i]));
            conversationHandler.sendMail(message, new String[] {tos[i]},
                    (String)servers.get(0));
        }
        return null;
    }

    public void setSmtpServer(String smtpServer)
    {
        this.smtpServer = smtpServer;
    }

    public void setNameServer(String nameServer)
    {
        this.nameServer = nameServer;
    }
}
