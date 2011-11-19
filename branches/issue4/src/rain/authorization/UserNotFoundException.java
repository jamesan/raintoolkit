/*
 * Created on Nov 21, 2008
 *
 */
package rain.authorization;

public class UserNotFoundException extends Exception {

	private String userName;

	public UserNotFoundException(String userName) {
		this.userName=userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
