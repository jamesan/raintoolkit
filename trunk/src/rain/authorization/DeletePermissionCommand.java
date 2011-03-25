/*
 * Created on Nov 26, 2008
 *
 */
package rain.authorization;

import rain.BaseCommand;
import rain.authorization.AWSPermission.AWSService;

import com.sampullara.cli.Argument;


public class DeletePermissionCommand extends BaseAuthorizationCommand {

	static {
		thisClass=DeletePermissionCommand.class;
	}
	@Argument(description="Permission sequence number", alias="n",required=true)
	private Integer number;
	@Argument(description="AWS service. Must be one of: SIMPLEDB, EC2 or S3", required=true,alias="s")
	private String service;
	
	public void run() {
		
		AWSService awsService=parseAWSService(service);
		if(awsService==null) {
			System.exit(1);
			return;
		}
		
		
		
		AuthorizationEngine engine=AuthorizationEngine.getInstance();
		
		try {
			engine.deletePermission(awsService,number);
			System.exit(0);
		} catch (AWSPermissionNotFoundException e) {
			output.printError("Permission not found");
			
		}
		
		System.exit(1);
		
		
		
		

	}

}
