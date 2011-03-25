package rain;

import com.sampullara.cli.Argument;

public class DeleteVirtualMachineCommand extends BaseCommand {

	static {
		thisClass=DeleteVirtualMachineCommand.class;
	}
	
	@Argument(description="Virtual Machine Name",alias="n",required=true)
	private String name;
	
	
	public void run() {
		
		
		RainEngine engine=RainEngine.getInstance();
		try {
			engine.deleteVirtualMachine(name);
		} catch (VirtualMachineNotFoundException e) {
			System.err.println("Virtual machine not found: "+name);
		}
		

	}

}
