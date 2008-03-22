package com.voxbiblia.rjmailer;

/**
 * Indicates that there is a permanent error when resolving the domain name
 * of the given email address. It can mean that the domain is missing or
 * that is not configured to receive email messages.
 */
public class RJMDomainException
        extends RJMException
{

    /**
	 * Used to shut up eclipse build warnings
	 */
	private static final long serialVersionUID = 1L;

	public RJMDomainException(String msg)
    {
        super(msg);
    }

    public RJMDomainException(String message, Throwable t)
    {
        super(message, t);
    }
}
