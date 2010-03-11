package rain;

import java.io.IOException;

public class GetMyIpAddressCommand extends BaseCommand {

	static {
		thisClass=GetMyIpAddressCommand.class;
	}
	public void run() {
		
		try {
			System.out.println(RainEngine.getInstance().getCurrentInternetIPAddress());
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
			
		}
		

	}

}
