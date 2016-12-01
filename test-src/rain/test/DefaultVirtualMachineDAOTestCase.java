/*
 * Created on Nov 3, 2008
 *
 */
package rain.test;

import java.util.List;


import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.security.AWSCredentials;

import rain.DefaultVirtualMachineDAO;
import rain.S3Store;
import rain.VirtualMachine;
import rain.Volume;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import junit.framework.TestCase;

public class DefaultVirtualMachineDAOTestCase extends TestCase {

	private DefaultVirtualMachineDAO dao;

	public void setUp() {
		
		// Create the S3 service
		
		AWSCredentials credentials=new AWSCredentials("xxx","xxx");
		
		try {
			RestS3Service service=new RestS3Service(credentials);
			
			dao=new DefaultVirtualMachineDAO();
			dao.setS3Service(service);
			
		
			
			dao.setSetBucketName("elastic-rain-444330339246");
			dao.setFileName("machines-unitTest.xml");
			
			
			
		} catch (S3ServiceException e) {
			throw new RuntimeException(e);
		}
		
		
	}
	
	public void testSaveOrUpdate() throws Exception {
		
		VirtualMachine test=new VirtualMachine();
		test.setName("testvm-1");
		test.setImage("ami-e8aa4e81");
		test.setAvailabilityZone("us-east-1c");
		test.setKeypair("gsg-keypair");
		
		
		
		dao.saveOrUpdate(test);
		
		List<VirtualMachine> list=dao.findAll();
		assertEquals(1,list.size());
		VirtualMachine vm=list.get(0);
		assertEquals(test.getName(),vm.getName());
		assertEquals(test.getImage(),vm.getImage());
		assertEquals(test.getAvailabilityZone(),vm.getAvailabilityZone());
		assertEquals(test.getKeypair(),vm.getKeypair());
		
		test.setAvailabilityZone("us-east-1a");
		dao.saveOrUpdate(test);
		
		
		list=dao.findAll();
		assertEquals(1,list.size());
		vm=list.get(0);
		assertEquals(test.getName(),vm.getName());
		assertEquals(test.getImage(),vm.getImage());
		assertEquals(test.getAvailabilityZone(),vm.getAvailabilityZone());
		assertEquals(test.getKeypair(),vm.getKeypair());
		
		dao.delete(test);
		
		
	}
	
	
}
