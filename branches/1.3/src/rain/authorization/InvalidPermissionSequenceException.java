/*
 * Created on Nov 26, 2008
 *
 */
package rain.authorization;

public class InvalidPermissionSequenceException extends Exception {

	private Integer sequence;

	public InvalidPermissionSequenceException(Integer sequence) {
		this.sequence=sequence;
	}

}
