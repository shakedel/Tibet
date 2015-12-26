
package algorithms;


import java.util.*;
import java.io.*;

import general.*;



/** This class implements the APBT algorithm for finding
 * in two strings, all the pairs of common approximate patterns, 
 * which satisfy the following criteria:
 * the length of both patterns in a pair is at least minLength, 
 * and the edit distance between patterns (in a pair) is at most maxDiff.
 * Max length of patterns is set to 300 characters. 
 * If patterns of length greater than 300 are expected, 
 * then they can be obtained either by appropriate post-processing or 
 * by re-runing this program with a bigger value for MAX_LENGTH.
 *
 *  @author marina
*/



public class APBT
{	
	/** This constant defines the size of a chunk of the matching matrix to be processed simultaneously. 
	 * This speeds up the calculation by decreasing the memory used. 
	 * The simultaneously processed part of a matrix is of size: 
	 * CHUNK_SIZE*_length1, where _length1 is the length of the first string.
	 */
	public final int CHUNK_SIZE=1000;
	
	/** This variable defines the maximum length of matches. 
	 */
	public int MAX_LENGTH=300;
	
	
	
	int _chunkSize;
	int _maxLength;
	List _solutions=new LinkedList();	
	Map _solutionsMap=new HashMap();
	int _maxDiff;
	int _minLength;
	
	Map _charPositions2;

	boolean [][] _matrix;
	public String _sequence1;
	public String _sequence2;
	int _length1;
	int _length2;
	char [] _seq1;
	char [] _seq2;
	char [] _originalSeq1;
	char [] _originalSeq2;
	Object[][]  _state;	
	

	private boolean byColumn=false;
	
	
	/** Constructor with char arrays instead of strings - to avoid the copying of big strings 
	 * when processing multiple times.
	 * @param seq1arr - char array representing string 1
	 * @param seq2arr - char array representing string 2
	 * @param minLength - the minimum length of a solution pattern
	 * @param maxDiff - the maximum number of allowed errors
	 */
	public APBT(char [] seq1arr, char [] seq2arr,
			int minLength, int maxDiff)
	{		
		_seq1=seq1arr;
		_seq2=seq2arr;
		_length1=_seq1.length;
		_length2=_seq2.length;
		
			    
		_matrix=new boolean[_length1][];
		_minLength=minLength;
		_maxDiff=maxDiff;
		
		_maxLength=MAX_LENGTH;
		_chunkSize=CHUNK_SIZE;
	}
	
	
	/**
	* The main recursion - expanding of the path. 
	* Stop conditions: the path cannot be expanded without an additional error,
	* OR a better path has been collected ending at point (currI, currJ)
	*/

