/*
 * Created on Nov 21, 2008
 *
 */
package rain.authorization;

import java.util.List;

public class UserDescription {
	
	private Principal principal;
	
	private List<Group> groups;

	public Principal getPrincipal() {
		return principal;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
	

}
