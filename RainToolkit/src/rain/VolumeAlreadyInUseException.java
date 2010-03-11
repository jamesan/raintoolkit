/*
 * Created on Nov 16, 2008
 *
 */
package rain;

public class VolumeAlreadyInUseException extends Exception {

	private Volume volume;

	public VolumeAlreadyInUseException(Volume vol2) {
		this.volume=vol2;
	}

	public Volume getVolume() {
		return volume;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

}
