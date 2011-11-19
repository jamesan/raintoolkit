/*
 * Created on Nov 21, 2008
 *
 */
package rain.authorization;

public class GroupAlreadyExistsException extends Exception {

	private String groupName;

	public GroupAlreadyExistsException(String groupName) {
		this.groupName=groupName;
	}

}
