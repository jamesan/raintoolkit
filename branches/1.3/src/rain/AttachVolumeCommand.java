/*
 * Created on Nov 5, 2008
 *
 */
package rain;

import com.sampullara.cli.Argument;

public class AttachVolumeCommand extends BaseCommand {

	static {
		thisClass=AttachVolumeCommand.class;
	}
	
	@Argument(description="Volume name",alias="v",required=true)
	private String volume;
	@Argument(description="Virtual machine name",alias="n",required=true)
	private String virtualMachine;
	@Argument(description="Attach device name",alias="d",required=true)
	private String device;
	@Argument(description="Mount point (if the volume should be automatically mounted)",alias="m")
	private String mountPoint;
	
	@Argument(description="Mount device name",alias="a",required=false)
	private String mountDevice;
	
	public void run() {
		

		RainEngine engine=RainEngine.getInstance();
		try {
			engine.attachVolume(volume,virtualMachine,device,mountDevice,mountPoint);
			System.exit(0);
			return;
		} catch (VolumeNotFoundException e) {
			System.err.println("Volume "+volume+" not found");
		} catch (VolumeAlreadyAttachedException e) {
			System.err.println("Volume "+volume+" is already attached to vm "+e.getVolume().getCurrentMachine().getName());
		} catch (VirtualMachineNotFoundException e) {
			System.err.println("Virtual machine "+virtualMachine+" not found");
		} catch (MountPointAlreadyInUseException e) {
			System.err.println("Mount point "+mountPoint+" is already in use in virtual machine "+virtualMachine);
		} catch (DeviceAlreadyInUseException e) {
			System.err.println("Device "+device+" is already in use in virtual machine "+virtualMachine);
		}
		
		System.exit(1);
		

	}

}
