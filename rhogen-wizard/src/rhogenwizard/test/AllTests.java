package rhogenwizard.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests 
{
	public static Test suite() 
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$

		suite.addTest((Test) new CreateTest());
		
		//$JUnit-END$
		return suite;
	}

}
