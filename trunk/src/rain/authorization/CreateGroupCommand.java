/*
 * Created on Nov 21, 2008
 *
 */
package rain.authorization;

import rain.BaseCommand;

import com.sampullara.cli.Argument;


public class CreateGroupCommand extends BaseCommand {

	static {
		thisClass=CreateGroupCommand.class;
	}
	
	@Argument(description="Group name",alias="g",required=true)
	private String groupName;
	public void run() {
		
		
		AuthorizationEngine engine=AuthorizationEngine.getInstance();
		
		try {
			engine.createGroup(groupName);
		} catch (GroupAlreadyExistsException e) {
			
			output.printError("Group already exists");
			System.exit(1);
			return;
			
		}
		
		
		
	}

}
