/*
 * Created on Nov 20, 2008
 *
 */
package rain.authorization;


import java.util.List;

import rain.authorization.AWSPermission.AWSService;

public interface AWSPermissionDAO {
	
	public List<AWSPermission> findAll();
	
	public void saveOrUpdate(AWSPermission p);
	
	public void delete(AWSPermission p);

	public List<AWSPermission> findByService(AWSService service);

	public Integer findMaxSequenceByService(AWSService service);

	public AWSPermission findByServiceAndSequence(AWSService awsService,
			Integer number);
	
	

}
