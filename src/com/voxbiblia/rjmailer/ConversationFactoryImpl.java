package com.voxbiblia.rjmailer;

/**
 * The default ConversationFactory implementation, spitting out
 * SMTPConversation instances
 */
public class ConversationFactoryImpl implements ConversationFactory
{
    private String ehloHostname;
    private SocketFactory socketFactory;
    private FieldGenerator fieldGenerator;
    private int smtpPort = -1;

    public Conversation getConversation(String smtpServer)
    {
        SMTPConversation c = new SMTPConversation(ehloHostname, smtpServer);
        c.setSocketFactory(socketFactory);
        c.setFieldGenerator(fieldGenerator);
        if (smtpPort != -1) {
            c.setSmtpPort(smtpPort);
        }
        return c;
    }

    public void setEhloHostname(String ehloHostname)
    {
        this.ehloHostname = ehloHostname;
    }

    public void setSmtpPort(int smtpPort)
    {
        this.smtpPort = smtpPort;
    }


    public void setSocketFactory(SocketFactory socketFactory)
    {
        this.socketFactory = socketFactory;
    }

    public void setFieldGenerator(FieldGenerator fieldGenerator)
    {
        this.fieldGenerator = fieldGenerator;
    }
}
