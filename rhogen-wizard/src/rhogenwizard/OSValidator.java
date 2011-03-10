package rhogenwizard;

public class OSValidator
{
	public enum OSType
	{
		MACOS,
		WINDOWS,
		UNIXLIKE,
		UNDEFINED,
	};
	
	public static OSType detect()
	{
		if(isWindows()){
			return OSType.WINDOWS;
		}else if(isMac()){
			return OSType.MACOS;
		}else if(isUnix()){
			return OSType.UNIXLIKE;
		}
		
		return OSType.UNDEFINED;
	}
 
	public static boolean isWindows(){
 
		String os = System.getProperty("os.name").toLowerCase();
		//windows
	    return (os.indexOf( "win" ) >= 0); 
 
	}
 
	public static boolean isMac(){
 
		String os = System.getProperty("os.name").toLowerCase();
		//Mac
	    return (os.indexOf( "mac" ) >= 0); 
 
	}
 
	public static boolean isUnix(){
 
		String os = System.getProperty("os.name").toLowerCase();
		//linux or unix
	    return (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0);
 
	}
}