package algorithms;

import general.*;
import java.io.*;
import java.util.*;
import suffixtree.*;
import suffixtree.UkkonenSuffixTree.SuffixNode;

/**
 * This class is an implementation of Baeza-Yates and Gonnet algorithm 
 * for (threshold) all-against-all approximate substring matching.
 * The algorithm is enchanced by calculating the DP table cells only in a 2*maxDiff+1 strip
 * around the main diagonal (the idea of Ukkonen)
 * @author marina
 *
 */
public class BYGU
{
	//pointers to the two Suffix trees roots of two input strings
	SuffixTreeNode _tree1;
	SuffixTreeNode _tree2;
	
	
	//the maximum size of the dynamic programming table - is set to twice minLength
	public  int _maxLength;	

	//used as an infinity=maxDiff+1 - we are not interested if the value of DP table exceeds maxDiff
	public  int INFINITY=0;
	
	char [] _seq1;
	char [] _seq2;
	
	int _length1;
	int _length2;
	
	int _maxDiff;
	int _minLength;	

	List _solutions=new LinkedList();
	
	//Dynamic programming table
	int [][] _table;
	
	//in order to produce comparable results, this variable is set to true,
	//so the algorithm produces the patterns starting and ending with a character match
	public boolean freeEnds=true;
	
	
	boolean _produceOutput=true;
	
	//This is an array where for each pair of nodes from tree1 and tree2, 
	//the last row and last column of the calculated DP table is saved in order to be reused when calculating
	//edit distance for child nodes.
	Object[][] _previousResults;
	
	//If all values in the last row/column of 2 parent nodes exceed threshold, there is no need to 
	//calculate an edit distance for child nodes. Since the table can be still used 
	//as previous result for parent1-child2 comparison, we save in this monitor only the true-false value -
	//to process the child nodes (true) or to abort (false).
	boolean [][] _monitor;
	
	//constants - indices of an array saved
	static final int ROW_HEADER=0;
	static final int COL_HEADER=1;
	static final int LAST_ROW=2;
	static final int LAST_COL=3;
	
	char _terminator1;
	char _terminator2;
	/**
	 * Constructor.
	 * @param tree1 - the first object SuffixTreeNode 
	 * - link to the root of suffix tree for the first string
	 * @param tree2 - the second object SuffixTreeNode 
	 * - link to the root of suffix tree for the second string
	 * Note, that the nodes of the input trees are labeled in a string depth order.
	 * @param seq1 - first string
	 * @param seq2 - second string
	 * @param maxDiff -  maximum allowed differences
	 * @param minLen - minimum length of interest
	 */	
		public BYGU(SuffixTreeNode tree1, SuffixTreeNode tree2,
				String seq1, String terminator1, String seq2, String terminator2,int maxDiff, int minLen)
		{
			_tree1=tree1;
			_tree2=tree2;
			_seq1=(seq1+terminator1).toCharArray();
			_length1=_seq1.length;
			
			_seq2=(seq2+terminator2).toCharArray();
			_length2=_seq2.length;
			_minLength=minLen;
			_maxDiff=maxDiff+1;
			_maxLength=Utils.min(2*_minLength, Utils.max(_length1, _length2));
			INFINITY=_maxDiff+1;	
			_terminator1=terminator1.charAt(0);
			_terminator2=terminator2.charAt(0);
		}
		
		/**
		 * This method allows to turn off the generation of output.
		 * This is because for producing the output corresponding to the suffix tree nodes that match,
		 * we need to traverse the suffix tree till we reach the leaves. 
		 * This is not part of the main hybrid DP algorithm of Baeza-Yates and Gonet, 
		 * and thus for performance experimentation, we provide the ability to specify that 
		 * we want to see running time without producing the result per se. 
		 *
		 * @param yesno
		 */
		public void withOutput(boolean yesno)
		{
			_produceOutput=yesno;
		}
		
