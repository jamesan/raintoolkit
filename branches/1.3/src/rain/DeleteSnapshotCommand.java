/*
 * Created on Nov 19, 2008
 *
 */
package rain;

import com.sampullara.cli.Argument;

public class DeleteSnapshotCommand extends BaseCommand {

	static {
		thisClass=DeleteSnapshotCommand.class;
	}
	@Argument(description="Snapshot id",alias="s", required=true)
	private String snapshotId;
	public void run() {
		
		RainEngine engine=RainEngine.getInstance();
		
		try {
			engine.deleteSnapshot(snapshotId);
		} catch (SnapshotNotFoundException e) {
			output.printError("Snapshot not found: "+e.getSnapshot());
		}
		

	}

}
