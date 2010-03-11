import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import rain.VirtualMachine;
import rain.Volume;

import junit.framework.TestCase;

import com.spaceprogram.simplejpa.EntityManagerFactoryImpl;

/*
 * Created on Nov 6, 2008
 *
 */

public class SimpleJPATestCase extends TestCase {
	
	
	public void testSimpleDbJPA() {
		
		Properties prop=new Properties();
		prop.put("accessKey","0QWXVVCMRE4KQMZGNE82");
		prop.put("secretKey","XTS5kbLFIHJ60ozJDN241Kt7rpCTY7yr14ZqxeWn");
		prop.put("sessionless", "true");
		
		// Create EntityManagerFactory. This should be a global object that you reuse.
		EntityManagerFactoryImpl factory = new EntityManagerFactoryImpl("persistenceUnitName", prop);
		// Get an EntityManager from the factory. This is a short term object that you'll use for some processing then throw away
		EntityManager em = factory.createEntityManager();
		VirtualMachine test=new VirtualMachine();
		test.setName("testvm-1");
		test.setImage("ami-e8aa4e81");
		test.setAvailabilityZone("us-east-1c");
		test.setKeypair("gsg-keypair");
		
		Volume vol=new Volume();
		vol.setName("TestVolume");
		vol.setDevice("/dev/sdd");
		vol.setVolumeId("vol-123456");
		vol.setMountPoint("/backup");
		
		List<Volume> volumes=new ArrayList<Volume>();
		volumes.add(vol);
		test.setVolumes(volumes);
		vol.setCurrentMachine(test);
		em.persist(vol);
		em.persist(test);
		
		em.getTransaction().commit();
		em.close();
		factory.close();
		
		factory = new EntityManagerFactoryImpl("persistenceUnitName", prop);
		// Get an EntityManager from the factory. This is a short term object that you'll use for some processing then throw away
		em = factory.createEntityManager();
		
		Query query = em.createQuery("select v from rain.VirtualMachine v where v.name=:name");
		query.setParameter("name","testvm-1");
		List<VirtualMachine> result=query.getResultList();
		
		assertEquals(1,result.size());
		VirtualMachine vm=result.get(0);
		assertEquals(test.getName(),vm.getName());
		assertEquals(test.getImage(),vm.getImage());
		assertEquals(test.getAvailabilityZone(),vm.getAvailabilityZone());
		assertEquals(test.getKeypair(),vm.getKeypair());
		List<Volume> vmVolumes=vm.getVolumes();
		assertEquals(1,vmVolumes.size());
		Volume vol2=vmVolumes.get(0);
		assertEquals(vol.getName(),vol2.getName());
		assertEquals(vol.getMountPoint(),vol2.getMountPoint());
		assertEquals(vol.getDevice(),vol2.getDevice());
		assertEquals(vol.getVolumeId(),vol2.getVolumeId());
		
		
	}

}