		/**
		 * The main processing method.
		 * For each child of the suffix tree root (subtree) the DP table starts from the beginning and the edit distance is calculated 
		 * for child nodes in a depth first order, such that child node is always processed after its parent node.
		 */
		public void process()
		{
			List rootchildren1=_tree1.children;
			List rootchildren2=_tree2.children;
			for(int i=0;i<rootchildren1.size();i++)
			{
				SuffixTreeNode node1=(SuffixTreeNode)rootchildren1.get(i);				
				
				for(int j=0;j<rootchildren2.size();j++)
				{					
					SuffixTreeNode node2=(SuffixTreeNode)rootchildren2.get(j);					
					processSubtrees(node1,node2);
				}
			}
			
			Collections.sort(_solutions, new IntervalComparator());
			
		}

		
		/**
		 * The algorithm produces all solutions (non-maximal), which can be turned into maximal
		 * by calling this method. 
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
	 	 * Compares 2 sutrees, which start from the same character.
	 	 * @param root1 - link to a root child of the first tree
	 	 * @param root2 - link to a root child of the second tree
	 	 * The processing includes the following steps:
	 	 * -calculating the DP table for root1, root2 (fillTableForRoots)
	 	 * -calculating the DP table for root1 and each child and grandchild of root2(fillTableForRoot1ChildrenRoot2)
	 	 * -calculating the DP table for each child/grandchild of root1 and root2 (fillTableForChild1ChildrenRoot2)
	 	 * -calculating the DP table for each child/grandchild of root1 and each child/grandchild of root2 (fillTableForChild1Child2)
	 	 */
		private void processSubtrees(SuffixTreeNode root1, SuffixTreeNode root2)
		{
				
			if(freeEnds && _seq1[root1.labelStart]!=_seq2[root2.labelStart])
				return;
			
			_previousResults=new Object[root1.totalChildren][root2.totalChildren];
			_monitor=new boolean[root1.totalChildren][root2.totalChildren];
			
			fillTableForRoots(root1,root2);
			
			fillTableForRoot1ChildrenRoot2(root1,root2);
			
			List root1children=root1.children;
			if(root1children!=null && root1children.size()>0)
			{				
				for(int i=0;i<root1children.size();i++)
				{
					SuffixTreeNode child1=(SuffixTreeNode)root1children.get(i);
					fillTableForChild1ChildrenRoot2(child1,root2);
				}
			}			
			
		}
		
		private void fillTableForRoot1ChildrenRoot2(SuffixTreeNode root1, SuffixTreeNode subtree2)
		{
			//calculate DP table  for root1 against subtree2 and all children & grandchildren of subtree2			
			List subtree2children=subtree2.children;
			if(subtree2children!=null && subtree2.children.size()>0)
			{
				fillTableForRoot1Children2(root1,subtree2children);
			}
		}
		
		private void fillTableForChild1ChildrenRoot2(SuffixTreeNode curr1, SuffixTreeNode subtree2)
		{
			
			fillTableForChild1Root2(curr1,subtree2);
					
			List subtree2children=subtree2.children;
			if(subtree2children!=null&& subtree2.children.size()>0)
			{
				for(int i=0;i<subtree2.children.size();i++)
				{
					SuffixTreeNode curr2=(SuffixTreeNode)subtree2children.get(i);
					fillTableForChild1Child2(curr1,curr2);
				}
			}
		
			List curr1children=curr1.children;
			if(curr1children!=null && curr1children.size()>0)
			{
				for(int i=0;i<curr1children.size();i++)
				{
					SuffixTreeNode curr1child=(SuffixTreeNode)curr1children.get(i);
					fillTableForChild1ChildrenRoot2(curr1child,subtree2);
				}
			}		
			
		}
		
