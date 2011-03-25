/*
 * Created on Nov 16, 2008
 *
 */
package rain;

import java.util.Calendar;

public class SnapshotDescription {
	
	private Volume volume;
	
	private String volumeId;
	
	public String getVolumeId() {
		return volumeId;
	}

	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}

	private Calendar snapshotTime;
	
	private String percentComplete;
	
	private String status;
	
	public String getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(String percentComplete) {
		this.percentComplete = percentComplete;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Volume getVolume() {
		return volume;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	public Calendar getSnapshotTime() {
		return snapshotTime;
	}

	public void setSnapshotTime(Calendar snapshotTime) {
		this.snapshotTime = snapshotTime;
	}

	public String getSnapshotId() {
		return snapshotId;
	}

	public void setSnapshotId(String snapshotId) {
		this.snapshotId = snapshotId;
	}

	private String snapshotId;
	

}
