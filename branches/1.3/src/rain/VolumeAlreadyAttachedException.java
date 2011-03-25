/*
 * Created on Nov 4, 2008
 *
 */
package rain;

public class VolumeAlreadyAttachedException extends Exception {

	private Volume volume;

	public VolumeAlreadyAttachedException(Volume vol) {
		this.volume=vol;
	}

	public Volume getVolume() {
		return volume;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	

}
