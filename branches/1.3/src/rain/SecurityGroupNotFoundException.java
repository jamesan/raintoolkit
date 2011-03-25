/*
 * Created on Nov 7, 2008
 *
 */
package rain;

public class SecurityGroupNotFoundException extends Exception {

	private String group;

	public SecurityGroupNotFoundException(String group) {
		this.group=group;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

}
