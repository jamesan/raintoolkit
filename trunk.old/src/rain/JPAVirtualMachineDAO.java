/*
 * Created on Nov 6, 2008
 *
 */
package rain;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class JPAVirtualMachineDAO extends BaseJPADAO implements
		VirtualMachineDAO {

	public void delete(VirtualMachine vm) {
		EntityManager em=getEntityManager();
		try {
			em.remove(vm);
			em.getTransaction().commit();
		
		}
		finally {
			em.close();
		}

	}

	public List<VirtualMachine> findAll() {
	
		EntityManager em=getEntityManager();
		try {
			Query q=em.createQuery("select v from rain.VirtualMachine v");
			return q.getResultList();
		}
		finally {
			em.close();
			
		}
	
		
	}

	public VirtualMachine findByName(String name) {
		EntityManager em=getEntityManager();
		try {
			Query q=em.createQuery("select v from rain.VirtualMachine v where v.name=:name");
			q.setParameter("name", name);
			List<VirtualMachine> resultList=q.getResultList();
			if(resultList.size()==0)
				return null;
			return resultList.get(0);
		}
		finally {
			em.close();
			
		}
	
		
	}

	public void saveOrUpdate(VirtualMachine vm) {
		EntityManager em=getEntityManager();
		
		try {
			em.persist(vm);
			em.getTransaction().commit();
		}
		finally {
			em.close();
		}
		
	}

	public VirtualMachine findByStaticIpAddress(String ip) {
		EntityManager em=getEntityManager();
		try {
			Query q=em.createQuery("select v from rain.VirtualMachine v where v.staticIpAddress=:ip");
			q.setParameter("ip", ip);
			List<VirtualMachine> resultList=q.getResultList();
			if(resultList.size()==0)
				return null;
			return resultList.get(0);
		}
		finally {
			em.close();
			
		}
	
	}

	public VirtualMachine findByInstanceId(String instanceId) {
		EntityManager em=getEntityManager();
		try {
			Query q=em.createQuery("select v from rain.VirtualMachine v where v.currentInstance=:i");
			q.setParameter("i", instanceId);
			List<VirtualMachine> resultList=q.getResultList();
			if(resultList.size()==0)
				return null;
			return resultList.get(0);
		}
		finally {
			em.close();
			
		}
	}

}
