/*
 * Created on Nov 7, 2008
 *
 */
package rain;

public class IpAddressAlreadyAssignedException extends Exception {

	private VirtualMachine virtualMachine;

	public IpAddressAlreadyAssignedException(VirtualMachine vm2) {
		this.virtualMachine=vm2;
	}

	public VirtualMachine getVirtualMachine() {
		return virtualMachine;
	}

	public void setVirtualMachine(VirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;
	}

}
