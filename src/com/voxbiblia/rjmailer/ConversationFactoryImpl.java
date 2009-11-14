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

    public Conversation getConversation(String smtpServer)
    {
        SMTPConversation c = new SMTPConversation(ehloHostname, smtpServer);
        c.setSocketFactory(socketFactory);
        c.setFieldGenerator(fieldGenerator);
        return c;
    }

    public void setEhloHostname(String ehloHostname)
    {
        this.ehloHostname = ehloHostname;
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
