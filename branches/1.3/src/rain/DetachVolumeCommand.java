/*
 * Created on Nov 27, 2008
 *
 */
package rain;

import com.sampullara.cli.Argument;

public class DetachVolumeCommand  extends BaseCommand{

	static {
		thisClass=DetachVolumeCommand.class;
	}

	@Argument(description="Volume name", required=true,alias="n")
	private String volumeName;
	public void run() {
		
		RainEngine engine=RainEngine.getInstance();
		
		try {
			engine.detachVolume(volumeName);
			System.exit(0);
		} catch (VolumeNotFoundException e) {
			output.printError("Volume not found: "+volumeName);
		} catch (VolumeNotAttachedException e) {
			output.printError("Volume "+volumeName+" is not attached to any virtual machine");
		}
		
		System.exit(1);
		
		
		
		
	}

}
