/*
 * Created on Nov 3, 2008
 *
 */
package rain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Volume {
	
	private String volumeId;
	private String name;
	
	private VirtualMachine currentMachine;
	
	@ManyToOne
	public VirtualMachine getCurrentMachine() {
		return currentMachine;
	}
	public void setCurrentMachine(VirtualMachine currentMachine) {
		this.currentMachine = currentMachine;
	}
	@Id
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private String device;
	private String mountPoint;
	private String mountDevice;
	
	
	
	public String getMountDevice() {
		return mountDevice;
	}
	public void setMountDevice(String mountDevice) {
		this.mountDevice = mountDevice;
	}
	public String getVolumeId() {
		return volumeId;
	}
	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getMountPoint() {
		return mountPoint;
	}
	public void setMountPoint(String mountPoint) {
		this.mountPoint = mountPoint;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Volume other = (Volume) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	

}
