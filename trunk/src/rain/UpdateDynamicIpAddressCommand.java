package rain;

import java.io.IOException;

import com.sampullara.cli.Argument;
import com.xerox.amazonws.ec2.EC2Exception;

public class UpdateDynamicIpAddressCommand extends BaseCommand {

	static {
		thisClass=UpdateDynamicIpAddressCommand.class;
	}
	
	@Argument(alias="n" , required=true)
	private String name;
	@Argument(alias="i", description="New value")
	private String newValue;
	public void run() {
		
		RainEngine engine=RainEngine.getInstance();
		
		try {
			if(newValue==null)
				newValue=engine.getCurrentInternetIPAddress();
			engine.updateDynamicIpAddress(name, newValue);
		} catch (EC2Exception e) {
			System.err.println("Unexpected exception: ");
			e.printStackTrace();
			System.exit(-1);
		} catch (DynamicIpAddressNotFoundException e) {
			System.err.println("Dynamic IP Address not found: "+e.getName());
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("Unexpected exception: ");
			e.printStackTrace();
			System.exit(-1);
		}
		

	}

}
