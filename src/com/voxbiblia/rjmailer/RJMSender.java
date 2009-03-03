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

    public RJMResult send(RJMMessage message)
    {
        List tos = AddressUtil.getToAddresses(message);
        if (tos.isEmpty()) {
            throw new IllegalArgumentException("Please use the sendMulti() " +
                    "method to send messages with multiple recipients");
        }
        Map result = sendMulti(message);
        Object o = result.values().iterator().next();
        if (o instanceof RuntimeException) {
            throw (RuntimeException)o;
        }
        return (RJMResult)o;
    }


    public Map sendMulti(RJMMessage message)
    {
        if (!calledAfterPropertiesSet) {
            afterPropertiesSet();
        }
        List tos = AddressUtil.getToAddresses(message);

        if (resolverProxy != null) {
            return resolveAndSend(message, tos);
        }
        String result = conversationHandler.sendMail(message, tos, smtpServer);
        Map results = new HashMap();
        for (int i = 0; i < tos.size(); i++) {
            results.put(tos.get(i), new RJMResult(smtpServer, result));
        }
        return results;
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

    private Map resolveAndSend(RJMMessage message, List tos)
    {
        SendState ss = new SendState(resolverProxy, tos);
        
        MXData d = ss.nextMXData();
        while (d != null) {
            List l = d.getRecipients();
            String result = conversationHandler.sendMail(message, 
                    d.getRecipients(), d.getServer());
            for (int i = 0; i < tos.size(); i++) {
                ss.success((String)l.get(i),d.getServer(), result);
            }


            d = ss.nextMXData();
        }
        return ss.getResults();
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
