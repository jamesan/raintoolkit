/*
 * Created on Nov 5, 2008
 *
 */
package rain;

public class CannotStartInstanceException extends Exception {

	private int stateCode;
	
	public int getStateCode() {
		return stateCode;
	}

	public void setStateCode(int stateCode) {
		this.stateCode = stateCode;
	}

	public CannotStartInstanceException(int stateCode) {
		this.stateCode=stateCode;
	}

	
	

}
