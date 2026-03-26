package be.R0B0TB0SS.launcher.utils.authentification;

import java.io.IOException;

public class MicrosoftAuthenticationException extends Exception
{
    public MicrosoftAuthenticationException(String message)
    {
        super(message);
    }

    public MicrosoftAuthenticationException(IOException cause)
    {
        super("I/O exception thrown during Microsoft HTTP requests", cause);
    }

    public MicrosoftAuthenticationException(Throwable cause)
    {
        super(cause);
    }
}
