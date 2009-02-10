package com.voxbiblia.rjmailer;

/**
 * The root exception of the RJMailer exception hierarchy.
 */
public class RJMException
    extends RuntimeException
{
    /**
	 * Used to shut up eclipse build warnings
	 */
	private static final long serialVersionUID = 1L;

    public RJMException(String message)
    {
        super(message);
    }

    public RJMException(String message, Throwable t)
    {
        super(message, t);
    }

    public RJMException(Throwable t)
    {
        super(t);
    }

}
