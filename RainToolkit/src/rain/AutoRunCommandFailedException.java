package rain;

public class AutoRunCommandFailedException extends Exception {

	private VirtualMachine vm;
	private int exitValue;

	public AutoRunCommandFailedException(VirtualMachine vm,int exitValue) {
		this.vm=vm;
		this.exitValue=exitValue;
	}

	public int getExitValue() {
		return exitValue;
	}

	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}

	public VirtualMachine getVm() {
		return vm;
	}

	public void setVm(VirtualMachine vm) {
		this.vm = vm;
	}

}
