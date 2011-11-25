/*
 * Created on Nov 3, 2008
 *
 */
package rain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;


/**
 * This default implementation of the VirtualMachineDAO interface stores virtual
 * machine information as serialized XML files on the S3 service
 * 
 * @author juliano (c) 2008 Boltblue International Limited
 */
public class DefaultVirtualMachineDAO  extends BaseS3DAO implements VirtualMachineDAO {

	public List<VirtualMachine> findAll() {

		return loadVirtualMachineList();

	}

	private List<VirtualMachine> loadVirtualMachineList() {

		return getMachines();
	
	}

	public VirtualMachine findByName(String name) {
		
		List<VirtualMachine> list=loadVirtualMachineList();
		for(VirtualMachine vm: list) {
			if(vm.getName().equals(name))
				return vm;
		}
		
		return null;
	}

	
	public void saveOrUpdate(VirtualMachine vm) {
		
		List<VirtualMachine> list=loadVirtualMachineList();
		VirtualMachine currentMachine=null;
		for(VirtualMachine v: list) {
			if(vm.getName().equals(v.getName())) {
				currentMachine=vm;
			}
			
		}
		
		if(currentMachine==null) {
			list.add(vm);
			
		}
		else {
			list.remove(currentMachine);
			list.add(vm);
			
		}
		
		saveEntityList();

	}

	

	
	public void delete(VirtualMachine vm) {
	
		List<VirtualMachine> list=loadVirtualMachineList();
		VirtualMachine currentMachine=null;
		for(VirtualMachine v: list) {
			if(vm.getName().equals(v.getName())) {
				currentMachine=vm;
				break;
			}
			
		}
		
		if(currentMachine!=null) {
			list.remove(currentMachine);
			saveEntityList();
		}

		
	}

	public VirtualMachine findByStaticIpAddress(String ip) {
		// TODO Auto-generated method stub
		return null;
	}

}
