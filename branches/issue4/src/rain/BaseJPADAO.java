/*
 * Created on Nov 6, 2008
 *
 */
package rain;

import java.util.Properties;

import javax.persistence.EntityManager;

import com.spaceprogram.simplejpa.EntityManagerFactoryImpl;

/**
 * This class implements the base features of the JPA dao
 * 
 * @author juliano (c) 2008 Boltblue International Limited
 */
public class BaseJPADAO {

	static EntityManagerFactoryImpl factory;

	private static String awsAccessId;
	private static String awsSecretKey;
	private static String persistenceUnitName;

	private static String endpointURL;

	public static String getPersistenceUnitName() {
		return persistenceUnitName;
	}

	public static void setPersistenceUnitName(String persistenceUnitName) {
		BaseJPADAO.persistenceUnitName = persistenceUnitName;
	}

	public synchronized static EntityManager getEntityManager() {

		if (factory == null) {
			Properties prop = new Properties();
			prop.put("accessKey", awsAccessId);
			prop.put("secretKey", awsSecretKey);
			prop.put("sessionless", "true");
			if(endpointURL!=null)
				prop.put("endpointURL", endpointURL);
			

			factory = new EntityManagerFactoryImpl(persistenceUnitName, prop);
		}
		
		return factory.createEntityManager();

	}

	public static String getAwsAccessId() {
		return awsAccessId;
	}

	public static void setAwsAccessId(String awsAccessId) {
		BaseJPADAO.awsAccessId = awsAccessId;
	}

	public static String getAwsSecretKey() {
		return awsSecretKey;
	}

	public static void setAwsSecretKey(String awsSecretKey) {
		BaseJPADAO.awsSecretKey = awsSecretKey;
	}

	public static void setEndpointURL(String url) {
	
		endpointURL=url;
		
	}

}