	private void continuePath(int startI, int startJ, int currI, int currJ, int currlen, int currdiff,int shiftJ,boolean continueFurther)
	{		
		if(currlen>=_maxLength)
			return;
		

		if(continueFurther)		
		{			
			if(currlen>1)
			{						
				int []  instate=(int [])_state[currI%_maxLength][currJ];			
				
				if(instate==null)
				{				
					instate=new int[2];	
					if(currI-startI<=currJ-startJ)
					{
						instate[0]=currdiff;
						instate[1]=_maxDiff+1;
					}
					else
					{
						instate[1]=currdiff;
						instate[0]=_maxDiff+1;
						//instate[1]=_maxDiff+1;
					}
					_state[currI%_maxLength][currJ]=instate;
				}
				else //check what is the best (minimum diff) checked for this point
				{					
					if(currI-startI<=currJ-startJ)
					{
						if(instate[0]<=currdiff)
							return;
						else
							instate[0]=currdiff;
						
					}
					else
					{
						if(instate[1]<=currdiff)
							return;
						else
							instate[1]=currdiff;
					}
											
					_state[currI%_maxLength][currJ]=instate;						
					
				}				
			
					
			}


			//define bounds of the target square
			int LT_I=Math.min(currI+1,_length1-1);
			int LT_J=Math.min(currJ+1,_length2-1);
	
			int RT_J=Math.min(LT_J+_maxDiff+1-currdiff,_length2);
			int LB_I=Math.min(LT_I+_maxDiff+1-currdiff,_length1);
		
			int RB_J=RT_J;		
	
			int currJBound=RB_J;
			int currIBound=LB_I;	 
			
			//explore the main diagonal
			boolean stop=false;
	
			for(int i=currI+1,j=currJ+1;
					i<Math.min(currI+_maxDiff+2-currdiff,_length1) && 
					j<Math.min(currJ+_maxDiff+2-currdiff,_length2) && !stop;i++,j++)
			{
				if(_matrix[i][j])
				{
									
					continuePath(startI,startJ,i,j,Math.min(i-startI,j-startJ)+1, Math.max(i-currI,j-currJ)-1+currdiff,shiftJ,true);
					stop=true;
					currJBound=j;
					currIBound=i;
				}
			}
				
			for(int i=currIBound+1;i<LB_I;i++)
			{
				if(_matrix[i][currJBound])
					continuePath(startI,startJ,i,currJBound,Math.min(i-startI,currJBound-startJ)+1, Math.max(i-currI,currJBound-currJ)-1+currdiff,shiftJ,false);
			}
			
			for(int j=currJBound+1;j<RT_J;j++)
			{
				if(_matrix[currIBound][j])
					continuePath(startI,startJ,currIBound,j,Math.min(currIBound-startI,j-startJ)+1, Math.max(currIBound-currI,j-currJ)-1+currdiff,shiftJ,false);
			}
	
			//add an edge from next k upper and lower diagonals		
			for(int k=1;k<_maxDiff+1-currdiff ;k++)
			{
				//lower diagonals			
				stop=false;
				for(int i=LT_I+k, j=LT_J;i<LB_I && j<currJBound && !stop;i++,j++)
				{
					if(_matrix[i][j])
					{
						
						continuePath(startI,startJ,i,j,Math.min(i-startI,j-startJ)+1, Math.max(i-currI,j-currJ)-1+currdiff,shiftJ,true);
						if(j<currJBound)
						{
							currJBound=j;
							for(int m=i+1;m<LB_I;m++)
							{
								if(_matrix[m][currJBound])
									continuePath(startI,startJ,m,currJBound,Math.min(m-startI,currJBound-startJ)+1, Math.max(m-currI,currJBound-currJ)-1+currdiff,shiftJ,false);
							}
							
						}
						stop=true;
					}
				}
	
				//upper diagonals	
				stop=false;
				for(int i=LT_I, j=LT_J+k;i<currIBound && j<RT_J && !stop;i++,j++)
				{
					if(_matrix[i][j])
					{
						
						continuePath(startI,startJ,i,j,Math.min(i-startI,j-startJ)+1, Math.max(i-currI,j-currJ)-1+currdiff,shiftJ,true);
						if(i<currIBound)
						{
							currIBound=i;
							for(int m=j+1;m<RT_J;m++)
							{
								
								if(_matrix[currIBound][m])
									continuePath(startI,startJ,currIBound,m,Math.min(currIBound-startI,m-startJ)+1, Math.max(currIBound-currI,m-currJ)-1+currdiff,shiftJ,false);
							}
							
						}
							
						stop=true;
					}
				}
			}	
		}
		
//		the maximal path can not continue. Check if this is a solution
		if(currlen>=_minLength )
		{	
			addToSolutions(startI,  startJ+shiftJ,  currI,  currJ+shiftJ);
		}

	}	

	
	


	/********************************************************************************************
	 *  PUBLIC 
	 ********************************************************************************************/

	
	/**
	 * This function processes the matching matrix for specified start positions
	 * in a row major order.
	 * The processing is performed by chunks.
	 *
	 */	
	public void process()
	{			
		int chunksNumber=_length2/_chunkSize;
		
		for(int k=0;k<=chunksNumber;k++)
		{
			int startJ=k*_chunkSize;
			process(startJ);			
		}		
	}
	
	/**
	 * This function processes the matching matrix for specified start positions
	 * in a column major order (by reversing 2 strings). 
	 * The processing is performed by chunks.
	 *
	 */	
	public void processByColumn(char [] first, char [] second)
	{			
		_originalSeq1=second;
		_originalSeq2=first;
		
		_seq1=first;
		_seq2=second;
		
		_length1=_seq1.length;
		_length2=_seq2.length;
		
		    
		_matrix=new boolean[_length1][];
		
		int chunksNumber=_length2/_chunkSize;
		
		byColumn=true;
		for(int k=0;k<=chunksNumber;k++)
		{
			int startJ=k*_chunkSize;
			process(startJ);			
		}
		
		_seq1=_originalSeq1;
		_seq2=_originalSeq2;
		Collections.sort(_solutions, new IntervalComparator());
	}

	
	/**
	 * 
	 * @return sorted List of intervals in a two-dimensional space.
	 * The Interval contains the coordinates of the start (start position in string1, start position in string 2) and
	 * of the end (end position in string1, end position in string 2).
	 */
	public List getSolutions()
	{
		
		return _solutions;
	}
	
