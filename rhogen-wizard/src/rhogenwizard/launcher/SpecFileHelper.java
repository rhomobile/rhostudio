package rhogenwizard.launcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.eclipse.core.resources.IProject;
import rhogenwizard.buildfile.AppYmlFile;

public class SpecFileHelper 
{
	private static final String startPathKey = "start_path";
	private static final String mspecKey     = "mspec";
	private static final String fileUtilKey  = "fileutils";
	
	private IProject   m_selProject = null;
	private AppYmlFile m_ymlConfig = null;
	
	public SpecFileHelper(IProject project)
	{
		m_selProject = project;
	}
	
	private void addSpecExtension()
	{
		try 
		{
			m_ymlConfig = AppYmlFile.createFromProject(m_selProject);
			
			List<String> genExt = m_ymlConfig.getGeneralExtension();
			
			genExt.add(mspecKey);
			genExt.add(fileUtilKey);
			
			m_ymlConfig.setGeneralExtension(genExt);
			
			m_ymlConfig.save();
		}
		catch (FileNotFoundException e) 
		{
			m_ymlConfig = null;
			e.printStackTrace();
		}
	}

	private void removeSpecExtension()
	{
		try 
		{
			m_ymlConfig = AppYmlFile.createFromProject(m_selProject);
			
			List<String> genExt = m_ymlConfig.getGeneralExtension();
			
			genExt.remove(mspecKey);
			genExt.remove(fileUtilKey);
			
			m_ymlConfig.setGeneralExtension(genExt);
			
			m_ymlConfig.save();
		}
		catch (FileNotFoundException e) 
		{
			m_ymlConfig = null;
			e.printStackTrace();
		}
	}
	
	public void changeForSpec() throws IOException
	{
//		String pathToConfigFile = m_selProject.getLocation().toOSString() + File.separator + "rhoconfig.txt";
//		String pathToTmpConfigFile = m_selProject.getLocation().toOSString() + File.separator + "rhoconfig-tmp.txt";
//		
//		createFileWithNewStartPage("\'/app/SpecRunner\'", pathToConfigFile, pathToTmpConfigFile);
//		
//		renameFile(pathToTmpConfigFile, pathToConfigFile);
//		
//		addSpecExtension();
	}
	
	public void changeForApp() throws IOException
	{
//		String pathToConfigFile = m_selProject.getLocation().toOSString() + File.separator + "rhoconfig.txt";
//		String pathToTmpConfigFile = m_selProject.getLocation().toOSString() + File.separator + "rhoconfig-tmp.txt";
//		
//		createFileWithNewStartPage("\'/app\'", pathToConfigFile, pathToTmpConfigFile);
//		
//		renameFile(pathToTmpConfigFile, pathToConfigFile);
//		
//		removeSpecExtension();
	}
	
	private void createFileWithNewStartPage(String newOptionsPath, String configFile, String tmpConfigFile) throws IOException
	{
		FileInputStream configFileStream = new FileInputStream(configFile); 
			
		BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(new File(tmpConfigFile), true));

		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(configFileStream));
		String fileLine = null; 
		 
		while ((fileLine = bufferReader.readLine()) != null)   
		{
			if (fileLine.contains(startPathKey))
			{
				fileLine = startPathKey + " = " + newOptionsPath;
			}
			
			bufferWriter.write(fileLine);
			bufferWriter.newLine();
		}
		
		bufferWriter.close();
		bufferReader.close();
		configFileStream.close();
	}
	
	private void renameFile(String srcFile, String dstFile) throws IOException
	{
		File srcFileHandle = new File(srcFile);
		 
		File dstFileHadle = new File(dstFile);
		dstFileHadle.delete();
		
		// Move file to new directory
		boolean success = srcFileHandle.renameTo(dstFileHadle);
		
		if (!success) 
		{
			throw new IOException("rename file is impossible. source=" + srcFile + " destination=" + dstFile);
		}
		
		srcFileHandle.delete();
	}
}
