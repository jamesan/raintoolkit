/*
 * Created on Nov 16, 2008
 *
 */
package rain;

import java.util.Calendar;

public class VolumeDescription {
	
	private String volumeId;
	
	private Volume volume;
	
	private Calendar createTime;
	
	private String availabilityZone;
	
	private int size;
	
	private String status;
	
	private Calendar attachTime;
	
	private String attachedInstanceId;
	
	private VirtualMachine attachedInstance;
	
	private String attachedDevice;
	

	public String getAttachedDevice() {
		return attachedDevice;
	}

	public void setAttachedDevice(String attachedDevice) {
		this.attachedDevice = attachedDevice;
	}

	public VirtualMachine getAttachedInstance() {
		return attachedInstance;
	}

	public void setAttachedInstance(VirtualMachine attachedInstance) {
		this.attachedInstance = attachedInstance;
	}

	public String getVolumeId() {
		return volumeId;
	}

	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}

	public Volume getVolume() {
		return volume;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	public Calendar getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Calendar createTime) {
		this.createTime = createTime;
	}

	public String getAvailabilityZone() {
		return availabilityZone;
	}

	public void setAvailabilityZone(String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Calendar getAttachTime() {
		return attachTime;
	}

	public void setAttachTime(Calendar attachTime) {
		this.attachTime = attachTime;
	}

	public String getAttachedInstanceId() {
		return attachedInstanceId;
	}

	public void setAttachedInstanceId(String attachedInstanceId) {
		this.attachedInstanceId = attachedInstanceId;
	}
	
	

}
