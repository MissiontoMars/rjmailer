package com.voxbiblia.rjmailer;

/**
 * Created by IntelliJ IDEA.
 * User: noa
 * Date: Apr 7, 2007
 * Time: 9:17:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class RJMParseException
    extends RJMException
{
    public RJMParseException(String msg)
    {
        super(msg);
    }

    public RJMParseException(String message, Throwable t)
    {
        super(message, t);
    }
}