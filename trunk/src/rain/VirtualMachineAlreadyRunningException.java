/*
 * Created on Nov 4, 2008
 *
 */
package rain;

public class VirtualMachineAlreadyRunningException extends Exception {

	public VirtualMachineAlreadyRunningException() {
	
	}

	public VirtualMachineAlreadyRunningException(String message) {
		super(message);
	
	}

	public VirtualMachineAlreadyRunningException(Throwable cause) {
		super(cause);
	
	}

	public VirtualMachineAlreadyRunningException(String message, Throwable cause) {
		super(message, cause);
		
	}

}
