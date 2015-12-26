package general;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import suffixtree.SuffixTreeNode;

import algorithms.*;

public class CompareOutputs 
{
	private static boolean isSolution1ContainedInSolution2(Interval curr, List list2)
	{
		
		for(int i=0;i<list2.size() ;i++)
		{
			Interval curr2=(Interval)list2.get(i);
			
			
			if(curr2.getStart().getIndex1()==curr.getStart().getIndex1()
					&& curr2.getStart().getIndex2()==curr.getStart().getIndex2()
					&& curr2.getEnd().getIndex1()==curr.getEnd().getIndex1()
					&& curr2.getEnd().getIndex2()==curr.getEnd().getIndex2())
				return true;
			else if(curr2.getStart().getIndex1()<=curr.getStart().getIndex1()
					&& curr2.getStart().getIndex2()<=curr.getStart().getIndex2()
					&& curr2.getEnd().getIndex1()>=curr.getEnd().getIndex1()
					&& curr2.getEnd().getIndex2()>=curr.getEnd().getIndex2())
				return true;
		}
		return false;
	}
	
	
	/**
	 * Since two algorithms work by different schemes, the size of an output can be comparable only 
	 * if the solutions produced by algorithms are converted into maximal solutions.
	 * It may be time consuming process depending on the produced solutions size.
	 * This class does it and in addition finds out if all solutions of the two algorithms are identical
	 * May be time consuming, if the original output consists of tenths thousand patterns. 
	 * @param args
	 */
	public static void main(String [] args)
	{
		String file1="";
		String file2="";
		int maxDiff=0;
		int minLen=0;		
		
		boolean identical=true;
		
		try
		{
			file1=args[0];
			file2=args[1];
			maxDiff=Integer.parseInt(args[5]);
			minLen=Integer.parseInt(args[4]);
			
			String suffixTreeFileName1=args[2];
			String suffixTreeFileName2=args[3];	
			
			SequenceFileReader reader =new SequenceFileReader(file1);
			String seq1=reader.getSequence();
			reader =new SequenceFileReader(file2);
			String seq2=reader.getSequence();			
			
			System.out.println("APBT algorithm");
			long start=System.currentTimeMillis();
			APBT algorithm2=new APBT(seq1.toCharArray(),seq2.toCharArray(),minLen,maxDiff);
			algorithm2.process();
			algorithm2.processByColumn(seq2.toCharArray(),seq1.toCharArray());
			
			long howlong=System.currentTimeMillis()-start;
			System.out.println("APBT Processed in "+howlong+" ms.");
			
			List solutions2=algorithm2.getMaximalSolutions();			
		
			System.out.println("Produced output size="+solutions2.size());
			
			SuffixTreeNode tree1=null;
			SuffixTreeNode tree2=null;
			try
			{
				FileInputStream in = new FileInputStream(suffixTreeFileName1);
				ObjectInputStream is = new ObjectInputStream(in);
				tree1 = (SuffixTreeNode)is.readObject();
				is.close();
				in.close();
			}
			catch(Exception e)
			{
				System.out.println("File "+suffixTreeFileName1+
						" not found where expected or is of an invalid type");
				e.printStackTrace();
				System.exit(1);
			}		

			try
			{
				FileInputStream in = new FileInputStream(suffixTreeFileName2);
				ObjectInputStream is = new ObjectInputStream(in);
				tree2 = (SuffixTreeNode)is.readObject();
				is.close();
				in.close();
			}
			catch(Exception e)
			{
				System.out.println("File "+suffixTreeFileName2+ 
						" not found where expected or is of an invalid type");
				e.printStackTrace();
				return;
			}
			
			System.out.println("BYGU algorithm");
			start=System.currentTimeMillis();
			BYGU algorithm1=new BYGU(tree1,tree2,
					seq1,"$",seq2,"#",maxDiff,minLen);
			
			algorithm1.process();			

			howlong=System.currentTimeMillis()-start;
			System.out.println("BYGU Processed in "+howlong+" ms.");
			
			List solutions1=algorithm1.getMaximalSolutions();			
		
			System.out.println("Output size="+solutions1.size());
			
			//check if all solutions1 contained in solutions2
			for(int i=0;i<solutions1.size();i++)
			{
				Interval curr=(Interval)solutions1.get(i);
				if(!isSolution1ContainedInSolution2(curr,solutions2))
				{
					identical=false;
					System.out.println("******************");					
					System.out.println("Found by BYGU, missed by APBT: "+curr);
					System.out.println(seq1.substring(curr.getStart().getIndex1(),curr.getEnd().getIndex1()+1));
					System.out.println(seq2.substring(curr.getStart().getIndex2(),curr.getEnd().getIndex2()+1));
					System.out.println();				
						
				}
			}
			
			//check if all solutions2 contained in solutions1
			for(int i=0;i<solutions2.size();i++)
			{
				Interval curr=(Interval)solutions2.get(i);
				if(!isSolution1ContainedInSolution2(curr,solutions1))
				{
					identical=false;
					System.out.println("******************");					
					System.out.println("Found by APBT, missed by BYGU: "+curr);
					System.out.println(seq1.substring(curr.getStart().getIndex1(),curr.getEnd().getIndex1()+1));
					System.out.println(seq2.substring(curr.getStart().getIndex2(),curr.getEnd().getIndex2()+1));
					System.out.println();				
						
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Usage: \n" + 
					"java -Xmx512M -Xms512m -classpath classes algorithms.BYGUPerformance \\ \n" +
	                		"<filename1> <filename2> \\ \n" +
	                		"<suffixtreefilename1> <suffixtreefilename2> \\ \n" +
	                		"<minLength> <maxDifferences>" );					

				System.exit(1);

		}
		if(identical)
			System.out.println("The outputs are identical.");
	}
}
