package rain;

import com.sampullara.cli.Argument;

public class DeleteDynamicIpAddressCommand extends BaseCommand {

	static {
		thisClass=DeleteDynamicIpAddressCommand.class;
	}
	
	@Argument(alias="n", required=true)
	private String name;
	public void run() {
		
		RainEngine engine=RainEngine.getInstance();
		try {
			engine.deleteDynamicIpAddress(name);
		} catch (DynamicIpAddressNotFoundException e) {
			System.err.println("Dynamic ip address now found: "+name);
			System.exit(-1);
		}
		

	}

}
