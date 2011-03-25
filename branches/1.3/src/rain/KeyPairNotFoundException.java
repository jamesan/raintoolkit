/*
 * Created on Nov 7, 2008
 *
 */
package rain;

public class KeyPairNotFoundException extends Exception {

	private String keypair;

	public KeyPairNotFoundException(String keypair) {
		this.keypair=keypair;
	}

	public String getKeypair() {
		return keypair;
	}

	public void setKeypair(String keypair) {
		this.keypair = keypair;
	}
	

}
