/*
 * Created on Nov 6, 2008
 *
 */
package rain;

public class VolumeAlreadyExistsException extends Exception {

	private Volume volume;

	public VolumeAlreadyExistsException(Volume volume) {
		this.volume=volume;
	}

	public Volume getVolume() {
		return volume;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}
	
	

}
