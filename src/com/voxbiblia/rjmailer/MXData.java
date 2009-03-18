package com.voxbiblia.rjmailer;

import java.util.List;

/**
 * Contains MX Data
 */
class MXData
{
    private String server;
    private List<String> recipients;

    public MXData(String server, List<String> recipients)
    {
        this.server = server;
        this.recipients = recipients;
    }

    public String getServer()
    {
        return server;
    }

    public List<String> getRecipients()
    {
        return recipients;
    }
}