		private void fillTableForChild1Child2(SuffixTreeNode curr1, SuffixTreeNode curr2)
		{			
			// compute ONLY curr1 against curr2
			int prevI=curr1.parentOrderNumber;
			int currI=curr1.orderNumber;
			
			int prevJ=curr2.parentOrderNumber;			
			int currJ=curr2.orderNumber;			
		
			
			int[][] cornerVals=(int[][])_previousResults[prevI][prevJ];
			int[][] leftVals=(int[][])_previousResults[currI][prevJ];
			int[][] upperVals=(int[][])_previousResults[prevI][currJ];
			
			if(_monitor[prevI][prevJ])
			{
				_table=new int [_maxLength+2][_maxLength+2];
				int prevlastCol=cornerVals[COL_HEADER].length+1;
				int prevlastRow=cornerVals[ROW_HEADER].length+1;
				
				int lastCol=Utils.min(prevlastCol+1+curr2.labelEnd-curr2.labelStart,_table[0].length-1);
				int lastRow=Utils.min(prevlastRow+1+curr1.labelEnd-curr1.labelStart,_table.length-1);
				
				int[] prevrowheader=leftVals[ROW_HEADER];
				int[] prevcolheader=upperVals[COL_HEADER];
				int[] prevlastRowVals=upperVals[LAST_ROW];
				int[] prevlastColVals=leftVals[LAST_COL];
			
				//fill table for root2 against current child
				
				//fill prev last row as an initial row			
				for(int j=2,k=0;j<=prevlastRowVals.length+1;j++,k++)
				{
					
					_table[prevlastRow][j]=prevlastRowVals[k];
				}
				
				//fill prev last col as an initial col
				for(int i=2,k=0;i<=prevlastColVals.length+1;i++,k++)
				{
					
					_table[i][prevlastCol]=prevlastColVals[k];
				}		
			
				List allLeaves1=null;
				List allLeaves2=null;
				if(_produceOutput && lastCol-1>=_minLength && lastRow-1>=_minLength)
				{
					allLeaves1=getAllLeaves(curr1);
					allLeaves2=getAllLeaves(curr2);
				}
				
				//fill positions for curr2 into row 0				
				for(int j=2,k=0;j<=lastCol;j++,k++)
				{
					_table[0][j]=prevcolheader[k];
				}	
			
			
				//fill positions for curr1 into col 0
				for(int i=2,k=0;i<=lastRow;i++,k++)
				{
					_table[i][0]=prevrowheader[k];
				}			
			
				//calculate all cells of the dp table
				for(int i=prevlastRow+1,k=prevlastRow+1;i<=lastRow;i++,k++)
				{
					
					for(int j=Utils.max(prevlastCol+1,k-_maxDiff);j<=Utils.min(lastCol,k+_maxDiff);j++)
					{
						
						_table[i][j]=
						(Utils.min
								(
								_table[i-1][j-1]+
								edScore(_seq1[_table[i][0]],
										_seq2[_table[0][j]]),
								infinity(_table[i][j-1])+1,
								infinity(_table[i-1][j])+1
								)
						);	
						
						if(_produceOutput && i-1>=_minLength && j-1>=_minLength && _table[i][j]<=_maxDiff 
								&& _seq1[_table[i][0]]!=_terminator1 && _seq2[_table[0][j]]!=_terminator2)
							addToSolution(allLeaves1,allLeaves2,i-1,j-1);						
					}					
				}
			

			
				//put new rowheader, colheader, lastrow, lastcol into previousResults array
				int [][] tableToSave=new int[4][];
				
				int [] rowHeader=new int[lastRow-1];
				for(int i=2;i<=lastRow;i++)
					rowHeader[i-2]=_table[i][0];
			
				tableToSave[ROW_HEADER]=rowHeader;
			
				int [] colHeader=new int[lastCol-1];
				for(int j=2;j<=lastCol;j++)
					colHeader[j-2]=_table[0][j];
				tableToSave[COL_HEADER]=colHeader;
			
				int [] lastRowVals=new int[lastCol-1];
				int[] leftprevlastRowVals=leftVals[LAST_ROW];
			
				boolean continuecompare=false;
				for(int j=2;j<=prevlastCol;j++)
				{
					
					lastRowVals[j-2]=leftprevlastRowVals[j-2];
					if(!continuecompare && infinity(leftprevlastRowVals[j-2])<=_maxDiff )
						continuecompare=true;
				}
				for(int j=prevlastCol+1;j<=lastCol;j++)
				{
					lastRowVals[j-2]=_table[lastRow][j];
					if(!continuecompare && infinity(_table[lastRow][j])<=_maxDiff )
						continuecompare=true;
				}
				tableToSave[LAST_ROW]=lastRowVals;
			
				int [] lastColVals=new int[lastRow-1];
				int[] upperprevlastColVals=upperVals[LAST_COL];
				for(int i=2;i<=prevlastRow;i++)
				{
					lastColVals[i-2]=
						upperprevlastColVals[i-2];
					if(!continuecompare && infinity(upperprevlastColVals[i-2])<=_maxDiff )
						continuecompare=true;
				}
				for(int i=prevlastRow+1;i<=lastRow;i++)
				{
					lastColVals[i-2]=_table[i][lastCol];
					if(!continuecompare && infinity(_table[i][lastCol])<=_maxDiff )
						continuecompare=true;
				}
				tableToSave[LAST_COL]=lastColVals;
			
				_previousResults[curr1.orderNumber][curr2.orderNumber]=tableToSave;
				_monitor[curr1.orderNumber][curr2.orderNumber]=continuecompare;
			}
			
			List curr2children=curr2.children;
			if(curr2children!=null && curr2children.size()>0 )
			{
				for(int i=0;i<curr2children.size();i++)
				{
					SuffixTreeNode curr2child=(SuffixTreeNode)curr2children.get(i);
					fillTableForChild1Child2(curr1,curr2child);
				}
			}
		}
		
