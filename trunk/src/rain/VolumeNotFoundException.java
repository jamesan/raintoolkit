/*
 * Created on Nov 6, 2008
 *
 */
package rain;

public class VolumeNotFoundException extends Exception {

	public String getVolumeId() {
		return volumeId;
	}

	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}

	private String volumeId;

	public VolumeNotFoundException(String volumeId) {
		this.volumeId=volumeId;
	}

}
