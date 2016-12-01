/*
 * Created on Nov 7, 2008
 *
 */
package rain;

public class StaticIpAddressNotFoundException extends Exception {

	private String staticIpAddress;

	public StaticIpAddressNotFoundException(String staticIpAddress) {
		
		this.staticIpAddress=staticIpAddress;
	}

	public String getStaticIpAddress() {
		return staticIpAddress;
	}

	public void setStaticIpAddress(String staticIpAddress) {
		this.staticIpAddress = staticIpAddress;
	}

}