		private void fillTableForChild1Root2(SuffixTreeNode curr1,SuffixTreeNode root2)
		{
			//calculate only curr node1 against root2
			int prevJ=0;
			int prevI=curr1.parentOrderNumber;
			
			int[][] prevValues=(int[][])_previousResults[prevI][prevJ];
			if(_monitor[prevI][prevJ])
			{
				
				int[] prevrowheader=prevValues[ROW_HEADER];
				int[] prevcolheader=prevValues[COL_HEADER];
				int[] prevlastRowVals=prevValues[LAST_ROW];
				int[] prevlastColVals=prevValues[LAST_COL];
				
				//fill table for root2 against current child			
				int prevlastCol=prevcolheader.length+1;
				int prevlastRow=prevrowheader.length+1;
				int lastCol=prevlastCol;
				int lastRow=Utils.min(prevlastRow+1+curr1.labelEnd-curr1.labelStart,_table.length-1);
			
				_table=new int [Utils.min(_maxLength+2,lastRow+1)][Utils.min(_maxLength+2,lastCol+1)];
				
				//fill prev last row as an initial row			
				for(int j=2,k=0;j<=prevlastCol;j++,k++)
				{
					
					_table[prevlastRow][j]=prevlastRowVals[k];
				}
			
				//initialize DP table row from prevlastrow
				for(int i=prevlastRow;i<=lastRow;i++)
				{
					
					_table[i][1]=i;
				}			
			
				//fill positions for root2 into row 0				
				for(int j=2,k=0;j<=prevlastCol;j++,k++)
				{
					_table[0][j]=prevcolheader[k];
				}
						
				List allLeaves1=null;
				List allLeaves2=null;
				if(_produceOutput && lastCol-1>=_minLength && lastRow-1>=_minLength)
				{
					allLeaves1=getAllLeaves(curr1);
					allLeaves2=getAllLeaves(root2);
				}
			
				//fill new positions of the first string
				int pos=curr1.labelStart;
				for(int i=prevlastRow+1;i<=lastRow;i++,pos++)
				{
					_table[i][0]=pos;
				}			
			
				//calculate all cells of the dp table
				for(int i=prevlastRow+1,k=prevlastRow+1;i<=lastRow;i++,k++)
				{
					for(int j=Utils.max(2,k-_maxDiff);j<=Utils.min(lastCol,k+_maxDiff);j++)
					{
						_table[i][j]=
						(Utils.min
								(
								_table[i-1][j-1]+
								edScore(_seq1[_table[i][0]],
										_seq2[_table[0][j]]),
								infinity(_table[i][j-1])+1,
								infinity(_table[i-1][j])+1
								)
						);
						if(_produceOutput && i-1>=_minLength && j-1>=_minLength && _table[i][j]<=_maxDiff
								&& _seq1[_table[i][0]]!=_terminator1 && _seq2[_table[0][j]]!=_terminator2)
							addToSolution(allLeaves1,allLeaves2,i-1,j-1);
					}
				}
			
			//put new rowheader, colheader, lastrow, lastcol into previousResults array
			int [][] tableToSave=new int[4][];
			
				int [] rowHeader=new int[lastRow-1];
				for(int i=2;i<=prevlastRow;i++)
					rowHeader[i-2]=prevrowheader[i-2];
				for(int i=prevlastRow+1;i<=lastRow;i++)
					rowHeader[i-2]=_table[i][0];
				
				tableToSave[ROW_HEADER]=rowHeader;
				
				int [] colHeader=new int[lastCol-1];
				for(int j=2;j<=lastCol;j++)
					colHeader[j-2]=_table[0][j];
				tableToSave[COL_HEADER]=colHeader;
				
				boolean continuecompare=false;
				int [] lastRowVals=new int[lastCol-1];
				for(int j=2;j<=lastCol;j++)
				{
					if(!continuecompare &&infinity(_table[lastRow][j])<=_maxDiff )
						continuecompare=true;
					lastRowVals[j-2]=_table[lastRow][j];
				}
				tableToSave[LAST_ROW]=lastRowVals;
				
				int [] lastColVals=new int[lastRow-1];
				for(int i=2;i<=prevlastRow;i++)
				{
					if(!continuecompare && infinity(prevlastColVals[i-2])<=_maxDiff )
						continuecompare=true;
					lastColVals[i-2]=
						prevlastColVals[i-2];
				}
				for(int i=prevlastRow+1;i<=lastRow;i++)
				{
					if(!continuecompare && infinity(_table[i][lastCol])<=_maxDiff )
						continuecompare=true;
					lastColVals[i-2]=_table[i][lastCol];
				}
				tableToSave[LAST_COL]=lastColVals;
				
				_previousResults[curr1.orderNumber][0]=tableToSave;
				_monitor[curr1.orderNumber][0]=continuecompare;
			}
		}
	
		
		private void fillTableForRoot1Children2(SuffixTreeNode root1, List children2)
		{
			for(int c=0;c<children2.size();c++)
			{
				SuffixTreeNode child2=(SuffixTreeNode)children2.get(c);
				int prevJ=child2.parentOrderNumber;
				int prevI=0;
				
				int[][] prevValues=(int[][])_previousResults[prevI][prevJ];
				if(_monitor[0][prevJ])
				{
					int[] prevrowheader=prevValues[ROW_HEADER];
					int[] prevcolheader=prevValues[COL_HEADER];
					int[] prevlastRowVals=prevValues[LAST_ROW];
					int[] prevlastColVals=prevValues[LAST_COL];
					
					//fill table for root1 against current child
					_table=new int [_maxLength+2][_maxLength+2];
					int prevlastCol=prevcolheader.length+1;
					int prevlastRow=prevrowheader.length+1;
					int lastCol=Utils.min(prevlastCol+1+child2.labelEnd-child2.labelStart,_table[0].length-1);
					int lastRow=prevlastRow;
					
					//fill prev last col as an initial col
					for(int i=2,k=0;i<=prevlastRow;i++,k++)
					{
						
						_table[i][prevlastCol]=prevlastColVals[k];
					}
				
					//initialize DP table column from prevlastcol
					for(int j=prevlastCol;j<=lastCol;j++)
					{
						
						_table[1][j]=j;
					}
				
				
					//fill positions for root1 into col 0				
					for(int i=2,k=0;i<=prevlastRow;i++,k++)
					{
						_table[i][0]=prevrowheader[k];
					}
								
					List allLeaves1=null;
					List allLeaves2=null;
					if(_produceOutput && lastCol-1>=_minLength && lastRow-1>=_minLength)
					{
						allLeaves1=getAllLeaves(root1);
						allLeaves2=getAllLeaves(child2);
					}
					
					//fill new positions of the second string
					int pos=child2.labelStart;
					for(int j=prevlastCol+1;j<=lastCol;j++,pos++)
					{
						_table[0][j]=pos;
					}
				
				
				
					//calculate all cells of the dp table
					for(int i=2,k=2;i<=lastRow;i++,k++)
					{
						for(int j=Utils.max(prevlastCol+1,k-_maxDiff);j<=Utils.min(lastCol,k+_maxDiff+1);j++)
						{
							
							_table[i][j]=
							(Utils.min
									(
									_table[i-1][j-1]+
									edScore(_seq1[_table[i][0]],
											_seq2[_table[0][j]]),
									infinity(_table[i][j-1])+1,
									infinity(_table[i-1][j])+1
									)
							);
							
							if(_produceOutput && i-1>=_minLength && j-1>=_minLength && _table[i][j]<=_maxDiff
									&& _seq1[_table[i][0]]!=_terminator1 && _seq2[_table[0][j]]!=_terminator2)
								addToSolution(allLeaves1,allLeaves2,i-1,j-1);
						}
					}
				

					//put new rowheader, colheader, lastrow, lastcol into previousResults array
					int [][] tableToSave=new int[4][];
					
					int [] rowHeader=new int[lastRow-1];
					for(int i=2;i<=lastRow;i++)
						rowHeader[i-2]=_table[i][0];
					
					tableToSave[ROW_HEADER]=rowHeader;
					
					int [] colHeader=new int[lastCol-1];
					for(int j=2;j<=prevlastCol;j++)
						colHeader[j-2]=prevcolheader[j-2];
					for(int j=prevlastCol+1;j<=lastCol;j++)
						colHeader[j-2]=_table[0][j];
					
					tableToSave[COL_HEADER]=colHeader;
					
					boolean continuecompare=false;
					int [] lastRowVals=new int[lastCol-1];
					for(int j=2;j<=prevlastCol;j++)
					{
						lastRowVals[j-2]=
							prevlastRowVals[j-2];
						if(!continuecompare && infinity(prevlastRowVals[j-2])<=_maxDiff)
							continuecompare=true;
					}
					for(int j=prevlastCol+1;j<=lastCol;j++)
					{
						lastRowVals[j-2]=_table[lastRow][j];
						if(!continuecompare && infinity(_table[lastRow][j])<=_maxDiff)
							continuecompare=true;
					}	
					
					tableToSave[LAST_ROW]=lastRowVals;
					
					int [] lastColVals=new int[lastRow-1];
					for(int i=2;i<=lastRow;i++)
					{
						lastColVals[i-2]=_table[i][lastCol];	
						if(!continuecompare && infinity(_table[i][lastCol])<=_maxDiff)
							continuecompare=true;
					}	
					tableToSave[LAST_COL]=lastColVals;
			
					_previousResults[0][child2.orderNumber]=tableToSave;
					_monitor[0][child2.orderNumber]=continuecompare;
				}
				
				List grandchildren=child2.children;
				if(grandchildren!=null && grandchildren.size()>0)
				{
					fillTableForRoot1Children2(root1,grandchildren);
				}
			}
		
		}
		
		
		private List getAllLeaves(SuffixTreeNode node)
		{
			List ret=new ArrayList();
			appendChildLeaves(ret,node);
			return ret;
		}

