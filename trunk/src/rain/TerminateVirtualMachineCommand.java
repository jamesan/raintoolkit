/*
 * Created on Nov 7, 2008
 *
 */
package rain;

import com.sampullara.cli.Argument;

public class TerminateVirtualMachineCommand extends BaseCommand {

	static {
		thisClass=TerminateVirtualMachineCommand.class;
	}
	
	@Argument(description="Virtual Machine Name",alias="n",required=true)
	private String name;
	public void run() {
		
		RainEngine engine=RainEngine.getInstance();
		try {
			engine.terminateVirtualMachine(name);
			System.exit(0);
			return;
		} catch (VirtualMachineNotRunningException e) {
			System.err.println("Virtual machine "+name+" not running");
		} catch (VirtualMachineNotFoundException e) {
			System.err.println("Virtual machine not found: "+name);
		}
		catch(RuntimeException e) {
			System.err.println("Error terminating machine");
			e.printStackTrace();
		}
		
		System.exit(1);

	}

}
