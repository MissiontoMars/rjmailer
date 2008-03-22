package com.voxbiblia.rjmailer;

/**
 * Created by IntelliJ IDEA.
 * User: noa
 * Date: Apr 7, 2007
 * Time: 9:17:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class RJMInputException
        extends RJMException
{
    /**
	 * Used to shut up eclipse build warnings
	 */
	private static final long serialVersionUID = 1L;

	
    public RJMInputException(String msg)
    {
        super(msg);
    }

    public RJMInputException(String message, Throwable t)
    {
        super(message, t);
    }
}
