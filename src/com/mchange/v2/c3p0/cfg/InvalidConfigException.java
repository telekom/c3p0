package com.mchange.v2.c3p0.cfg;

public final class InvalidConfigException extends Exception
{
    public InvalidConfigException( String message, Throwable cause )
    { super( message, cause ); }

    public InvalidConfigException( String message )
    { this( message, null ); }
}
