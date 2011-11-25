/*
 * Created on Nov 4, 2008
 *
 */
package rain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

public abstract class BaseCommand implements Runnable {

	protected static Class thisClass;

	
	  protected static final SimpleDateFormat iso8601DateFormat = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	  
	  protected RainOutput output;
	  
	  
	  public BaseCommand() {
		  output=new DefaultGlueOutput();
		  
	  }

	public static void main(String[] args) {

		// Configure the logging system

		Logger rootLogger = Logger.getLogger("");

		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.FINEST);
		handler.setFormatter(new Formatter() {

			@Override
			public String format(LogRecord record) {

				String message=record.getMessage()+"\n";
				if(record.getThrown()!=null) {
					ByteArrayOutputStream out=new ByteArrayOutputStream();
					PrintStream pr=new PrintStream(out);
					record.getThrown().printStackTrace(pr);
					String trace=new String(out.toByteArray());
					message+=trace;
					
				}
				
				return message;

			}

		});

		rootLogger.addHandler(handler);
		rootLogger.setLevel(Level.SEVERE);
		
		Logger glueLogger=Logger.getLogger("rain");
		// Parse the verbose flag

		
		List<String> realArgs=new ArrayList<String>();
		
		boolean verbose=false;
		
		for(String s: args) {
			if(s.equals("-x")) {
				verbose=true;
			}
			else
				realArgs.add(s);
			
		}
		if(verbose)
			glueLogger.setLevel(Level.FINEST);
		
			
		
		// Create an instance of the actual type to be run

		Runnable newInstance;
		try {
			newInstance = (Runnable) thisClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);

		}
		List<String> extra;
		try {
			extra = Args.parse(newInstance, realArgs.toArray(new String[]{}));
		} catch (Exception e) {
			System.err.println(e.getMessage());
			Args.usage(newInstance);
			System.exit(1);
			return;
		}
		if (extra != null && extra.size() > 0) {
			Args.usage(newInstance);
			System.exit(1);
			return;

		}

		// Args parsed successfully - run command now

		try {
			newInstance.run();
			System.exit(0);
			
		}
		catch(Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}

	}
	
	protected String formatIso8601Date(Calendar date) {
		
		return iso8601DateFormat.format(date.getTime());
	}

}
