/*
 * Created on Nov 20, 2008
 *
 */
package rain;

/**
 * Base class for engines that need to communicate with AWS services in an
 * uniform way
 * 
 * @author juliano (c) 2008 Boltblue International Limited
 */
public class BaseEngine {

	protected String aws_access_id;
	protected String aws_secret_key;
	protected String aws_account_id;
	protected String glue_home;
	protected String endpointURL;

	public BaseEngine() {

		aws_access_id = System.getenv("AWS_ACCESS_ID");

		aws_secret_key = System.getenv("AWS_SECRET_KEY");

		glue_home = System.getenv("RAIN_HOME");
		endpointURL = System.getenv("EC2_ENDPOINT_URL");

		if (aws_access_id == null || aws_secret_key == null)
			throw new RuntimeException(
					"You need to set your AWS_ACCESS_ID and AWS_SECRET_KEY environment variables");

		if (glue_home == null)
			throw new RuntimeException(
					"You need to set your RAIN_HOME environment variable");
		
		initDAO();
	}

	private void initDAO() {
		BaseJPADAO.setAwsAccessId(aws_access_id);
		BaseJPADAO.setAwsSecretKey(aws_secret_key);
		BaseJPADAO.setPersistenceUnitName("rain");
		BaseJPADAO.setEndpointURL(System.getenv("SIMPLEDB_ENDPOINT_URL"));
		
	}

	public BaseEngine(String aws_access_id,String aws_secret_key,String aws_account_id,String glue_home,String endpointURL) {

		this.aws_access_id=aws_access_id;
		this.aws_secret_key=aws_secret_key;
		this.aws_account_id=aws_account_id;
		this.glue_home=glue_home;
		this.endpointURL=endpointURL;
		
		initDAO();
		
		
	}

}
