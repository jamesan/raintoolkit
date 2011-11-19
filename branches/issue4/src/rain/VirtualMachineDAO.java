/*
 * Created on Nov 3, 2008
 *
 */
package rain;

import java.util.List;

/**
 * This interface specifies data access methods for the
 * VirtualMachine entity
 * @author juliano
 * (c) 2008 Boltblue International Limited
 */
public interface VirtualMachineDAO {
	
	public List<VirtualMachine> findAll();
	
	public VirtualMachine findByName(String name);
	
	public VirtualMachine findByStaticIpAddress(String ip);
	
	public void saveOrUpdate(VirtualMachine vm);
	
	public void delete(VirtualMachine vm);

}
