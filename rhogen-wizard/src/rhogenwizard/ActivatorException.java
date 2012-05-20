package rhogenwizard;

public class ActivatorException extends RuntimeException
{
    private static final long serialVersionUID = -1810847289167344407L;

    public ActivatorException(String msg)
    {
        super(msg);
    }

    public ActivatorException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
