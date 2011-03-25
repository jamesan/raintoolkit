/*
 * Created on Nov 26, 2008
 *
 */
package rain.authorization;

import rain.authorization.AWSPermission.AWSService;

public class AWSPermissionNotFoundException extends Exception {

	private AWSService service;
	private Integer sequence;

	public AWSPermissionNotFoundException(AWSService awsService, Integer number) {
		
		this.service=awsService;
		this.sequence=number;
	}

	public AWSService getService() {
		return service;
	}

	public void setService(AWSService service) {
		this.service = service;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

}
