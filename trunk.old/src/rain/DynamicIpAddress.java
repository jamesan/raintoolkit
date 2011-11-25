package rain;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * This class represents a dynamic ip address associated with a set of AWS permissions
 * it allows the system to replace occurrences of the old ip address with
 * occurrences of the new ip address whenever an ip change is detected
 * @author juliano
 *
 */

@Entity
public class DynamicIpAddress {

	
	private String name;
	@Id
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
	private String currentValue;
	
	
}
