/*
 * Created on Nov 4, 2008
 *
 */
package rain;

import com.sampullara.cli.Argument;

public class StartVirtualMachineCommand extends BaseCommand {

	static {
	thisClass=StartVirtualMachineCommand.class;
	}
	
	@Argument(description="Virtual Machine Name", alias="n",required=true)
	private String name;
	
	public void run() {
		
	
		RainEngine engine=RainEngine.getInstance();
		try {
			System.out.println(engine.startVirtualMachine(name));
			System.exit(0);
			return;
		} catch (VirtualMachineNotFoundException e) {
			System.err.println("Cannot find virtual machine: "+name);
			
		} catch (CannotStartInstanceException e) {
			System.err.println("There was an error starting the instance - instance terminated while starting");
			
		} catch (VirtualMachineAlreadyRunningException e) {
			System.err.println("Virtual machine "+name+" is already running");
		} catch (InconsistentAvailabilityZoneException e) {
			System.err.println("Virtual machine "+name+" has inconsistent avilability zone setting (probable cause: some volumes attached to the VM are in a different availability zone than the VM itself)");
			
			
		} catch (VolumeAlreadyAttachedException e) {
			
			System.err.println("Volume "+e.getVolume().getName()+ "(id "+e.getVolume().getVolumeId()+" ) is already attached to an instance");
		} catch (AddressAlreadyAssignedException e) {
			System.err.println("Ip address "+e.getIpAddress()+" is already assigned to instance "+e.getInstanceId());
		} catch (VolumeMountFailedException e) {
			System.err.println("Failed to mount volume "+e.getVol().getName());
		}
		catch(AutoRunCommandFailedException e) {
			System.err.println("Auto run command "+e.getVm().getAutoRunCommand()+" failed with status "+e.getExitValue());
			
		}
		catch(Exception e) {
			System.err.println("There was an unexpected exception while starting the virtual machine:");
			e.printStackTrace(System.err);
		}
		
		System.exit(1);
		

	}

}
