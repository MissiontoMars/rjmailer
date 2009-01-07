package com.voxbiblia.rjmailer;

import java.util.List;

/**
 * Contains MX Data
 */
class MXData
{
    private String server;
    private List recipients;

    public String getServer()
    {
        return server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public List getRecipients()
    {
        return recipients;
    }

    public void setRecipients(List recipients)
    {
        this.recipients = recipients;
    }
}