	/**
	 * This function allows to serialize the solutions List into a file, which can be read and processed by another program.
	 * @param solutions - full or maximal list of solutions
	 * @param outputfilename - the name of binary file where the solution LinkedList object will be saved
	 */
	public void savePatterns(List solutions, String outputfilename)
	{
		try
		{
			FileOutputStream out = new FileOutputStream(outputfilename);
			ObjectOutputStream s = new ObjectOutputStream(out);
			s.writeObject(solutions);	
			s.flush();
			s.close();
			out.close();
		}
		catch(Exception e){}
	}
	
	/**
	 * We try to expand paths to maximal length, but some of them may still be non-maximal.
	 * This method is called in order to output only maximal solurtions.
	 * @return
	 */
	public List getMaximalSolutions()
	{
 		
 		List ret=new LinkedList();
 		if(_solutions.size()<=1)
			return _solutions;
 		
 		int maxI=0;
 		
 		
 		Interval first=(Interval)_solutions.get(0);
 		maxI=first.getEnd().getIndex1();
 		
 		List temp=new LinkedList();
 		for(int i=0;i<_solutions.size();i++)
 		{ 			
 			Interval curr=(Interval)_solutions.get(i);
 			if(curr.getStart().getIndex1()<=maxI-_minLength+1 )
 			{
 				temp.add(curr);
 				maxI=Math.max(maxI,curr.getEnd().getIndex1());
 				if(i==_solutions.size()-1)
 					createMaximalSolutions(temp,ret);
 				
 			}
 			else
 			{
 				createMaximalSolutions(temp,ret);
 				temp=new LinkedList();
 				temp.add(curr);
 				maxI=curr.getEnd().getIndex1();
 				
 				if(i==_solutions.size()-1)
 					createMaximalSolutions(temp,ret);
 			}
 		}
 		
		return ret;

	}
	/********************************************************************************************
	 *  PRIVATE
	 ********************************************************************************************/
	
	/** This function processes the parts of the matrix starting from particular position in the second string.
	* @param startJ - start position in a second string
	*/
	
	private void process(int startJ)
	{
	
		if(startJ>_length2-_minLength)
			return;
		initializeMatrix(startJ,startJ+_chunkSize+_maxLength);
		
		for(int i=0;i<=_length1-_minLength;i++)
		{	
			
			for(int j=0;j<=Math.min(_chunkSize,_length2-_minLength);j++)
			{				
				
				if(_matrix[i][j])
				{					
					createPaths(i,j,startJ);				
				}
			}
			//reset the row of _state array to use with the next added (i+_maxLength)-th row: 
			_state[(i)%_maxLength]=new Object[Math.min(_chunkSize+_maxLength,_length2)];
			
		}		
	}
	
	
	/** Initializes a new path of ML=1, EN=0, starting from current true cell of the matrix
	*/	
	private void createPaths(int startI, int startJ ,int shiftJ)
	{
		
		continuePath(startI,startJ,startI,startJ,1,0,shiftJ,true);
	}		
	
	private void initializeMatrix(int from, int to)
	{
		_charPositions2=new HashMap(20);
		for(int j=from;j<Math.min(to,_length2);j++)
		{
			char curr=_seq2[j];
			boolean [] row=(boolean [])_charPositions2.get(new Character(curr));
			if(row==null)
				row=new boolean[Math.min(_chunkSize+_maxLength,_length2)];
			row[j-from]=true;
			_charPositions2.put(new Character(curr),row);
		}		
		
		int ccTemp = 0;
		HashSet<Character> charSet = new HashSet<Character>();
		for(int i=0;i<_length1;i++)
		{
			char curr=_seq1[i];
			if(_charPositions2.get(new Character(curr))!=null)
			{
				boolean [] row=(boolean [])_charPositions2.get(new Character(curr));
				_matrix[i]=row;
			}
			else
			{
				ccTemp++;
				charSet.add(new Character(curr));
				_matrix[i]=new boolean[_length2];
				
				//_charPositions2.put(new Character(curr),_matrix[i]);
			}
		}
		
		_state=new Object[_maxLength][Math.min(_chunkSize+_maxLength,_length2)];
		
	}
	

	
	
	private void addToSolutions(int startI, int startJ, int currI, int currJ)
	{
		
		
		IndexPair start=null;
		IndexPair end=null;
		if(byColumn)
		{
			start=new IndexPair(startJ,startI);
			end=new IndexPair(currJ,currI);
		}
		else
		{
			start=new IndexPair(startI,startJ);
			end=new IndexPair(currI,currJ);
		}
		
		Interval interval=new Interval(start,end);
		if(!_solutionsMap.containsKey(interval.toString()))
		{
			_solutionsMap.put(interval.toString(),null);
			_solutions.add(interval);
		}
	}
	

