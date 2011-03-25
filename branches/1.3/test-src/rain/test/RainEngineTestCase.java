package rain.test;

import rain.RainEngine;
import junit.framework.TestCase;

public class RainEngineTestCase extends TestCase {

	
	
	public void testGetCurrentIPAddress() throws Exception {
		
		
		
		RainEngine engine=RainEngine.getInstance();
		String ip=engine.getCurrentInternetIPAddress();
		
		assertNotNull(ip);
		System.err.println(ip);
	}
}
