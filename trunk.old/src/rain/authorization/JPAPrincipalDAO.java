/*
 * Created on Nov 20, 2008
 *
 */
package rain.authorization;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import rain.BaseJPADAO;


public class JPAPrincipalDAO extends BaseJPADAO implements PrincipalDAO {

	public void addMembership(Principal principal, Group group) {
		
		GroupMembership membership=new GroupMembership();
		membership.setPrincipal(principal);
		membership.setGroup(group);
		EntityManager em=getEntityManager();
		try {
			em.persist(membership);
		}
		finally {
			em.close();
		}

	}

	public List<Principal> findAllUsers() {
	
		EntityManager em=getEntityManager();
		try {
			Query q=em.createQuery("select v from rain.authorization.Principal v");
			
			// Filter out the groups
			List<Principal> result= q.getResultList();
			ArrayList<Principal> filteredResults=new ArrayList<Principal>();
			for(Principal p: result) {
				if(!(p instanceof Group)) {
					filteredResults.add(p);
				}
			}
			
			return filteredResults;
		}
		finally {
			em.close();
			
		}
		
	}

	public List<Group> findAllGroups() {
		EntityManager em=getEntityManager();
		try {
			Query q=em.createQuery("select v from rain.authorization.Group v");
			return q.getResultList();
		}
		finally {
			em.close();
			
		}
		
	}

	public Principal findByName(String name) {
	
		EntityManager em=getEntityManager();
		try {
			Query q=em.createQuery("select v from rain.authorization.Principal v where v.name=:name");
			q.setParameter("name", name);
			
			List<Principal> result=q.getResultList();
			if(result.size()==0)
				return null;
			return result.get(0);
			
		}
		finally {
			em.close();
			
		}
		
	}

	public List<Group> findGroups(Principal principal) {
		
		EntityManager em=getEntityManager();
		try {
			Query q=em.createQuery("select v from rain.authorization.GroupMembership  v where v.principal.name=:name");
			q.setParameter("name", principal.getName());
			List<GroupMembership> result=q.getResultList();
			List<Group> groups=new ArrayList<Group>();
			for(GroupMembership m: result) {
				groups.add(m.getGroup());
			}
			return groups;
		}
		finally {
			em.close();
			
		}
		
	}

	public List<Principal> findMembers(Group group) {
		EntityManager em=getEntityManager();
		try {
			Query q=em.createQuery("select v from rain.authorization.GroupMembership  v where v.group.name=:name");
			q.setParameter("name", group.getName());
			List<GroupMembership> result=q.getResultList();
			List<Principal> principals=new ArrayList<Principal>();
			for(GroupMembership m: result) {
				principals.add(m.getPrincipal());
			}
			return principals;
		}
		finally {
			em.close();
			
		}
	}

	public void removeMembership(Principal principal, Group group) {
		
		EntityManager em=getEntityManager();
		try {
			Query q=em.createQuery("select v from rain.authorization.GroupMembership  v where v.group.name=:gname and v.principal.name=:pname");
			q.setParameter("gname", group.getName());
			q.setParameter("pname",principal.getName());
			List<GroupMembership> result=q.getResultList();
			for(GroupMembership m: result) {
				em.remove(m);
			}
			
		}
		finally {
			em.close();
			
		}

	}

	public void saveOrUpdate(Principal p) {
		
		EntityManager em=getEntityManager();
		try {
			em.persist(p);
			em.getTransaction().commit();
		}
		finally {
			em.close();
		}
		
	}

	public Group findGroupByName(String groupName) {
		EntityManager em=getEntityManager();
		try {
			Query q=em.createQuery("select v from rain.authorization.Group v where v.name=:name");
			q.setParameter("name", groupName);
			
			List<Group> result=q.getResultList();
			if(result.size()==0)
				return null;
			return result.get(0);
			
		}
		finally {
			em.close();
			
		}
	}

	public Principal findByAccessKeyId(String accessKeyId) {
		EntityManager em=getEntityManager();
		try {
			Query q=em.createQuery("select v from rain.authorization.Principal v where v.accessId=:id");
			q.setParameter("id", accessKeyId);
			
			List<Group> result=q.getResultList();
			if(result.size()==0)
				return null;
			return result.get(0);
			
		}
		finally {
			em.close();
			
		}
		
	}

}
