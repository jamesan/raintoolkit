/*
 * Created on Nov 20, 2008
 *
 */
package rain.authorization;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import rain.BaseJPADAO;
import rain.authorization.AWSPermission.AWSService;


public class JPAAWSPermissionDAO extends BaseJPADAO implements AWSPermissionDAO {

	public void delete(AWSPermission p) {
		EntityManager em=getEntityManager();
		
		try {
			em.remove(p);
			em.getTransaction().commit();
		}
		finally {
			em.close();
		}
		

	}

	public List<AWSPermission> findAll() {
		EntityManager em=getEntityManager();
		
		// The <1000 below is just to please the Simpledb JPA implementation.
		try {
			Query q=em.createQuery("select v from rain.authorization.AWSPermission v where v.sequence<1000 order by v.sequence ");
		
			
			
			return q.getResultList();
		}
		finally {
			em.close();
			
		}
	
	}

	public void saveOrUpdate(AWSPermission p) {
		EntityManager em=getEntityManager();
		try {
			em.persist(p);
			em.getTransaction().commit();
		}
		finally {
			em.close();
		}
	}

	public List<AWSPermission> findByService(AWSService service) {
		EntityManager em=getEntityManager();
		try {
			Query q=em.createQuery("select v from rain.authorization.AWSPermission v where v.service=:service and v.sequence < 1000 order by v.sequence");
			q.setParameter("service", service.ordinal());
			return q.getResultList();
		}
		finally {
			em.close();
			
		}
		
	}

	public Integer findMaxSequenceByService(AWSService service) {
		
		EntityManager em=getEntityManager();
		try {
			Query q=em.createQuery("select v from rain.authorization.AWSPermission v where v.service=:service and v.sequence < 1000 order by v.sequence desc");
			q.setParameter("service", service.ordinal());
			List<AWSPermission> result=q.getResultList();
			if(result.size()==0)
				return 0;
			
			return result.get(0).getSequence();
		}
		finally {
			em.close();
			
		}
		
		
		
	}

	public AWSPermission findByServiceAndSequence(AWSService service,
			Integer number) {
		EntityManager em=getEntityManager();
		try {
			Query q=em.createQuery("select v from rain.authorization.AWSPermission v where v.service=:service and v.sequence =:sequence ");
			q.setParameter("service", service.ordinal());
			q.setParameter("sequence", number);
			List<AWSPermission> result=q.getResultList();
			if(result.size()==0)
				return null;
			
			return result.get(0);
		}
		finally {
			em.close();
			
		}
	}

}
