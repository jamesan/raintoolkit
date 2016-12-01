/*
 * Created on Nov 5, 2008
 *
 */
package rain;

import java.util.List;

/**
 * This interface defines the data access methods for the Volume entity
 * @author juliano
 * (c) 2008 Boltblue International Limited
 */
public interface VolumeDAO {
	
	public Volume findByName(String name);
	public Volume findByVolumeId(String volumeId);
	
	public void saveOrUpdate(Volume volume);
	
	public List<Volume> findAll();

}
