/*
 * Created on Nov 26, 2008
 *
 */
package rain.authorization;

import rain.BaseCommand;
import rain.authorization.AWSPermission.AWSService;
import rain.authorization.AWSPermission.AuthorizationAction;

public abstract class BaseAuthorizationCommand extends BaseCommand {

	protected AuthorizationAction parseAuthorizationaction(String authorizationAction) {
		try {
			AuthorizationAction authorizedAction=AuthorizationAction.valueOf(authorizationAction);
			return authorizedAction;
		}
		catch(Exception e) {
			output.printError("Invaid authorization action value, use one of:");
			for(AuthorizationAction a: AuthorizationAction.values()) {
				output.printError(a.name());
			
			}
			return null;
		}
		
	}

	protected AWSService parseAWSService(String service) {
		try {
			AWSService awsService=AWSService.valueOf(service);
			return awsService;
		}
		catch(Exception e) {
			output.printError("Invalid service value, use one of:");
			for(AWSService s: AWSService.values()) {
				output.printError(s.name());
			}
			
			return null;
		}
		
	}

	

}
