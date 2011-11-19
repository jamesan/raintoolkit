package rain;

public class DynamicIpAddressNotFoundException extends Exception {

	private String name;

	public DynamicIpAddressNotFoundException(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
