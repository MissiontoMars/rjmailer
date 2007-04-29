package com.voxbiblia.rjmailer;

/**
 * Indicates that there is a permanent error when resolving the domain name
 * of the given email address. It can mean that the domain is missing or
 * that is not configured to recieve email messages.
 */
public class RJMDomainException
        extends RJMException
{

    public RJMDomainException(String msg)
    {
        super(msg);
    }

    public RJMDomainException(String message, Throwable t)
    {
        super(message, t);
    }
}
