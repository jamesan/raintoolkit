package rain;

import java.io.IOException;

import com.sampullara.cli.Argument;


public class AuthorizeIPAddressCommand extends BaseCommand {

	static {
		thisClass=AuthorizeIPAddressCommand.class;
	}
	@Argument(alias="s",description="Start port")
	private int startPort=0;
	
	@Argument(alias="e" , description="End port")
	private int endPort=65535;
	
	@Argument(alias="g" , description="Security group")
	private String securityGroup;
	
	@Argument(alias="a", description="IP address or IP range in CIDR format", required=true)
	private String ipAddress;
	
	@Argument(alias="p",description="Protocol")
	private String protocol;
	
	public void run() {
		
		String[] allProtocols=new String[]{"tcp","udp"};
		
		
		RainEngine engine=RainEngine.getInstance();
		
		if(ipAddress.indexOf('/')==-1)
			ipAddress=ipAddress+"/32";
		
		
		String[] groups;
		if(securityGroup==null) {
			
			try {
				groups=engine.getSecurityGroupNames();
			} catch (Exception e) {
				System.err.println("Error getting security group names");
				e.printStackTrace();
				return;
			}
		}
		else {
			groups=new String[] { securityGroup};
		}
		
		String[] protocols;
		if(protocol==null)
			protocols=allProtocols;
		else
			protocols=new String[] { protocol };
		
		for(int i=0;i<groups.length;i++) {
			for(int j=0;j<protocols.length;j++) {
				
			
			try {
				engine.authorizeIPAddress(ipAddress, groups[i],protocols[j], startPort, endPort);
			} catch (Exception e) {
				System.err.println("Error authorizing group "+groups[i]);
				e.printStackTrace();
			}
			}
		}
		

	}

}
