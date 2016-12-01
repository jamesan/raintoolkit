/*
 * Created on Nov 7, 2008
 *
 */
package rain;

public class VirtualMachineNotRunningException extends Exception {

	private String name;

	public VirtualMachineNotRunningException(String name) {
		this.name=name;
	}

	
}
