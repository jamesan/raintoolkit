/*
 * Created on Nov 16, 2008
 *
 */
package rain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sampullara.cli.Argument;

public class BackupVolumeCommand extends BaseCommand {

	static {
		thisClass = BackupVolumeCommand.class;
	}

	@Argument(description = "Volume id")
	private String id;

	@Argument(description = "Volume name", alias = "n")
	private String volume;

	@Argument(description = "Maximum retention period. For instance: 1h (one hour), 2w (2 weeks), 3d (3 days) ", alias="r")
	private String retentionPeriod;

	public void run() {

		if (id == null && volume == null) {
			System.out.println("You must specify a volume name or a volume id");
			System.exit(1);
			return;
		}
		RainEngine engine = RainEngine.getInstance();
		String snapshotId;

		long maximumRetentionAge;
		try {
			maximumRetentionAge = calculateMaximumRetentionAge();
		} catch (Exception e) {
			System.err.println("Invalid retention period (use -h for help)");
			System.exit(1);
			return;

		}
		try {
			snapshotId = engine.backupVolume(volume, id, maximumRetentionAge);
		} catch (VolumeNotFoundException e) {
			System.err.println("Volume not found: " + e.getVolumeId());
			System.exit(1);
			return;

		} catch (SnapshotNotFoundException e) {
			System.err.println("Snapshot not found: " + e.getSnapshot());
			System.exit(1);
			return;
		}

		System.out.println(snapshotId);
		System.exit(0);

	}

	private long calculateMaximumRetentionAge() {
		if (retentionPeriod == null)
			return 0;

		Pattern retentionPattern = Pattern.compile("([0-9]+)([h|d|w])");
		Matcher m=retentionPattern.matcher(retentionPeriod);
		if(!m.matches())
			throw new RuntimeException("Invalid retention period format");
		
		int quantity=Integer.parseInt(m.group(1));
		String unit=m.group(2);
		long result=0;
		
		result=quantity*3600*1000;  // hour
		
		if(unit.equals("d") || unit.equals("w") )
			result=result*24;
		
		if( unit.equals("w") )
			result=result*7;
		
		return result;

	}

}
