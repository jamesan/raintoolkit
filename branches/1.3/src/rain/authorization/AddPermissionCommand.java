/*
 * Created on Nov 21, 2008
 *
 */
package rain.authorization;

import rain.BaseCommand;
import rain.authorization.AWSPermission.AWSService;
import rain.authorization.AWSPermission.AuthorizationAction;

import com.sampullara.cli.Argument;


public class AddPermissionCommand extends BaseAuthorizationCommand {

	static {
		thisClass=AddPermissionCommand.class;
	}
	
	@Argument(description="AWS service",alias="s", required=true)
	private String service;
	@Argument(description="User or group name", alias="u")
	private String principal;
	@Argument(description="AWS service action",alias="a")
	private String action;
	@Argument(description="AWS action parameter (in the format param1=value1,param2=value2 etc.)",alias="p")
	private String parameters;
	@Argument(description="Action parameters",required=true,alias="j")
	private String authorizationAction;
	@Argument(description="Sequence number",alias="n")
	private Integer sequence;
	public void run() {
		
		
		
		AuthorizationEngine engine=AuthorizationEngine.getInstance();
		AWSService awsService;
		AuthorizationAction authorizedAction;
		
		awsService = parseAWSService(service);
		if(awsService==null) {
			System.exit(1);
			return;
		}
		
		authorizedAction = parseAuthorizationaction(authorizationAction);
		if(authorizedAction==null) {
			System.exit(1);
			return;
		}
			
		
		try {
			engine.addPermission(awsService,principal,action,parameters,authorizedAction,sequence);
			System.exit(0);
		} catch (UserNotFoundException e) {
			output.printError("User not found: "+e.getUserName());
		} catch (InvalidPermissionSequenceException e) {
			output.printError("Invalid sequence number");
		}
		
		
		System.exit(-1);

	}

}
