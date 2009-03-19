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
        List<String> tos = AddressUtil.getToAddresses(message);
        if (tos.size() > 1) {
            throw new IllegalArgumentException("Please use the sendMulti() " +
                    "method to send messages with multiple recipients");
        }
        Object o = sendMulti(message).get(tos.get(0));
        if (o instanceof RJMException) {
            throw (RJMException)o;
        }
        return (RJMResult)o;
    }

    public Map<String, SendResult> sendMulti(RJMMessage message)
    {
        if (!calledAfterPropertiesSet) {
            afterPropertiesSet();
        }
        List<String> tos = AddressUtil.getToAddresses(message);

        if (resolverProxy != null) {
            return resolveAndSend(message, tos);
        }
        String result = conversationHandler.sendMail(message, tos, smtpServer);
        Map<String, SendResult> results = new HashMap<String, SendResult>();
        for (String to : tos) {
            results.put(to, new RJMResult(smtpServer, result));
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

    private Map<String, SendResult> resolveAndSend(RJMMessage message, List tos)
    {
        SendState ss = new SendState(resolverProxy, tos);
        
        MXData d = ss.nextMXData();
        if (d == null) {
            throw new Error("Invalid state, no MXData");
        }
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
    Map<String, List<String>> makeMXMap(String[] tos)
    {
        Map<String, List<String>> m = new HashMap<String,List<String>>();

        for (String to : tos) {
            String domain = AddressUtil.getDomain(to);
            String mx = resolverProxy.resolveMX(domain).get(0);
            List<String> l = m.get(mx);
            if (l == null) {
                l = new ArrayList<String>();
                m.put(mx, l);
            }
            l.add(to);
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
