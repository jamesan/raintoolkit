/*
 * Created on Nov 7, 2008
 *
 */
package rain;

public class VirtualMachineAlreadyExistsException extends Exception {

	private String name;

	public VirtualMachineAlreadyExistsException(String name) {
		this.name=name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
