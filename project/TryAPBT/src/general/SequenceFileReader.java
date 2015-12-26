package general;

import java.io.*;

/**Created on Jul 12, 2005
 * @author mgbarsky
 * This is a simple util class which perfoms buffered reading of a single String from a file
 */

public class SequenceFileReader 

{
	
	private StringBuffer _sbuffer=new StringBuffer("");
	/**
	 * Constructor
	 * @param filename - the input file name with the single String in it
	 */
	public SequenceFileReader(String filename)

	{String thisLine;

		try {
				
	          FileInputStream fin =  new FileInputStream(filename);

	         
	          BufferedReader myInput = new BufferedReader

	              (new InputStreamReader(fin));

		          while ((thisLine = myInput.readLine()) != null) 

		          {  
		        	  	
		        	  _sbuffer.append(thisLine.trim().toLowerCase());

		          }

	          }

	       catch (Exception e) 

		   {
	    	   System.out.println("File "+filename +" is not found where expected or is of invalid type. ");
	       		System.exit(1);

	       }	

	}
	
/**
 *  @return the String as a String object
 */
	public String getSequence()
	{
		return _sbuffer.toString();
	}

}


