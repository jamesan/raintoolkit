/*
 * Created on Nov 7, 2008
 *
 */
package rain;

public class InstanceNotFoundException extends Exception {

	private String instance;

	public InstanceNotFoundException(String currentInstance) {
		this.instance=currentInstance;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}
	

}
