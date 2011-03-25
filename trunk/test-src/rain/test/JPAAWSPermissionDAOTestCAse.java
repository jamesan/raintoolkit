/*
 * Created on Nov 20, 2008
 *
 */
package rain.test;

import java.util.List;

import rain.authorization.AWSPermission;
import rain.authorization.JPAAWSPermissionDAO;
import rain.authorization.AWSPermission.AWSService;
import rain.authorization.AWSPermission.AuthorizationAction;


public class JPAAWSPermissionDAOTestCAse extends BaseJPATestCase {

	
	private JPAAWSPermissionDAO dao=new JPAAWSPermissionDAO();
	
	
	public void testDAO() throws Exception {
		
		
		AWSPermission permission=new AWSPermission();
		
		permission.setAction("DescribeInstances");
		permission.setService(AWSService.EC2);
		permission.setPrincipalName("testPrincipal");
		permission.setAuthorizationAction(AuthorizationAction.ALLOW);
		permission.setParameters("");
		permission.setSequence(0);
		
		dao.saveOrUpdate(permission);
		
		assertNotNull(permission.getId());
		
		
		List<AWSPermission> list=dao.findAll();
		
		assertEquals(1,list.size());
		
		AWSPermission permission2=list.get(0);
		
		
		
		dao.delete(permission2);
		
		list=dao.findAll();
		
		assertEquals(0,list.size());
		
		
	}
	
}
