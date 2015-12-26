

package general;

/**
 * Created on Jul 22, 2005
 *@author mgbarsky
 * This class can be used in order to generate pseudo-random sequences of a predefined length
 * over a specified alphabet. Used for debug and testing purposes
  */


import java.util.Random;

import java.io.*;


public   class RandomStringGenerator
{
/**
 * Generates a pseudo-random string
 * @param len - the length of a String
 * @param alphabet - the char [] alphabet
 * @return pseudo-random String
 */
	
	public static String getRandomString(int len, char[] alphabet)
	{
		char [] seqAsArr=new char [len];		

		Random rand=new Random();

		for(int i=0;i<seqAsArr.length;i++)

		{

			seqAsArr[i]=alphabet[rand.nextInt(alphabet.length)];			

		}

		return(new String(seqAsArr));

	}

	/**
	 * Can save a pseudo-random String generated to file.
	 * The two sample alphabets are those of DNA and of protein sequences
	 * 
	 */

	public static void main (String [] args)throws Exception

	{

		int N=10000;

		char [] alphabet={'a','c','g','t'};
		//char [] alphabet={'a','c','d','e','f','g','h','i','k','l','m','n','p','q','r','s','t','v','w','y'};
		String randfilename="rand10000_2";
		

		String seq=RandomStringGenerator.getRandomString(N,alphabet);
		
		BufferedWriter bwout = new BufferedWriter(new FileWriter(randfilename));
		bwout.write(seq);			
		bwout.close();
		

	}

}


