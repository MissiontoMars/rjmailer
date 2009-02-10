package com.voxbiblia.rjmailer;

import java.util.List;

/**
 * Contains MX Data
 */
class MXData
{
    private String server;
    private List recipients;

    public MXData(String server, List recipients)
    {
        this.server = server;
        this.recipients = recipients;
    }

    public String getServer()
    {
        return server;
    }

    public List getRecipients()
    {
        return recipients;
    }
}
