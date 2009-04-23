package com.voxbiblia.rjmailer;

import java.util.List;

/**
 * Instances of this class contains information about the sending of a message.
 */
public class RJMResult
    implements SendResult
{

    public enum Status {
        SENT, DEFERRED
    }

    private String recievingServer;
    private String result;
    private Status status;
    private List<RJMException> softFailures;

    public RJMResult(String recievingServer, String result, Status status)
    {
        this.recievingServer = recievingServer;
        this.result = result;
        this.status = status;
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

    public Status getStatus()
    {
        return status;
    }

    public List<RJMException> getSoftFailures()
    {
        return softFailures;
    }

    public void setSoftFailures(List<RJMException> softFailures)
    {
        this.softFailures = softFailures;
    }
}
