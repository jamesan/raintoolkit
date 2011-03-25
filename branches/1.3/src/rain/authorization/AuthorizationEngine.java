/*
 * Created on Nov 20, 2008
 *
 */
package rain.authorization;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import rain.BaseEngine;
import rain.authorization.AWSPermission.AWSService;
import rain.authorization.AWSPermission.AuthorizationAction;


public class AuthorizationEngine extends BaseEngine {

	private AWSPermissionDAO permissionDAO;
	private PrincipalDAO principalDAO;
	private AuthorizationManager authManager;

	private static AuthorizationEngine instance;

	public static AuthorizationEngine getInstance() {
		if (instance == null)
			instance = new AuthorizationEngine();
		return instance;
	}

	/**
	 * @param aws_access_id
	 * @param aws_secret_key
	 * @param aws_account_id
	 * @param glue_home
	 * @param endpointURL
	 */
	public AuthorizationEngine(String aws_access_id, String aws_secret_key,
			String aws_account_id, String glue_home, String endpointURL) {
		super(aws_access_id, aws_secret_key, aws_account_id, glue_home,
				endpointURL);

		initializeEngine();
	}

	private void initializeEngine() {
		permissionDAO = new JPAAWSPermissionDAO();
		principalDAO = new JPAPrincipalDAO();
		authManager = new AuthorizationManager();
		authManager.setPermissionDAO(permissionDAO);
		authManager.setPrincipalDAO(principalDAO);
	}

	public AuthorizationEngine() {

		initializeEngine();

	}

	public AuthorizationManager getAuthManager() {
		return authManager;
	}

	public void setAuthManager(AuthorizationManager authManager) {
		this.authManager = authManager;
	}

	public Principal createUser(String username)
			throws UseralreadyExistsException {

		// Check that username is unique

		Principal p = principalDAO.findByName(username);

		if (p != null)
			throw new UseralreadyExistsException(username);

		p = new Principal();
		p.setAccessId(generateAccessId());

		p.setSecretKey(generateSecretKey());

		p.setName(username);

		principalDAO.saveOrUpdate(p);

		return p;

	}

	private String generateSecretKey() {
		return generateRandomBase64(30);
	}

	private String generateAccessId() {
		return generateRandomBase64(15);
	}

	private String generateRandomBase64(int length) {

		byte[] key = new byte[length];

		SecureRandom random = new SecureRandom();
		random.nextBytes(key);

		return new String(Base64.encodeBase64(key));

	}

	public void createGroup(String groupName)
			throws GroupAlreadyExistsException {

		Group g = principalDAO.findGroupByName(groupName);
		if (g != null)
			throw new GroupAlreadyExistsException(groupName);
		g = new Group();
		g.setName(groupName);
		principalDAO.saveOrUpdate(g);

	}

	public void addGroupUser(String userName, String groupName)
			throws UserNotFoundException, GroupNotFoundException {

		Principal p = principalDAO.findByName(userName);

		if (p == null)
			throw new UserNotFoundException(userName);

		Group g = principalDAO.findGroupByName(groupName);

		if (g == null)
			throw new GroupNotFoundException(groupName);

		if (p instanceof Group)
			throw new UserNotFoundException(userName);

		List<Group> currentGroups = principalDAO.findGroups(p);

		if (currentGroups.contains(g))
			return;

		principalDAO.addMembership(p, g);

	}

	public List<UserDescription> describeUsers(String username, String groupName)
			throws GroupNotFoundException, UserNotFoundException {

		List<UserDescription> descriptions = new ArrayList<UserDescription>();

		List<Principal> users;
		if (username != null) {
			Principal user = principalDAO.findByName(username);
			users = new ArrayList<Principal>();
			if (user != null)
				users.add(user);
			else
				throw new UserNotFoundException(username);
		} else
			users = principalDAO.findAllUsers();

		for (Principal p : users) {
			UserDescription description = new UserDescription();
			description.setPrincipal(p);
			description.setGroups(principalDAO.findGroups(p));
			descriptions.add(description);

		}

		if (groupName != null) {
			Group group = principalDAO.findGroupByName(groupName);
			if (group == null)
				throw new GroupNotFoundException(groupName);

			Iterator<UserDescription> it = descriptions.iterator();
			while (it.hasNext()) {
				UserDescription description = it.next();
				if (!description.getGroups().contains(group))
					it.remove();
			}

		}

		return descriptions;

	}

	public void addPermission(AWSService service, String principal,
			String action, String parameters,
			AuthorizationAction authorizationAction, Integer sequence)
			throws UserNotFoundException, InvalidPermissionSequenceException {

		Principal p = null;

		if (principal != null) {
			p = principalDAO.findByName(principal);
			if (p == null)
				throw new UserNotFoundException(principal);
		}

		AWSPermission permission = new AWSPermission();
		permission.setAction(action);
		permission.setAuthorizationAction(authorizationAction);
		permission.setParameters(parameters);
		permission.setPrincipalName(principal);
		permission.setService(service);

		int currentMaxSequence = permissionDAO
		.findMaxSequenceByService(service);

		if (sequence == null) {
			
			permission.setSequence(currentMaxSequence+1);
		}
		else {
			if(sequence>currentMaxSequence || sequence<0)
				throw new InvalidPermissionSequenceException(sequence);
			permission.setSequence(sequence);
			List<AWSPermission> permissions=permissionDAO.findByService(service);
			for(AWSPermission p1: permissions) {
				if(p1.getSequence()>=sequence) {
					p1.setSequence(p1.getSequence()+1);
					permissionDAO.saveOrUpdate(p1);
				}
			}
		}

		permissionDAO.saveOrUpdate(permission);
		
		if(sequence!=null) {
		
		}

	}

	public List<AWSPermission> getPermissions(AWSService service) {

		List<AWSPermission> permissions;

		if (service != null)
			permissions = permissionDAO.findByService(service);
		else
			permissions = permissionDAO.findAll();

		return permissions;

	}

	public Principal findUerByAccessId(String accessKeyId) {

		return principalDAO.findByAccessKeyId(accessKeyId);
	}

	public void deletePermission(AWSService awsService, Integer number)
			throws AWSPermissionNotFoundException {

		AWSPermission p = permissionDAO.findByServiceAndSequence(awsService,
				number);

		if (p == null)
			throw new AWSPermissionNotFoundException(awsService, number);

		permissionDAO.delete(p);

		List<AWSPermission> permissionList = permissionDAO
				.findByService(awsService);

		for (AWSPermission p1 : permissionList) {
			if (p1.getSequence() > p.getSequence()) {
				p1.setSequence(p1.getSequence() - 1);
				permissionDAO.saveOrUpdate(p1);
			}
		}
	}

}
