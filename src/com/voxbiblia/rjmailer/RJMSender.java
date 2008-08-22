package com.voxbiblia.rjmailer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private boolean calledAfterPropertiesSet = false;

    public RJMSender(String ehloHostname)
    {
        conversationHandler = new ConversationHandler(ehloHostname);
    }

    public RJMResult[] send(RJMMessage message)
    {
        if (!calledAfterPropertiesSet) {
            afterPropertiesSet();
        }
        String[] tos = AddressUtil.getToAddresses(message);
        if (resolverProxy != null) {
            resolveAndSend(message, tos);
        }
        String result = conversationHandler.sendMail(message, tos, smtpServer);
        return new RJMResult[] { new RJMResult(smtpServer, result)};
    }

    private void afterPropertiesSet()
    {
        calledAfterPropertiesSet = true;
        if (resolverProxy != null) {
            // this means that setResolverProxy was called and the caller knows
            // what she is doing
            return;
        }


        if (smtpServer == null && nameServer == null) {
            throw new Error("Either one of the properties nameServer or " +
                    "smtpServer must be set");
        }
        if (nameServer != null) {
            if (!ResolverProxyImpl.hasJresolver()) {
                throw new Error("You don't have the jresolver classes in " +
                        "your classpath, download and install or use the " +
                        "smtpServer property instead of nameServer");
            }
            resolverProxy = new ResolverProxyImpl(nameServer);
        }
    }

    private RJMResult[] resolveAndSend(RJMMessage message, String[] tos)
    {
        List results = new ArrayList();
        for (int i = 0; i < tos.length; i++) {
            List servers = resolverProxy.resolveMX(AddressUtil.getDomain(tos[i]));
            String mx = (String)servers.get(0);
            String s = conversationHandler.sendMail(message, new String[] {tos[i]},
                    mx);
            results.add(new RJMResult(mx, s));
        }
        return (RJMResult[])results.toArray(new RJMResult[results.size()]);
    }

    // returns a map with string keys and list values where the value is a list
    // of recipients
    Map makeMXMap(String[] tos)
    {
        Map m = new HashMap();
        for (int i = 0; i < tos.length; i++) {
            String domain = AddressUtil.getDomain(tos[i]);
            String mx = (String)resolverProxy.resolveMX(domain).get(0);
            List l = (List)m.get(mx);
            if (l == null) {
                l = new ArrayList();
                m.put(mx, l);
            }
            l.add(tos[i]);
        }
        return m;
    }

    public void setSmtpServer(String smtpServer)
    {
        this.smtpServer = smtpServer;
    }

    public void setNameServer(String nameServer)
    {
        this.nameServer = nameServer;
    }

    // this method is for testing purposes
    void setResolverProxy(ResolverProxy resolverProxy)
    {
        this.resolverProxy = resolverProxy;
    }
}
