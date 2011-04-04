/*
 * Created on Nov 15, 2008
 *
 */
package rain;

import java.util.Calendar;
import java.util.List;

import com.sampullara.cli.Argument;

public class DescribeVirtualMachinesCommand extends BaseCommand {

	static {
		thisClass = DescribeVirtualMachinesCommand.class;
	}

	@Argument(description = "List of machine names separated by ,", delimiter = ",", alias = "n")
	private String[] names;
	@Argument(description = "Display all available data", alias = "a")
	private boolean all;

	public void run() {

		RainEngine engine = RainEngine.getInstance();
		List<VirtualMachineInfo> info;
		try {
			info = engine.describeVirtualMachines(names);
		} catch (VirtualMachineNotFoundException e) {
			output.printError("Virtual machine not found: " + e.getMessage());
			System.exit(1);
			return;
		}

		if(!all)
			output.startSection("");
		
		for (VirtualMachineInfo i : info) {
			VirtualMachine vm = i.getVirtualMachine();
			if(all )
				output.startSection(vm!=null?vm.getName():"n/a");
			output.startLine();
			
			
			
			
			output.printValue("Name",vm!=null?vm.getName():"n/a");
			if (i.getStartTime() == null)
				output.printValue("Uptime", "none");
			else {

				long startTime = i.getStartTime().getTimeInMillis();
				long now = Calendar.getInstance().getTimeInMillis();

				long uptime = now - startTime;
				long days = uptime / (1000 * 60 * 60 * 24);
				long hoursandmins = uptime % (1000 * 60 * 60 * 24);
				long hours = hoursandmins / (1000 * 60 * 60);
				long mins = (hoursandmins % (1000 * 60 * 60)) / (1000 * 60);
				output.printValue("Uptime",String.format("%dd%02d:%02d", days, hours, mins));

			}

                        printValue("State",getStateName(i.getCurrentState()),null,"none");
			printValue("DnsName",i.getCurrentDnsName(), null, "none");
			printValue("InternalIpAddress", i.getCurrentPrivateIpAddress(), null, "none");
			printValue("InstanceId",i.getInstanceId(),null,"none");
			

			if (all) {
				
			
				printValue("AvailabilityZone",i.getCurrentAvailabilityZone(), vm!=null?vm
						.getAvailabilityZone():null, "undefined");
				output.printValue("AMI" , vm!=null?vm.getImage():"undefined");
				printValue("KeyPair",vm!=null?vm.getKeypair():null, null, "none");
				printValue("Kernel",vm!=null?vm.getKernel():null, null, "default");
				printValue("Ramdisk",vm!=null?vm.getRamdisk():null, null, "default");
				printInstanceType(vm);
				printValue("StaticIpAddress",vm!=null?vm.getStaticIpAddress():null, null, "none");
                                printValue("Subnet",vm!=null?vm.getVpcSubNet():null,null,"none");
                                printValue("PrivateIpAddress",vm!=null?vm.getPrivateIpAddress():null,null,"none");
				printSecurityGroups(vm);
				printValue("UserData",vm!=null?vm.getUserData():null, null, "none");
				printValue("AutoRunCommand",vm!=null?vm.getAutoRunCommand():null,null,"none");
				
				
				List<Volume> volumes = vm!=null?vm.getVolumes():null;
				if (volumes!=null && volumes.size() > 0) {
					output.endLine();
					output.endSection();
					output.startSection("Volumes");
					
					for (Volume v : volumes) {
						output.startLine();
						output.printValue("Name", v.getName());
						output.printValue("VolumeId", v.getVolumeId());
						output.printValue("Device", v.getDevice());
						output.printValue("MountDevice", v.getMountDevice()!=null?v.getMountDevice():v.getDevice());
						output.printValue("MountPoint", v.getMountPoint());
						output.endLine();
					}
					
				}

				output.endSection();
				output.startSection("");
	

			}

			output.endLine();
			
			
		}

		output.endSection();
		System.exit(0);
	}

	private void printSecurityGroups(VirtualMachine vm) {

		
		String groups = vm!=null?vm.getSecurityGroup():null;
		if (groups == null) {
			output.printValue("SecurityGroups","default");
		} else {

			output.printValue("SecurityGroups",vm.getSecurityGroup());
		}

	}

	private void printInstanceType(VirtualMachine vm) {

		if (vm==null || vm.getInstanceType() == null)
			output.printValue("InstanceType","SMALL");
		else
			output.printValue("InstanceType:" , vm.getInstanceType().toString());

	}

	private void printValue(String label,String value1, String value2, String defaultValue) {

		if (value1 != null)
			output.printValue(label, value1);
		else if (value2 != null)
			output.printValue(label, value2);
		else
			output.printValue(label, defaultValue);

	}

        private String getStateName(Integer code) {

           
            if(code==null)
                return "Not Running";
            if(code==0)
                return "Pending";
            if(code==16)
                return "Running";
            if(code==48)
                return "Terminated";

            if(code==64)
                return "Stopping";

            if(code==80)
                return "Stopped";

            return ""+code;

           
        }

}
