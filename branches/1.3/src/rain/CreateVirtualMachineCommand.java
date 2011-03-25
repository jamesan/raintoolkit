/*
 * Created on Nov 3, 2008
 *
 */
package rain;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

/**
 * Cli interface for creating a new virtual machine representation
 * @author juliano
 * (c) 2008 Boltblue International Limited
 */
public class CreateVirtualMachineCommand extends BaseCommand {
	
	static {
		thisClass=CreateVirtualMachineCommand.class;
	}

	private Logger logger=Logger.getLogger(CreateVirtualMachineCommand.class.getName());
	
	@Argument(description="Virtual machine name", alias="n",required=true)
	private String name;
	@Argument(description="New name")
	private String newName;
	@Argument(description="Virtual machine image id" , alias="i")
	private String image;
	@Argument(description="Availability zone", alias="z")
	private String availbilityZone;
	@Argument(description="Virtual machine security groups, comma-separated",alias="g", delimiter=",")
	private String[] groups;
	@Argument(description="Kernel id",alias="k")
	private String kernel;
	@Argument(description="Ramdisk id",alias="r")
	private String ramDisk;
	@Argument(description="User data",alias="u")
	private String userData;
	@Argument(description="Static ip address",alias="s")
	private String staticIpAddress;
	@Argument(description="SSH key id",alias="h")
	private String key;
	@Argument(description="Current instance id",alias="c")
	private String currentInstanceId;
	@Argument(description="Modify existing virtual machine")
	private boolean modify;
	@Argument(description="Instance type",alias="t")
	private String instanceType;
	@Argument(description="AutoRun Command", alias="l")
	private String autoRunCommand;
	
	

	public void run() {
		VirtualMachine vm=new VirtualMachine();
		vm.setName(name);
		vm.setImage(image);
		vm.setAvailabilityZone(availbilityZone);
		if(groups!=null) {
			
			StringBuffer groupList=new StringBuffer();
			for(int i=0;i<groups.length;i++) {
				groupList.append(groups[i]);
				groupList.append(",");
				
				
			}
			groupList.setLength(groupList.length()-1); // remove the last ,
			vm.setSecurityGroup(groupList.toString());
		}
		vm.setKernel(kernel);
		vm.setRamdisk(ramDisk);
		vm.setUserData(userData);
		vm.setStaticIpAddress(staticIpAddress);
		vm.setCurrentInstance(currentInstanceId);
		
		if(instanceType!=null)
			vm.setInstanceType(VirtualMachine.InstanceType.valueOf(instanceType));
		vm.setKeypair(key);
		vm.setAutoRunCommand(autoRunCommand);
		
		RainEngine engine=RainEngine.getInstance();
		try {
			if(modify) 
				engine.modifyVirtualMachine(vm, newName);
			else {
				if(image==null) {
					output.printError("You must specify an image name");
					System.exit(1);
					return;
				}
				engine.createVirtualMachine(vm);
			}
		}
		catch(AMIDoesNotExistException e2) {
			output.printError("Amazon Machine Image "+vm.getImage()+" does not exist");
			System.exit(1);
		}
		catch(RuntimeException e) {
			output.printError("Cannot create virtual machine");
			logger.log(Level.FINE, "Error creating virtual machine",e);
			System.exit(1);
		} catch (VirtualMachineAlreadyExistsException e) {
			output.printError("Virtual machine "+name+" already exists");
			
		} catch (VirtualMachineNotFoundException e) {
			output.printError("Virtual machine "+name+" not found");
		} catch (SecurityGroupNotFoundException e) {
		
			output.printError("Security group not found: "+e.getGroup());
		} catch (KernelNotFoundException e) {
			
			output.printError("Kernel image not found: "+e.getKernel());
		} catch (InstanceNotFoundException e) {
			output.printError("Virtual Machine instance not found: "+e.getInstance());
		} catch (AvailabilityZoneNotFoundException e) {
			output.printError("Availability zone not found: "+e.getAvailabilityZone());
		} catch (StaticIpAddressNotFoundException e) {
			output.printError("Static ip address is not allocated for the current account: "+e.getStaticIpAddress());
		} catch (IpAddressAlreadyAssignedException e) {
			output.printError("Static ip "+vm.getStaticIpAddress()+" already assigned to virtual machine "+e.getVirtualMachine().getName());
		} catch (KeyPairNotFoundException e) {
			output.printError("Key pair "+e.getKeypair()+" not found");
		}
                catch(ImageIsNotKernelException e) {
                    output.printError("Image "+e.getKernel()+" is not a kernel image");
                }
		
		System.exit(1);
	}

}
