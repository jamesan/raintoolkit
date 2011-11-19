/*
 * Created on Nov 6, 2008
 *
 */
package rain;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class JPAVolumeDAO extends BaseJPADAO implements VolumeDAO {

	public List<Volume> findAll() {
		EntityManager em=getEntityManager();
		
		try {
			Query q=em.createQuery("select v from rain.Volume v");
			return q.getResultList();
		}
		finally {
			em.close();
		}
		
	}

	public Volume findByName(String name) {
		EntityManager em=getEntityManager();
		
		try {
			Query q=em.createQuery("select v from rain.Volume v where v.name=:name");
			q.setParameter("name", name);
			List<Volume> volumes= q.getResultList();
			if(volumes.size()==0)
				return null;
			
			return volumes.get(0);
		}
		finally {
			em.close();
		}
	}

	public Volume findByVolumeId(String volumeId) {
		EntityManager em=getEntityManager();
		
		try {
			Query q=em.createQuery("select v from rain.Volume v where v.volumeId=:volumeId");
			q.setParameter("volumeId", volumeId);
			List<Volume> volumes= q.getResultList();
			if(volumes.size()==0)
				return null;
			
			return volumes.get(0);
		}
		finally {
			em.close();
		}
	}

	public void saveOrUpdate(Volume volume) {
		EntityManager em=getEntityManager();
		try {
			em.persist(volume);
			em.getTransaction().commit();
			
		}
		finally {
			em.close();
		}

	}

	public void delete(Volume vol) {
	
		EntityManager em=getEntityManager();
		try {
			em.remove(vol);
			em.getTransaction().commit();
			
		}
		finally {
			em.close();
		}
		
		
	}

}
