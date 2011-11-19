/*
 * Created on Nov 19, 2008
 *
 */
package rain.authorization;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 *  This class represents an action that can be executed upon an AWS object
 * @author juliano
 * (c) 2008 Boltblue International Limited
 */

@Entity
public class AWSPermission {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	private Integer sequence;
	
	
	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}


	private String id;
	public enum AWSService {
		SIMPLEDB,S3,EC2;

		
	}
	
	public enum AuthorizationAction {
		ALLOW,DENY
	}
	
	private AuthorizationAction authorizationAction;
	
	
	@Enumerated
	public AuthorizationAction getAuthorizationAction() {
		return authorizationAction;
	}

	public void setAuthorizationAction(AuthorizationAction authorizationAction) {
		this.authorizationAction = authorizationAction;
	}

	@Enumerated
	public AWSService getService() {
		return service;
	}

	public void setService(AWSService service) {
		this.service = service;
	}


	private String principalName;
	
	public String getPrincipalName() {
		return principalName;
	}

	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}
	
	

	private  AWSService service;
	
	private String action;

	private String parameters;
	
	
	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	@Transient
	public Map<String,String> getParameterMap() {
		Map<String,String> parameterMap=new HashMap<String,String>();
		
		if(parameters!=null) {
			String[] tmp=parameters.split(",");
			for(String t: tmp) {
				String[] tmp2=t.split("=");
				if(tmp2.length<2)
					continue;
				parameterMap.put(tmp2[0],tmp2[1]);
				
			}
		}
		
		return parameterMap;
	}
	
	

}
