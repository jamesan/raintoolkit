/*
 * Created on Nov 21, 2008
 *
 */
package rain.authorization;

import rain.BaseCommand;

import com.sampullara.cli.Argument;


public class CreateUserCommand extends BaseCommand {

	static {
		thisClass=CreateUserCommand.class;
	}
	
	@Argument(description="User name", required=true,alias="u")
	private String userName;
	public void run() {
		
		AuthorizationEngine engine=AuthorizationEngine.getInstance();
		
		try {
			Principal p=engine.createUser(userName);
			output.startSection("");
			output.startLine();
			output.printValue("AccessKeyId", p.getAccessId());
			output.printValue("SecretKey", p.getSecretKey());
			output.endLine();
			output.endSection();
			
			
		} catch (UseralreadyExistsException e) {
			output.printError("User already exists");
			System.exit(1);
			return;
		}
		
		

	}

}
