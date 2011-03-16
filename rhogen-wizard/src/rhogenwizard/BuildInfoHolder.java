package rhogenwizard;

public class BuildInfoHolder 
{
	private static final String[] generalAttributesHelpStrings =
	{
          "Run, but do not make any changes. (-p, --pretend)",
		  "Overwrite files that already exist. (-f, --force)",
		  "Skip files that already exist. (-s, --skip)",
		  "Delete files that have previously been generated with this generator. (-d, --delete)",
		  "Do not catch errors (--debug)",
	};

	private static final String[] generalAttributesFlasg =
	{
          "--pretend",
		  "--force",
		  "--skip",
		  "--delete",
		  "--debug",
	};

	public String m_appName = null;
	public String m_appDir  = null;
	
	public boolean isPretend = false;
	public boolean isForce   = false;
	public boolean isSkip    = false;
	public boolean isDelete  = false;
	public boolean isDebug   = false;
	
	public String generateAttributeString()
	{
		StringBuilder sb = new StringBuilder();
		
		if (isPretend)
			sb.append(" " + generalAttributesFlasg[0]);
		
		if (isForce)
			sb.append(" " + generalAttributesFlasg[1]);
		
		if (isSkip)
			sb.append(" " + generalAttributesFlasg[2]);
		
		if (isDelete)
			sb.append(" " + generalAttributesFlasg[3]);
		
		if (isDebug)
			sb.append(" " + generalAttributesFlasg[4]);
		
		return sb.toString();
	}
	
	public static String[] getAttributesStrings()
	{
		return generalAttributesHelpStrings;
	}
}