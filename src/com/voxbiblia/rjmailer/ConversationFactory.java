package com.voxbiblia.rjmailer;

/**
 * Implementations of this interface know how to create server conversation
 * instances.
 */
public interface ConversationFactory
{
    Conversation getConversation(String smtpServer);
}
