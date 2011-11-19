/*
 * Created on Nov 16, 2008
 *
 */
package rain;

import com.sampullara.cli.Argument;

public class CreateSnapshotCommand extends BaseCommand {

	static {
		thisClass=CreateSnapshotCommand.class;
	}
	
	@Argument(description="Volume id")
	private String id;
	
	@Argument(description="Volume name" , alias="n")
	private String volume;
	
	
	public void run() {
		
		if(id==null && volume==null) {
			System.out.println("You must specify a volume name or a volume id");
			System.exit(1);
			return;
		}
		RainEngine engine=RainEngine.getInstance();
		String snapshotId;
		if(volume!=null)
			try {
				snapshotId=engine.createSnapshotByName(volume);
			} catch (VolumeNotFoundException e) {
				System.err.println("Volume not found: "+e.getMessage());
				System.exit(1);
				return;
				
			}
		else
			snapshotId=engine.createSnapshotById(id);
		
		System.out.println(snapshotId);
		System.exit(0);
		

	}

}