		private int infinity(int val)
		{
			if(val==0)
				return INFINITY;
			return val;
		}
		
		private void appendChildLeaves(List res, SuffixTreeNode parent)
		{
			if(parent.children==null || parent.children.size()==0)
			{
				res.add(new Integer(parent.leafNumber));
				return;
			}
			
			List childNodes=parent.children;
			for(int i=0;i<childNodes.size();i++)
			{
				SuffixTreeNode child=(SuffixTreeNode)childNodes.get(i);
				appendChildLeaves(res,child);
			}
		}
		
		private void fillTableForRoots(SuffixTreeNode root1,SuffixTreeNode root2)
		{
			_table=new int [_maxLength+2][_maxLength+2];
			int lastRow=Utils.min(2+(root1.labelEnd-root1.labelStart),_table.length-1);
			int lastCol=Utils.min(2+(root2.labelEnd-root2.labelStart),_table[0].length-1);
			
			//fill positions for root1 into col 0			
			int pos=root1.labelStart;
			for(int i=2;i<=lastRow;i++,pos++)
			{
				_table[i][0]=pos;
			}
			
			//fill positions for root2 into row 0			
			pos=root2.labelStart;
			for(int j=2;j<=lastCol;j++,pos++)
			{
				_table[0][j]=pos;
			}
			
			List allLeaves1=null;
			List allLeaves2=null;
			if(_produceOutput && lastCol-1>=_minLength && lastRow-1>=_minLength)
			{
				allLeaves1=getAllLeaves(root1);
				allLeaves2=getAllLeaves(root2);
			}
			
			//initialize DP table
			for(int i=1;i<=lastRow;i++)
			{
				_table[i][1]=i;
			}
			for(int j=1;j<=lastCol;j++)
			{
				_table[1][j]=j;
			}
			
			
			//calculate all cells of the dp table around the main diagonal			
			for(int i=2,k=2;i<=lastRow;i++,k++)
			{
				for(int j=Utils.max(2,k-_maxDiff);j<=Utils.min(lastCol,k+_maxDiff);j++)
				{
					_table[i][j]=
					(Utils.min
							(
							_table[i-1][j-1]+edScore(_seq1[_table[i][0]],_seq2[_table[0][j]]),
							infinity(_table[i][j-1])+1,
							infinity(_table[i-1][j])+1
							)
					);
					if(i-1>=_minLength && j-1>=_minLength && _table[i][j]<=_maxDiff
							&& _seq1[_table[i][0]]!=_terminator1 && _seq2[_table[0][j]]!=_terminator2)
						addToSolution(allLeaves1,allLeaves2,i-1,j-1);
				}
			}
			
			//put rowheader, colheader, lastrow, lastcol into previousResults array
			int [][] tableToSave=new int[4][];
			int [] rowHeader=new int[lastRow-1];
			for(int i=2;i<=lastRow;i++)
				rowHeader[i-2]=_table[i][0];
			tableToSave[ROW_HEADER]=rowHeader;
			
			int [] colHeader=new int[lastCol-1];
			for(int j=2;j<=lastCol;j++)
				colHeader[j-2]=_table[0][j];
			tableToSave[COL_HEADER]=colHeader;
			
			boolean continuecompare=false;
			int [] lastRowVals=new int[lastCol-1];
			for(int j=2;j<=lastCol;j++)
			{
				if(!continuecompare && infinity(_table[lastRow][j])<=_maxDiff)
					continuecompare=true;
				lastRowVals[j-2]=_table[lastRow][j];
			}
			tableToSave[LAST_ROW]=lastRowVals;
			
			int [] lastColVals=new int[lastRow-1];
			for(int i=2;i<=lastRow;i++)
			{
				if(!continuecompare && infinity(_table[i][lastCol])<=_maxDiff)
					continuecompare=true;
				lastColVals[i-2]=_table[i][lastCol];	
			}
						
			tableToSave[LAST_COL]=lastColVals;
			
			_previousResults[0][0]=tableToSave;
			_monitor[0][0]=continuecompare;
			
		}
		
