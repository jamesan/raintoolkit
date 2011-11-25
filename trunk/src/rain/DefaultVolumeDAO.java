/*
 * Created on Nov 5, 2008
 *
 */
package rain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;

public class DefaultVolumeDAO extends BaseS3DAO implements VolumeDAO {

	public Volume findByName(String name) {
		
		
		List<Volume> volumes=loadVolumes();
		for(Volume v: volumes) {
			if(v.getName().equals(name))
				return v;
		}
		
		return null;
		
	}

	private List<Volume> loadVolumes() {
		
		return getVolumes();
	}

	public Volume findByVolumeId(String volumeId) {
		
		List<Volume> volumes= loadVolumes();
		
		for(Volume v: volumes) {
			if(v.getVolumeId().equals(volumeId)) {
				return v;
			}
		}
		
		return null;
	}

	public void saveOrUpdate(Volume volume) {
		
		List<Volume> volumes = loadVolumes();
		
		Volume currentVolume=null;
		for(Volume v: volumes) {
			if(v.getName().equals(volume.getName()))
				currentVolume=v;
		}
		
		if(currentVolume==null) {
			volumes.add(volume);
		}
		else {
			volumes.remove(currentVolume);
			volumes.add(volume);
		}
			
		saveEntityList();
	}

	

	public List<Volume> findAll() {
		return loadVolumes();
	}

}
