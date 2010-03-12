/*
 * Created on Nov 5, 2008
 *
 */
package rain.test;

import java.util.List;


import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.security.AWSCredentials;

import rain.DefaultVirtualMachineDAO;
import rain.DefaultVolumeDAO;
import rain.VirtualMachine;
import rain.Volume;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import junit.framework.TestCase;

public class DefaultVolumeDAOTestCase extends TestCase {

	private DefaultVolumeDAO dao;

	public void setUp() {

		// Create the S3 service
		
		AWSCredentials credentials=new AWSCredentials("xxx","xx");
		
		try {
			RestS3Service service=new RestS3Service(credentials);
			
			dao=new DefaultVolumeDAO();
			dao.setS3Service(service);
			
			
			
			dao.setSetBucketName("elastic-rain-444330339246");
			dao.setFileName("volumes-unitTest.xml");
			
			
			
		} catch (S3ServiceException e) {
			throw new RuntimeException(e);
		}
		
	
	}

	
	public void testSaveOrUpdate() {
		
		Volume vol=new Volume();
		vol.setName("TestVolume");
		vol.setDevice("/dev/sdd");
		vol.setVolumeId("vol-123456");
		vol.setMountPoint("/backup");
		
		dao.saveOrUpdate(vol);
		
		
		List<Volume> volumes=dao.findAll();
		assertEquals(1,volumes.size());
		Volume vol2=volumes.get(0);
		assertEquals(vol.getName(),vol2.getName());
		assertEquals(vol.getDevice(),vol2.getDevice());
		assertEquals(vol.getMountPoint(),vol2.getMountPoint());
		assertEquals(vol.getVolumeId(),vol2.getVolumeId());
		
		
	}
}
