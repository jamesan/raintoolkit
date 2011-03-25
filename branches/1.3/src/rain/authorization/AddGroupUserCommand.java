/*
 * Created on Nov 21, 2008
 *
 */
package rain.authorization;

import rain.BaseCommand;

import com.sampullara.cli.Argument;


public class AddGroupUserCommand extends BaseCommand {

	static {
		thisClass=AddGroupUserCommand.class;
	}
	
	@Argument(description="User name" , alias="u", required=true)
	private String userName;
	@Argument(description="Group name" , alias="g", required=true)
	private String groupName;
	public void run() {
		
		
		AuthorizationEngine engine=AuthorizationEngine.getInstance();
		
		try {
			engine.addGroupUser(userName,groupName);
			System.exit(0);
			return;
		} catch (UserNotFoundException e) {
			output.printError("User not found");
		} catch (GroupNotFoundException e) {
			output.printError("Group not found");
		}
		
		System.exit(1);

	}

}
