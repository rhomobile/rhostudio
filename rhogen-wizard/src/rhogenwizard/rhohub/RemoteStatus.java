package rhogenwizard.rhohub;

public enum RemoteStatus
{
    eQueued,
    eStarted,
    eComplete,
    eFailed,
    eUnknow;

    public static String statusQueued   = "queued";
    public static String statusComplete = "completed";
    public static String statusFailed   = "failed";
    public static String statusStarted  = "started";
    
    @Override
    public String toString()
    {
        switch (this)
        {
        case eQueued:
            return statusQueued;
        case eComplete:
            return statusComplete;
        case eFailed:
            return statusFailed;
        case eStarted:
            return statusStarted;
        }

        return "";
    }
    
    public static RemoteStatus fromString(final String enumString)
    {
        if (enumString.toLowerCase().equals(statusQueued))
            return eQueued;
        else if(enumString.toLowerCase().equals(statusComplete))
            return eComplete;
        else if(enumString.toLowerCase().equals(statusFailed))
            return eFailed;
        else if(enumString.toLowerCase().equals(statusStarted))
            return eStarted;
        
        return eUnknow;
    }
}
