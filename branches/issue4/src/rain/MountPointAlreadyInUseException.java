/*
 * Created on Nov 6, 2008
 *
 */
package rain;

public class MountPointAlreadyInUseException extends Exception {

	private String mountPoint;

	public MountPointAlreadyInUseException(String mountPoint) {
		this.mountPoint=mountPoint;
	}

	public String getMountPoint() {
		return mountPoint;
	}

	public void setMountPoint(String mountPoint) {
		this.mountPoint = mountPoint;
	}
	

}
