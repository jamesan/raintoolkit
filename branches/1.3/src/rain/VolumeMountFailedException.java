/*
 * Created on Nov 5, 2008
 *
 */
package rain;



public class VolumeMountFailedException extends Exception {

	private VirtualMachine vm;
	private Volume vol;
	

	public VolumeMountFailedException(VirtualMachine vm, Volume vol
			) {
		
	
		this.vm=vm;
		this.vol=vol;
		
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

	

}
