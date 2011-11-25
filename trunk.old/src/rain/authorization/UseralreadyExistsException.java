/*
 * Created on Nov 21, 2008
 *
 */
package rain.authorization;

public class UseralreadyExistsException extends Exception {

	private String username;

	public UseralreadyExistsException(String username) {
		this.username=username;
	}

}
