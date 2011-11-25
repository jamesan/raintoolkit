/*
 * Created on Nov 5, 2008
 *
 */
package rain.test;

import java.util.List;


import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.security.AWSCredentials;

import rain.BaseJPADAO;
import rain.DefaultVirtualMachineDAO;
import rain.DefaultVolumeDAO;
import rain.JPAVirtualMachineDAO;
import rain.JPAVolumeDAO;
import rain.VirtualMachine;
import rain.Volume;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import junit.framework.TestCase;

public class JPAVolumeDAOTestCase extends BaseJPATestCase {

	private JPAVolumeDAO dao;

	public void setUp() throws Exception {

		super.setUp();
		dao=new JPAVolumeDAO();
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
