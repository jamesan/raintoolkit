package rain;

import com.sampullara.cli.Argument;

public class CreateDynamicIpAddressCommand extends BaseCommand {

	static {
		
		thisClass=CreateDynamicIpAddressCommand.class;
	}
	
	@Argument(alias="n", required=true)
	private String name;
	@Argument(alias="i" , required=true)
	private String value;
	
	public void run() {
		
		RainEngine engine=RainEngine.getInstance();
		try {
			engine.createDynamicIpAddress(name, value);
		} catch (DynamicIpAddressAlreadyExistsException e) {
			System.err.println("Dynamic ip address with the name "+name+" already exists");
			System.exit(-1);
			
		}
		
		

	}

}
