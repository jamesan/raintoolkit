/*
 * Created on Nov 20, 2008
 *
 */
package rain.authorization;

import java.util.List;

public interface PrincipalDAO {
	
	public Principal findByName(String name);
	
	public List<Principal>  findAllUsers();
	
	public List<Group> findAllGroups();
	
	public List<Principal> findMembers(Group group);
	
	public List<Group>  findGroups(Principal principal);
	
	public void addMembership(Principal principal, Group group);
	
	public void removeMembership(Principal principal, Group group);

	public void saveOrUpdate(Principal p);

	public Group findGroupByName(String groupName);

	public Principal findByAccessKeyId(String accessKeyId);
	
	

}
