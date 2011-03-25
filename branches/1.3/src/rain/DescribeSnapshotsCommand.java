/*
 * Created on Nov 16, 2008
 *
 */
package rain;

import java.util.List;

import com.sampullara.cli.Argument;

public class DescribeSnapshotsCommand extends BaseCommand {

	static {
		thisClass=DescribeSnapshotsCommand.class;
	}
	
	@Argument(description="Volume name")
	private String volumeName;
	
	@Argument(description="Volume id")
	private String volumeId;
	
	public void run() {
		
		List<SnapshotDescription> snapshots;
		RainEngine engine=RainEngine.getInstance();
		
		output.startSection("");
		
			try {
				snapshots=engine.describeSnapshots(volumeName,volumeId);
				for(SnapshotDescription s: snapshots) {
					output.startLine();
					output.printValue("SnapshotId",s.getSnapshotId());
					output.printValue("Volume",(s.getVolume()==null?s.getVolumeId():s.getVolume().getName()));
					output.printValue("Status",s.getStatus());
					output.printValue("Progress",s.getPercentComplete());
					output.printValue("StartTime",formatIso8601Date(s.getSnapshotTime()));
					
					output.endLine();
					
					
				}
			} catch (VolumeNotFoundException e) {
			
				output.printError("Volume not found: "+e.getMessage());
				System.exit(1);
				return;
				
			}
		
			output.endSection();

	}

}
