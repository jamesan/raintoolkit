/*
 * Created on Nov 19, 2008
 *
 */
package rain;


/*
 * This interface defines the service used to output command results
 */
public interface RainOutput {

	
	public void startLine();
	public void printValue(String label,String value);
	public void endLine();
	public void startSection(String title);
	public void endSection();
	public void printError(String error);
	public void printStackTrace(String error,Throwable t);
	
	
}
