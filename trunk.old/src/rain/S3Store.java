/*
 * Created on Nov 5, 2008
 *
 */
package rain;

import java.util.ArrayList;
import java.util.List;

/**
 *  This class is used to hold a reference to the entities stored in S3
 * @author juliano
 * (c) 2008 Boltblue International Limited
 */
public class S3Store {
	
	private List<VirtualMachine> virtualMachines=new ArrayList<VirtualMachine>();
	private List<Volume> volumes=new ArrayList<Volume>();
	public List<VirtualMachine> getVirtualMachines() {
		return virtualMachines;
	}
	public void setVirtualMachines(List<VirtualMachine> virtualMachines) {
		this.virtualMachines = virtualMachines;
	}
	public List<Volume> getVolumes() {
		return volumes;
	}
	public void setVolumes(List<Volume> volumes) {
		this.volumes = volumes;
	}
	

}
