/*
 * Created on Nov 19, 2008
 *
 */
package rain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultGlueOutput implements RainOutput {

	private List<String> labels=new ArrayList<String>();
	
	private List<Map<String,String>> lineValues=new ArrayList<Map<String,String>>();
	
	
	
	public void endLine() {
		

	}

	public void endSection() {

		
		// Calculate the size of each column
		
		int[] columnSizes=new int[labels.size()];
		
		for(int i=0;i<columnSizes.length;i++) {
			String label=labels.get(i);
			int maxLength=label.length();
			for(int j=0;j<lineValues.size();j++) {
				String value=lineValues.get(j).get(label);
				if(value==null)
					value="";
				
				if(value.length()>maxLength)
					maxLength=value.length();
			}
			columnSizes[i]=maxLength+5;
		}
		
		
		
		// Prints the header as a line 
		
		for(int i=0;i<labels.size();i++)  {
			String label=labels.get(i);
			printPadding(label,columnSizes[i]);
			
		}
		
		System.out.print("\n");
		
		for(int i=0;i<lineValues.size();i++) {
			for(int j=0;j<labels.size();j++) {
				String value=lineValues.get(i).get(labels.get(j));
				if(value==null)
					value="";
				printPadding(value, columnSizes[j]);
				
				
				
			}
			System.out.print("\n");
			
		}

	}

	private void printPadding(String label, int size) {
		
		//System.out.print(label);
		
		System.out.printf("%-"+size+"s",label);
		
	}

	public void printValue(String label, String value) {
		
		if(!labels.contains(label))
			labels.add(label);
		
		Map<String,String> labelsAndValues=lineValues.get(lineValues.size()-1);
		labelsAndValues.put(label, value);
		

	}

	public void startLine() {
		
		lineValues.add(new HashMap<String,String>());

	}

	public void startSection(String title) {
		
		if(title!="")
			System.out.println(title);
	
		labels.clear();
		lineValues.clear();
		
	}

	public void printError(String error) {
		System.err.println(error);
		
	}

	public void printStackTrace(String error, Throwable t) {
		System.err.println(error);
		t.printStackTrace();
		
	}

}
