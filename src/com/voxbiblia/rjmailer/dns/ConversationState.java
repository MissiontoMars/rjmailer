package com.voxbiblia.rjmailer.dns;

/**
 * Contains information about the state of an UDP query-response-cycle.
 *
 * @author Noa Resare (noa@voxbiblia.com)
 */
class ConversationState
{
    private byte[] response;
    private int id;
    private byte[] query;
    private Throwable exception;

    public byte[] getResponse()
    {
        return response;
    }

    public void setResponse(byte[] response)
    {
        this.response = response;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public byte[] getQuery()
    {
        return query;
    }

    public void setQuery(byte[] query)
    {
        this.query = query;
    }

    public Throwable getException()
    {
        return exception;
    }

    public void setException(Throwable exception)
    {
        this.exception = exception;
    }
}
