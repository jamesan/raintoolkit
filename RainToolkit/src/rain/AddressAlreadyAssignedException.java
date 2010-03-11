/*
 * Created on Nov 4, 2008
 *
 */
package rain;

public class AddressAlreadyAssignedException extends Exception {

	private String ipAddress;
	private String instanceId;

	public AddressAlreadyAssignedException() {
		
	}

	public AddressAlreadyAssignedException(String ipAddress,String instanceId) {
		this.ipAddress=ipAddress;
		this.instanceId=instanceId;
		
		
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}



}
