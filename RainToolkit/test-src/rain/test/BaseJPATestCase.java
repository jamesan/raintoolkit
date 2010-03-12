/*
 * Created on Nov 20, 2008
 *
 */
package rain.test;

import org.apache.commons.codec.binary.Base64;

import rain.BaseJPADAO;

import junit.framework.TestCase;

public class BaseJPATestCase extends TestCase {

	@Override
	protected void setUp() throws Exception {
		
		BaseJPADAO.setAwsAccessId("xxx");
		BaseJPADAO.setAwsSecretKey("xxx");
		BaseJPADAO.setPersistenceUnitName("unitTest");
		
	}

	
	public static void main(String[] args) {
		
		
		byte[] accessId=Base64.decodeBase64("xxx".getBytes());
		byte[] secretKey=Base64.decodeBase64("xxx".getBytes());
		
		System.err.println("Access id length: "+accessId.length+" secret key length: "+secretKey.length);
		
	}
	

}
