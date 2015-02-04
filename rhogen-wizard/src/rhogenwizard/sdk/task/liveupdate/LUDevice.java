package rhogenwizard.sdk.task.liveupdate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.yaml.snakeyaml.Yaml;

public class LUDevice 
{
	public static final String configFileName = "dev-config.yml"; 
	
	public String URI         = null;
	public String Name        = null;
	public String Platfrom    = null;
	public String Application = null;
	
	static public List<LUDevice> load(IPath path) throws FileNotFoundException 
	{
		List<LUDevice> devices = new ArrayList<LUDevice>();
		
		File       ymlFile = new File(path.toOSString());
		Yaml       yaml    = new Yaml();		
		FileReader fr      = new FileReader(ymlFile);
		
		Map<Object, Object> rawData = cast(yaml.load(fr));		
		List<HashMap<String, String>> rawDevices = (List<HashMap<String, String>>)rawData.get("devices");
		
		if (rawDevices == null)
			return null;
		
		for (HashMap<String, String> device : rawDevices) 
		{
			LUDevice outDevice = new LUDevice();
			
			outDevice.Application = device.get("application"); 
			outDevice.Name        = device.get("name");
			outDevice.Platfrom    = device.get("platform");
			outDevice.URI         = device.get("uri");

			devices.add(outDevice);
		} 
		
		return devices;
	}
	
	@SuppressWarnings("unchecked")
	static private Map<Object, Object> cast(Object mapObject)
	{
        return (Map<Object, Object>) mapObject;
	}
} 
