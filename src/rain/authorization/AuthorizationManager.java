/*
 * Created on Nov 20, 2008
 *
 */
package rain.authorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import rain.authorization.AWSPermission.AWSService;
import rain.authorization.AWSPermission.AuthorizationAction;


public class AuthorizationManager {
	
	private AWSPermissionDAO permissionDAO;
	
	public AWSPermissionDAO getPermissionDAO() {
		return permissionDAO;
	}


	public void setPermissionDAO(AWSPermissionDAO permissionDAO) {
		this.permissionDAO = permissionDAO;
	}


	public PrincipalDAO getPrincipalDAO() {
		return principalDAO;
	}


	public void setPrincipalDAO(PrincipalDAO principalDAO) {
		this.principalDAO = principalDAO;
	}


	private PrincipalDAO principalDAO;
	
	
	private List<String> restrictedDomains=Arrays.asList("rain-Principal","rain-Group","rain-GroupMembership","rain-AWSPermission");
	
	public void checkAuthorization(String action,AWSService service,Map<String,String> parameters,Principal principal) throws ActionNotAuthorizedException {

		
		// Hard coded permissons preventing anyone from messing with the internal databases
		
		checkInternalDatabaseAccess(service,parameters);
		
		
		List<AWSPermission> permissions=findPermissionsByServiceAndPrincipal(service,principal);
	
		for(AWSPermission p: permissions) {
			
			if(isWildcard(p.getAction()) || p.getAction().equals(action)) {
				
				boolean parameterMatches=true;
				if(p.getParameterMap()!=null && p.getParameterMap().size()>0) {
					for(String param: p.getParameterMap().keySet()) {
						String requiredValue=p.getParameterMap().get(param);
						String providedValue=parameters.get(param);
						if(providedValue==null || !providedValue.matches(requiredValue)) {
							parameterMatches=false;
							break;
						}
					}
				}
				
				if(!parameterMatches)
					continue;
				// Ok, found one permission. now check the authorized action
				
				if(p.getAuthorizationAction()==AuthorizationAction.ALLOW)
					return;
				else
					throw new ActionNotAuthorizedException();
				
			}
			
		}
		
		// If we are here, its because no permission was found. In this case the action cannot be authorized
		
		throw new ActionNotAuthorizedException();
		
	}


	private void checkInternalDatabaseAccess(AWSService service, Map<String, String> parameters) throws ActionNotAuthorizedException {
		
		if(service==AWSService.SIMPLEDB) {
			String domain=parameters.get("DomainName");
			if(domain!=null && restrictedDomains.contains(domain))
				throw new ActionNotAuthorizedException();
		}
		
		
	}


	private List<AWSPermission> findPermissionsByServiceAndPrincipal(
			AWSService service, Principal principal) {
		
		List<AWSPermission> permissions=permissionDAO.findByService(service);
		ArrayList<AWSPermission> principalPermissions=new ArrayList<AWSPermission>();
		
		List<Group> principalGroups=principalDAO.findGroups(principal);
		
		for(AWSPermission p: permissions) {
			
			if(isWildcard(p.getPrincipalName()) || p.getPrincipalName().equals(principal.getName()))
				principalPermissions.add(p);
			else {
				for(Group g: principalGroups) {
					if(g.getName().equals(p.getPrincipalName()))
						principalPermissions.add(p);
				}
			}
			
			
		}
		
		
		return principalPermissions;
		
		
	}


	private boolean isWildcard(String action) {
		return action==null || action.equals("*");
	}

}
