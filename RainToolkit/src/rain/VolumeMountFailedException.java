/*
 * Created on Nov 5, 2008
 *
 */
package rain;

import com.xerox.amazonws.ec2.ReservationDescription.Instance;

public class VolumeMountFailedException extends Exception {

	private VirtualMachine vm;
	private Volume vol;
	private Instance currentInstance;

	public VolumeMountFailedException(VirtualMachine vm, Volume vol,
			Instance currentInstance) {
		
	
		this.vm=vm;
		this.vol=vol;
		this.currentInstance=currentInstance;
	}

	public VirtualMachine getVm() {
		return vm;
	}

	public void setVm(VirtualMachine vm) {
		this.vm = vm;
	}

	public Volume getVol() {
		return vol;
	}

	public void setVol(Volume vol) {
		this.vol = vol;
	}

	public Instance getCurrentInstance() {
		return currentInstance;
	}

	public void setCurrentInstance(Instance currentInstance) {
		this.currentInstance = currentInstance;
	}

}
