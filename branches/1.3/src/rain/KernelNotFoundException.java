/*
 * Created on Nov 7, 2008
 *
 */
package rain;

public class KernelNotFoundException extends Exception {

	private String kernel;

	public KernelNotFoundException(String kernel) {
		this.kernel=kernel;
	}

	public String getKernel() {
		return kernel;
	}

	public void setKernel(String kernel) {
		this.kernel = kernel;
	}

}
