/*
 * Created on Nov 19, 2008
 *
 */
package rain;

import com.sampullara.cli.Argument;

public class DeleteVolumeCommand extends BaseCommand {

	static {
		thisClass=DeleteVolumeCommand.class;
	}
	@Argument(description="Volume name",alias="n")
	private String volumeName;
	@Argument(description="Volume id",alias="i")
	private String volumeId;
	
	public void run() {
		
		if(volumeName==null && volumeId==null) {
			output.printError("You must specify either a volume name or a volume id");
			System.exit(1);
			return;
		}
		
		if(volumeName!=null && volumeId!=null) {
			output.printError("You cannot specify both a volume name and volume id");
			System.exit(1);
			return;
		}
		
		RainEngine engine=RainEngine.getInstance();
		try {
		if(volumeId!=null) {
		
				engine.deleteEC2Volume(volumeId);
		}
		else
			engine.deleteVolume(volumeName);
		} catch (VolumeNotFoundException e) {
			output.printError("Volume not found: "+e.getVolumeId());
		}

	}

}
