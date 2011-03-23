package rhogenwizard;

public class StringHelper 
{
	public static String removeChar(String s, char c) 
	{
		  StringBuffer r = new StringBuffer( s.length() );
		  r.setLength( s.length() );
		  int current = 0;
	
		  for (int i = 0; i < s.length(); i ++) 
		  {
			  char cur = s.charAt(i);
			  if (cur != c) 
			  {
				  r.setCharAt( current++, cur );
			  }
		  }
		  
		  return r.toString();
	}

	public static String removeCharAt(String s, int pos) 
	{
		   StringBuffer buf = new StringBuffer( s.length() - 1 );
		   buf.append( s.substring(0,pos) ).append( s.substring(pos+1) );
		   return buf.toString();
	}
}
