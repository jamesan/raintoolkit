/*
 * Created on Nov 16, 2008
 *
 */
package rain;

public class SnapshotNotFoundException extends Exception {

	private String snapshot;

	public SnapshotNotFoundException(String snapshot) {
		this.snapshot=snapshot;
		
	}

	public String getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(String snapshot) {
		this.snapshot = snapshot;
	}

}
