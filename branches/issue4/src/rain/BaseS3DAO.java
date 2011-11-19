/*
 * Created on Nov 5, 2008
 *
 */
package rain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;

import com.sun.org.apache.xerces.internal.dom.EntityImpl;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.SortableFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;

public abstract class BaseS3DAO {

	
	
	protected String bucketName;
	protected String fileName;
	protected S3Service s3Service;
	protected XStream stream;
	
	
	
	public BaseS3DAO() {
		SortableFieldKeySorter sorter = new SortableFieldKeySorter();
		sorter.registerFieldOrder(S3Store.class, new String[] { "volumes", "virtualMachines" });
		stream =  new XStream(new Sun14ReflectionProvider(new FieldDictionary(sorter)),new DomDriver());
		stream.alias("VirtualMachine", VirtualMachine.class);
		stream.alias("Volume", Volume.class);
		stream.alias("Store", S3Store.class);
		stream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
		

		store=new ArrayList<List>();
		store.add(new ArrayList<VirtualMachine>());
		store.add(new ArrayList<Volume>());

	}
	
	protected List<List> store;
	

	public XStream getStream() {
		return stream;
	}

	
	public S3Service getS3Service() {
		return s3Service;
	}

	public void setS3Service(S3Service service) {
		s3Service = service;
	}

	public String getSetBucketName() {
		return bucketName;
	}

	public void setSetBucketName(String setBucketName) {
		this.bucketName = setBucketName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String setFileName) {
		this.fileName = setFileName;
	}

	protected S3Bucket getS3Bucket() throws S3ServiceException {
		S3Bucket bucket = s3Service.getBucket(bucketName);
		return bucket;
	}
	
	protected void saveEntityList() {
		try {
			S3Bucket bucket=getS3Bucket();
			if(bucket==null)
				bucket=s3Service.createBucket(bucketName);
			S3Object file=new S3Object(bucket,fileName);
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			stream.toXML(store, out);
			ByteArrayInputStream in=new ByteArrayInputStream(out.toByteArray());
			file.setContentType("text/xml");
			file.setContentLength(in.available());
			file.setDataInputStream(in);
			s3Service.putObject(bucket, file);
			
		} catch (S3ServiceException e) {
			throw new RuntimeException(e);
		}
	
	}
	
	protected void loadEntityList() {
		try {
			S3Bucket bucket = getS3Bucket();
			if (bucket != null) {
				
				S3Object file = null;
				try {
					file=s3Service.getObject(bucket, fileName);
				}
				catch(Exception e) {
					// First time case...
				}
				if (file != null) {
					
					store= (List<List>) stream
							.fromXML(file.getDataInputStream());
					 
					 
					
					
				}
				

			}

		} catch (S3ServiceException e) {

			throw new RuntimeException(e);
			
		}
	}
	
	protected List<Volume> getVolumes() {
		loadEntityList();
		return store.get(1);
	}
	protected List<VirtualMachine> getMachines() {
		loadEntityList();
		
		return store.get(0);
		
	}

}