		private void addToSolution(List positions1, List positions2, int len1, int len2)
		{			
			for(int i=0;i<positions1.size();i++)
			{
				int startI=((Integer)positions1.get(i)).intValue();
				int endI=startI+len1-1;
				for(int j=0;j<positions2.size();j++)
				{
					
					int startJ=((Integer)positions2.get(j)).intValue();
					int endJ=startJ+len2-1;
					
					if((freeEnds && _seq1[startI]==_seq2[startJ]&& _seq1[endI]==_seq2[endJ]
					        && _table[len1][len2]<=Utils.min(infinity(_table[len1+1][len2]),infinity(_table[len1][len2+1]))+1)
					        ||!freeEnds)
					{
						IndexPair start=new IndexPair(startI,startJ);
						IndexPair end=new IndexPair(endI,endJ);
						Interval interval=new Interval(start,end);
						_solutions.add(interval);							
					}
				}
			}
		}
		
		public List getSolutions()
		{			
			return _solutions;
		}
		
		private static int edScore(char first,char second)
		{
			if(first==second)
				return 0;
			return 1;
		}		
		
		public static void main(String [] args)
		{
			String file1=null;
			String file2=null;
			String terminator1=null;
			String terminator2=null;
			int maxDiff=0;
			int minLen=0;		
			boolean withOutput=true;
			boolean maximalOutput=false;
			boolean printStdOutput=false;
			boolean treesFromFile=true;
			String suffixTreeFileName1=null;
			String suffixTreeFileName2=null;	
			
			try
			{
				file1=args[0];
				file2=args[1];
				terminator1=args[2];
				terminator2=args[3];
				
				if(terminator1.length()>1 || terminator2.length()>1 || terminator1.equals(terminator2))
				{
					System.out.println("The termination chars should be 2 different characters not containing in the main alphabet.");
					System.exit(1);
				}
				minLen=Integer.parseInt(args[4]);
				maxDiff=Integer.parseInt(args[5]);
				
				int yesno=Integer.parseInt(args[6]);
				
				if(yesno==0)
					withOutput=false;

				yesno=Integer.parseInt(args[7]);
				
				if(yesno==1)
					maximalOutput=true;
				
				yesno=Integer.parseInt(args[8]);
				
				if(yesno==1)
					printStdOutput=true;
				
				yesno=Integer.parseInt(args[9]);
				
				if(yesno==0)
					treesFromFile=false;
				
				if(treesFromFile)
				{
					suffixTreeFileName1=args[10];
					suffixTreeFileName2=args[11];	
				}
			}
			catch (Exception e)
			{
				System.out.println("Usage: \n" + 
						"java -Xmx512M -Xms512m -classpath classes algorithms.BYGU \\ \n" +
		                		"<filename1> <filename2> \\ \n" +
		                		"<terminationchar1> <terminationchar2> \\ \n" +
		                		"<minLength> <maxDifferences> <produceOutput: 1 | 0>\\ \n" +
		                		"<maximalOutput: 1 | 0> <printStdOutput: 1 | 0> <readTreesFromFiles: 1 | 0>\\ \n" +
		                		" if readTreesFromFiles <suffixtreefilename1> <suffixtreefilename2>" );					

					System.exit(1);
			}
			
			SequenceFileReader reader =new SequenceFileReader(file1);
			String seq1=reader.getSequence();
			reader =new SequenceFileReader(file2);
			String seq2=reader.getSequence();			

			SuffixTreeNode tree1=null;
			SuffixTreeNode tree2=null;
			if(treesFromFile)
			{
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
			}
			else
			{
				UkkonenSuffixTree oldtree1=new UkkonenSuffixTree();
				oldtree1.setTerminationChar(terminator1.charAt(0));
				oldtree1.addSequence(seq1+terminator1,file1,true);		

				SuffixNode root1=oldtree1.getRoot();
				SuffixTreeWriter stwriter=new SuffixTreeWriter();
				tree1=stwriter.convertTree(root1);
				
				
				UkkonenSuffixTree oldtree2=new UkkonenSuffixTree();
				oldtree1.setTerminationChar(terminator2.charAt(0));
				oldtree1.addSequence(seq2+terminator2,file2,true);		

				SuffixNode root2=oldtree2.getRoot();
				stwriter=new SuffixTreeWriter();
				tree2=stwriter.convertTree(root2);
			}
				
			
			
			
			System.out.println("BYGU algorithm");
			long start=System.currentTimeMillis();
			BYGU algorithm=new BYGU(tree1,tree2,
						seq1,terminator1,seq2,terminator2,maxDiff,minLen);

			algorithm.withOutput(withOutput);
			algorithm.process();			

			long howlong=System.currentTimeMillis()-start;
			System.out.println("Processed in "+howlong+" ms");
			if(withOutput)
			{
				List solutions=algorithm.getSolutions();
				System.out.println("Produced output size="+solutions.size());
				
				if(maximalOutput)
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
					if(printStdOutput)
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
					if(printStdOutput)
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

}

