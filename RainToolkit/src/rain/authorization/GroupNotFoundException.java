/*
 * Created on Nov 21, 2008
 *
 */
package rain.authorization;

public class GroupNotFoundException extends Exception {

	private String groupName;

	public GroupNotFoundException(String g) {
	
		this.groupName=g;
		
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
