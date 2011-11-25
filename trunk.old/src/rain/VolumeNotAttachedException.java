/*
 * Created on Nov 27, 2008
 *
 */
package rain;

public class VolumeNotAttachedException extends Exception {

	private Volume volume;

	public VolumeNotAttachedException(Volume vol) {
		this.volume=vol;
	}

}
