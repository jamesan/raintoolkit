package rain;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class JpaDynamicIpAddressDAO extends BaseJPADAO implements
		DynamicIPAddressDAO {

	public void delete(DynamicIpAddress ip) {
	
		EntityManager em=getEntityManager();
		
		try {
			em.remove(ip);
			em.getTransaction().commit();
			
			
		}
		finally {
			
			em.close();
		}

	}

	public DynamicIpAddress findByCurrentValue(String ipValue) {
		
		
		EntityManager em = getEntityManager();
		try {
			Query q=em.createQuery("select d from rain.DynamicIpAddress ip where ip.currentValue=:v");
			q.setParameter("v", ipValue);
			return (DynamicIpAddress) q.getSingleResult();
			
		}
		finally {
			em.close();
			
		}
	}

	public DynamicIpAddress findByName(String name) {
		EntityManager em = getEntityManager();
		try {
			Query q=em.createQuery("select d from rain.DynamicIpAddress ip where ip.name=:v");
			q.setParameter("v", name);
			
			List<DynamicIpAddress> result=q.getResultList();
			if(result.size()>0)
				return result.get(0);
			else
				return null;
			
		}
		finally {
			em.close();
			
		}
	}

	public void saveOrUpdate(DynamicIpAddress ip) {
	
		EntityManager em = getEntityManager();
		
		try {
			em.persist(ip);
		}
		finally {
			
			em.close();
		}

	}

	public List<DynamicIpAddress> findAll() {
		EntityManager em=getEntityManager();
		
		try {
			Query q=em.createQuery("select d from rain.DynamicIpAddress ip");
			return q.getResultList();
		}
		finally {
		
			em.close();
		}
		
	
		
	}

}
