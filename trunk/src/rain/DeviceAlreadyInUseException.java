/*
 * Created on Nov 6, 2008
 *
 */
package rain;

public class DeviceAlreadyInUseException extends Exception {

	private String device;

	public DeviceAlreadyInUseException(String device) {
		this.device=device;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	
}
