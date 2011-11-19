package rain;

import java.util.List;


public class DescribeDynamicIpAddressCommand extends BaseCommand {

	static {
		thisClass=DescribeDynamicIpAddressCommand.class;
	}
	
	
	public void run() {
		
		RainEngine engine=RainEngine.getInstance();
		
		List<DynamicIpAddress> addresses=engine.getDynamicIpAddresses();
		output.startSection("");
		for(DynamicIpAddress i: addresses) {
			output.startLine();
			output.printValue("Name", i.getName());
			output.printValue("CurrentAddress", i.getCurrentValue());
			output.endLine();
			
		}
		output.endSection();
		

	}

}
