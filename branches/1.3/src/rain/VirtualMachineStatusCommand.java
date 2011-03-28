/*
 * Created on Nov 3, 2008
 *
 */
package rain;

public class VirtualMachineStatusCommand {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length<1) {
			System.err.println("Usage: VirtualMachineStatusCommand <virtual machine name>");
			return;
		}
		
		RainEngine engine=RainEngine.getInstance();
		
		String ipAddress=engine.getVirtualMachineIPAddress(args[0]);
		if(ipAddress==null) {
			System.out.println("Virtual machine "+args[0]+" not running");
			System.exit(1);
		}
		
		System.out.println(ipAddress);
		System.exit(0);
		

	}

}
