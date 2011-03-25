/*
 * Created on Nov 25, 2008
 *
 */
package rain.authorization;

import java.util.List;

import rain.BaseCommand;
import rain.authorization.AWSPermission.AWSService;

import com.sampullara.cli.Argument;


public class DescribePermissionsCommand extends BaseCommand {

	static {
		thisClass=DescribePermissionsCommand.class;
	}
	
	@Argument(description="AWS service", alias="s")
	private String service;
	
	public void run() {
		
		
		
		AuthorizationEngine engine=AuthorizationEngine.getInstance();
		AWSService awsService=null;
		try {
			if(service!=null)
				awsService=AWSService.valueOf(service);
		}
		catch(Exception e) {
			output.printError("Invalid service value, use one of:");
			for(AWSService s: AWSService.values()) {
				output.printError(s.name());
			}
			
			System.exit(1);
			return;
		}
		
		List<AWSPermission> permissions=engine.getPermissions(awsService);
		
		for(AWSService s: AWSService.values()) {
			if(awsService==null || awsService.equals(s))
				printPermissionsForService(s,permissions);
		}
		
		

	}

	private void printPermissionsForService(AWSService s,
			List<AWSPermission> permissions) {
		
		output.startSection(s.name());
		for(AWSPermission p: permissions) {
			if(!p.getService().equals(s))
				continue;
			output.startLine();
			output.printValue("Sequence", p.getSequence()+"");
			output.printValue("Principal", p.getPrincipalName());
			output.printValue("Action",(p.getAction()==null || p.getAction().equals("")?"-":p.getAction()));
			output.printValue("Parameters",(p.getParameters()==null || p.getParameters().equals(""))?"-":p.getParameters());
			output.printValue("Outcome", p.getAuthorizationAction().name());
			output.endLine();
		}
		
		output.endSection();
	}

}