	private void createMaximalSolutions(List overlappings, List res)
 	{
 		if(overlappings.size()==1)
 		{
 			res.add(overlappings.get(0));
 			return;
 		}
 		for(int i=0;i<overlappings.size();i++)
 		{
 			Interval curr1=(Interval)overlappings.get(i);
 			for(int j=0;j<overlappings.size();j++)
 			{
 				if(i!=j)
 				{
 					Interval curr2=(Interval)overlappings.get(j);
 					if(curr1!=null && curr2!=null)
 					{
 						if(curr2.getStart().getIndex1()>=curr1.getStart().getIndex1() &&
 								curr2.getStart().getIndex2()>=curr1.getStart().getIndex2()
 								&& curr2.getEnd().getIndex1()<=curr1.getEnd().getIndex1()
 								&& curr2.getEnd().getIndex2()<=curr1.getEnd().getIndex2())
 							overlappings.set(j,null);
 					}
 				}
 			}
 		}
 		
 		for(int i=0;i<overlappings.size();i++)
 		{
 			Interval curr=(Interval)overlappings.get(i);
 			if(curr!=null)
 				res.add(curr);
 		}
 	}	
	
 	
 /**
  * Demonstrates how to use APBT. 
  * Note, that computation is repeated in row-major and column-major order.
  * But since the 2-dimensional matrix consists in fact from a number of 
  * one-dimensional arrays of positions, it is faster to rebuild the matrix 
  * and to reprocess, now seq1 is horizontal, seq2 - vertical 
  */	

	
	public static void main(String [] args)
	{
		
		String file1=null;		
		String file2=null;
		int maxDiff=0;
		int minLen=0;
		
		boolean maximalSolutions=false;
		boolean printOutput=false;
		
		try
		{
			file1=args[0];		
			file2=args[1];
			
			minLen=Integer.parseInt(args[2]);
			maxDiff=Integer.parseInt(args[3]);
			
			int yesno=Integer.parseInt(args[4]);
			
			if(yesno==1)
				maximalSolutions=true;
			
			int print=Integer.parseInt(args[5]);
			if(print==1)
				printOutput=true;
		}
		catch(Exception e)
		{
			System.out.println("Usage: \n" + 
					"java -Xmx512M -Xms512m  \n" +
            		"<filename1> <filename2>  \n" +
            		"<minLength> <maxDifferences> <maximalOutput: 1 | 0> <printStdOutput: 1 | 0>" );
			System.exit(1);
		}	
		
		System.out.println("APBT algorithm");		
		SequenceFileReader reader =new SequenceFileReader(file1);
		String seq1=reader.getSequence();
		reader =new SequenceFileReader(file2);
		String seq2=reader.getSequence();
			
		long start=System.currentTimeMillis();
			
		APBT algorithm=new APBT(seq1.toCharArray(),seq2.toCharArray(),
				minLen,maxDiff);
		algorithm.process();
		algorithm.processByColumn(seq2.toCharArray(), seq1.toCharArray());
			
				
		long howlong=System.currentTimeMillis()-start;
		System.out.println("Processed in "+howlong+" ms.");
		List solutions=algorithm.getSolutions();
		System.out.println("Produced output size="+solutions.size());
		
		if(maximalSolutions)
		{
			if(solutions.size()>10000)
			{
				System.out.println("Producing maximal solutions may be time consuming.");
				System.out.println("Do you still want to continue? y/n");
				String yesno=null;
				try
				{
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					yesno = br.readLine();
				}
				catch(Exception e)
				{
					System.exit(1);
				}
				if(yesno!=null && yesno.toLowerCase().startsWith("n"))
					System.exit(0);
			}
			
			List maximalsolutions=algorithm.getMaximalSolutions();	
			System.out.println("Maximal output size="+maximalsolutions.size());
			
			if(printOutput)
			{
				for(int i=0;i<maximalsolutions.size();i++)
				{
					Interval curr=(Interval)maximalsolutions.get(i);
					System.out.println(curr);
					System.out.println(seq1.substring(curr.getStart().getIndex1(),curr.getEnd().getIndex1()+1));
					System.out.println(seq2.substring(curr.getStart().getIndex2(),curr.getEnd().getIndex2()+1));
				}
			}
		}	
		else
		{
			if(printOutput)
			{
				for(int i=0;i<solutions.size();i++)
				{
					Interval curr=(Interval)solutions.get(i);
					System.out.println(curr);
					System.out.println(seq1.substring(curr.getStart().getIndex1(),curr.getEnd().getIndex1()+1));
					System.out.println(seq2.substring(curr.getStart().getIndex2(),curr.getEnd().getIndex2()+1));
				}
			}
			
		}		
		
	}

}


