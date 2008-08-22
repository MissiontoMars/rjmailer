package com.voxbiblia.rjmailer;

/**
 * Instances of this class contains information about a sent message.
 *
 * 
 */
public class RJMResult
{
    private String recievingServer;
    private String result;
    private boolean tls;

    public RJMResult(String recievingServer, String result)
    {
        this.recievingServer = recievingServer;
        this.result = result;
    }

    public String getRecievingServer()
    {
        return recievingServer;
    }

    public void setRecievingServer(String recievingServer)
    {
        this.recievingServer = recievingServer;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public boolean isTls()
    {
        return tls;
    }

    public void setTls(boolean tls)
    {
        this.tls = tls;
    }
}
