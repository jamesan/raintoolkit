/*
 * Created on Nov 21, 2008
 *
 */
package rain.authorization;

import java.util.List;

import rain.BaseCommand;

import com.sampullara.cli.Argument;


public class DescribeUsersCommand extends BaseCommand {

	static {
		thisClass=DescribeUsersCommand.class;
	}
	@Argument(description="User name" , alias="u")
	private String username;
	@Argument(description="Only list users in group name",alias="g")
	private String groupName;
	@Argument(description="List secret keys", alias="k")
	private boolean listKeys;
	
	public void run() {
		
		AuthorizationEngine engine=AuthorizationEngine.getInstance();
		
		try {
			List<UserDescription> users=engine.describeUsers(username,groupName);
			
			output.startSection("");
			for(UserDescription u: users) {
				output.startLine();
				output.printValue("Username", u.getPrincipal().getName());
				output.printValue("AccessId", u.getPrincipal().getAccessId());
				if(listKeys)
					output.printValue("SecretKey", u.getPrincipal().getSecretKey());
				StringBuffer groups=new StringBuffer();
				for(Group g: u.getGroups())  {
					groups.append(g.getName());
					groups.append(",");
				}
				groups.setLength(groups.length()-1);
				output.printValue("Groups", groups.toString());
				output.endLine();
					
				
			}
			
			output.endSection();
			System.exit(0);
			return;
		} catch (GroupNotFoundException e) {
			output.printError("Group not found: "+groupName);
		} catch (UserNotFoundException e) {
			output.printError("User not found: "+username);
		}
		

		System.exit(1);
	}

}
