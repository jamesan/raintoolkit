/*
 * Created on Nov 7, 2008
 *
 */
package rain;

public class AvailabilityZoneNotFoundException extends Exception {

	private String availabilityZone;

	public AvailabilityZoneNotFoundException(String availabilityZone) {
		this.availabilityZone=availabilityZone;
	}

	public String getAvailabilityZone() {
		return availabilityZone;
	}

	public void setAvailabilityZone(String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

}
