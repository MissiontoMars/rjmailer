package com.voxbiblia.rjmailer;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A separate class that aims to reproduce the different errors.
 *
 */
public class SendFailureTest
    extends TestCase
{

    public static Logger log = LoggerFactory.getLogger(SendFailureTest.class);

    public void testSendFailure()
    {
        RJMMessage m = new RJMMessage();
        m.addTo("a b@c");
        m.setFrom("noa@resare.com");
        RJMSender s = new RJMSender("ehloName");
        // this smtp server will not be used, but needs to be set to something
        s.setSmtpServer("localhost");
        try {
            s.send(m);
            fail("should not be able to send with syntax error in email");
        } catch (RJMException e) {
            assertEquals(e.getExactCause(), ExactCause.ADDRESS_SYNTAX);
        }
    }

    public void testDomainLookupNXDOMAIN()
    {
        RJMMessage m = new RJMMessage();
        m.addTo("a.b@nonexistant");
        m.setFrom("noa@resare.com");
        RJMSender s = new RJMSender("ehloName");
        s.setResolver(new DummyResolver());
        s.setSmtpServer("anything");
        try {
            s.send(m);
            fail("should have indicated nonexistant recipient domain");
        } catch (RJMException e) {
            assertEquals(e.getExactCause(), ExactCause.DOMAIN_NOT_FOUND);
            assertEquals("nonexistant", e.getDomain());
        }
    }

    public void testDomainLookupSERVFAIL()
    {
        RJMMessage m = new RJMMessage();
        m.addTo("a.b@servfail-domain");
        m.setFrom("noa@resare.com");
        RJMSender s = new RJMSender("ehloName");
        s.setResolver(new DummyResolver());
        s.setSmtpServer("anything");
        try {
            s.send(m);
            fail("should have indicated nonexistant recipient domain");
        } catch (RJMException e) {
            assertEquals(e.getExactCause(), ExactCause.DOMAIN_FAILURE);
        }
    }

    public void testHostNotFoundFailure()
            throws Exception
    {
        RJMMessage m = new RJMMessage();
        m.addTo("a.b@mx-incorrectly-configured");
        m.setFrom("noa@resare.com");
        RJMSender s = new RJMSender("ehloName");
        DummyResolver dr = new DummyResolver();
        dr.addData("mx-incorrectly-configured", "outer-space");
        s.setResolver(dr);
        s.setSocketFactory(new DummySocketFactory());
        try {
            s.send(m);
            fail("Should have thrown RJMException");
        } catch (RJMException e) {
            assertEquals(ExactCause.ALL_SERVERS_FAILED, e.getExactCause());
            List<RJMException> softs = e.getSoftFailures();
            assertEquals(1, softs.size());
            //noinspection ThrowableResultOfMethodCallIgnored
            assertEquals(ExactCause.DOMAIN_INVALID, softs.get(0).getExactCause());
        }
    }

    public void testNonexistantUser()
    {
        RJMMessage m = new RJMMessage();
        m.addTo("doesnotexist@resare.com");
        m.setFrom("noa@resare.com");
        RJMSender s = new RJMSender("a.b.c");
        s.setConversationFactory(new ConversationFactory() {
            public Conversation getConversation(String smtpServer)
            {
                return new DummyConversation();
            }
        });
        
        try {
            s.send(m);
            fail("should result in RJMException");
        } catch (RJMException e) {
            assertEquals(ExactCause.MAILBOX_UNAVAILABLE, e.getExactCause());
        }
    }


    public void testRealTLS()
    {
        RJMMessage m = new RJMMessage();
        m.addTo("noa@resare.com");
        m.setFrom("noa@resare.com");
        m.setText("test test");
        m.setSubject("test test");
        RJMSender s = new RJMSender("viktor.resare.com");

        s.setSmtpPort(10025);
        RJMResult r = s.send(m);
        // openssl x509 -fingerprint -sha1 -noout -in /usr/share/ssl/certs/postfix.crt
        assertEquals("83:B0:F8:D7:5C:C4:5D:84:38:A5:A1:36:BA:3B:C6:EA:F6:CC:5D:70",
                r.getTlsCertHash());
        assertEquals("TLS_DHE_RSA_WITH_AES_128_CBC_SHA", r.getTlsCipherSuite());
    }
}
