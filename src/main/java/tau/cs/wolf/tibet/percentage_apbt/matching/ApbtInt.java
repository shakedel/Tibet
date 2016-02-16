
package tau.cs.wolf.tibet.percentage_apbt.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tau.cs.wolf.tibet.percentage_apbt.data.IndexPair;
import tau.cs.wolf.tibet.percentage_apbt.data.Interval;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

/**
 * This class implements the APBT algorithm for finding in two strings, all the
 * pairs of common approximate patterns, which satisfy the following criteria:
 * the length of both patterns in a pair is at least minLength, and the edit
 * distance between patterns (in a pair) is at most maxDiff. Max length of
 * patterns is set to 300 characters. If patterns of length greater than 300 are
 * expected, then they can be obtained either by appropriate post-processing or
 * by re-runing this program with a bigger value for MAX_LENGTH.
 *
 * @author marina
 */

public class ApbtInt implements Apbt<int[]> {

	Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * This constant defines the size of a chunk of the matching matrix to be
	 * processed simultaneously. This speeds up the calculation by decreasing
	 * the memory used. The simultaneously processed part of a matrix is of
	 * size: CHUNK_SIZE*_seq1.length, where _seq1.length is the length of the
	 * first string.
	 */
	private ProcessType processType;
	private int chunkSize;
	private int maxLength;
	private SortedSet<Interval> _solutions = new TreeSet<Interval>();
	private int maxDiff;
	private int maxDiffPlus1;
	private int minLength;

	private boolean[][] matrix;
	private int[] seq1;
	private int[] seq2;
	private int[][][] state;

	@Override 
	public void setup(int[] seq1, int[] seq2, ProcessType processBy, Args args, Props props) {
		if (args.getMinLength() > props.getMaxMatchLength()) {
			throw new IllegalStateException("minimum match legth exceeds maximum alowed match length property");
		}
		
		switch (processBy) {
			case ROW:
				this.seq1 = seq1;
				this.seq2 = seq2;
				break;
			case COL:
				this.seq1 = seq2;
				this.seq2 = seq1;
				break;
			default: throw new IllegalArgumentException("Unknown "+ProcessType.class.getSimpleName()+": "+this.processType);
		}
		this.processType = processBy;

		this.minLength = args.getMinLength();
		this.maxDiff = args.getMaxError();
		this.maxDiffPlus1 = args.getMaxError()+1;

		this.maxLength = props.getMaxMatchLength();
		this.chunkSize = props.getChunkSize();
	};

	
	/********************************************************************************************
	 * PUBLIC
	 ********************************************************************************************/

	@Override
	public void run() {

		this.matrix = new boolean[this.seq1.length][];
		
		int numChunks = (int) Math.ceil(((double) this.seq2.length) / this.chunkSize);
		
		for (int chunkIdx = 0; chunkIdx < numChunks; chunkIdx++) {
			int startJ = chunkIdx * this.chunkSize;
			logger.debug(String.format("processing chunk %d/%d",  chunkIdx+1, numChunks));
			process(startJ);
		}
	}

	/**
	 * 
	 * @return sorted List of intervals in a two-dimensional space. The Interval
	 *         contains the coordinates of the start (start position in string1,
	 *         start position in string 2) and of the end (end position in
	 *         string1, end position in string 2).
	 */
	public List<Interval> getSolutions() {
		return new ArrayList<Interval>(_solutions);
	}

	/**
	 * We try to expand paths to maximal length, but some of them may still be
	 * non-maximal. This method is called in order to output only maximal
	 * solurtions.
	 * 
	 * @return
	 */
	public List<Interval> getMaximalSolutions() {
		List<Interval> solutions = new ArrayList<Interval>(_solutions);
		List<Interval> ret = new LinkedList<Interval>();
		if (solutions.size() <= 1) {
			return solutions;
		}

		int maxI = 0;

		Interval first = (Interval) solutions.get(0);
		maxI = first.getEnd().getIndex1();

		List<Interval> temp = new LinkedList<Interval>();
		for (int i = 0; i < solutions.size(); i++) {
			Interval curr = (Interval) solutions.get(i);
			if (curr.getStart().getIndex1() <= maxI - this.minLength + 1) {
				temp.add(curr);
				maxI = Math.max(maxI, curr.getEnd().getIndex1());
				if (i == solutions.size() - 1)
					createMaximalSolutions(temp, ret);

			} else {
				createMaximalSolutions(temp, ret);
				temp = new LinkedList<Interval>();
				temp.add(curr);
				maxI = curr.getEnd().getIndex1();

				if (i == solutions.size() - 1)
					createMaximalSolutions(temp, ret);
			}
		}

		return ret;

	}
	/********************************************************************************************
	 * PRIVATE
	 ********************************************************************************************/

	/**
	 * This function processes the parts of the matrix starting from particular
	 * position in the second string.
	 * 
	 * @param startJ
	 *            - start position in a second string
	 */

	private void process(int startJ) {

		if (startJ > this.seq2.length - this.minLength) {
			return;
		}
		initializeMatrix(startJ, startJ + this.chunkSize + this.maxLength);

		for (int i = 0; i <= this.seq1.length - this.minLength; i++) {

			for (int j = 0; j <= Math.min(this.chunkSize-1, this.seq2.length - this.minLength); j++) {

				if (this.matrix[i][j]) {
					createPaths(i, j, startJ);
				}
			}
			// reset the row of state array to use with the next added (i+this.maxLength)-th row:
			this.state[(i) % this.maxLength] = new int[Math.min(this.chunkSize + this.maxLength, this.seq2.length)][];

		}
	}

	/**
	 * Initializes a new path of ML=1, EN=0, starting from current true cell of
	 * the matrix
	 */
	private void createPaths(int startI, int startJ, int shiftJ) {
		continuePath(startI, startJ, startI, startJ, 1, 0, shiftJ, true);
	}

	/**
	 * The main recursion - expanding of the path. Stop conditions: the path
	 * cannot be expanded without an additional error, OR a better path has been
	 * collected ending at point (currI, currJ)
	 */
	
	private void continuePath(int startI, int startJ, int currI, int currJ, int currlen, int currdiff, int shiftJ,
			boolean continueFurther) {
		if (currlen >= this.maxLength)
			return;
	
		if (continueFurther) {
			if (currlen > 1) {
				int[] instate = this.state[currI % this.maxLength][currJ];
	
				if (instate == null) {
					instate = new int[2];
					if (currI - startI <= currJ - startJ) {
						instate[0] = currdiff;
						instate[1] = this.maxDiffPlus1;
					} else {
						instate[1] = currdiff;
						instate[0] = this.maxDiffPlus1;
					}
					this.state[currI % this.maxLength][currJ] = instate;
				} else { // check what is the best (minimum diff) checked for this point
					if (currI - startI <= currJ - startJ) {
						if (instate[0] <= currdiff) {
							return;
						} else {
							instate[0] = currdiff;
						}
	
					} else {
						if (instate[1] <= currdiff) {
							return;
						} else {
							instate[1] = currdiff;
						}
					}
	
					this.state[currI % this.maxLength][currJ] = instate;
	
				}
	
			}
	
			// define bounds of the target square
			int LT_I = Math.min(currI + 1, this.seq1.length - 1);
			int LT_J = Math.min(currJ + 1, this.seq2.length - 1);
	
			int RT_J = Math.min(LT_J + this.maxDiff + 1 - currdiff, this.seq2.length);
			int LB_I = Math.min(LT_I + this.maxDiff + 1 - currdiff, this.seq1.length);
	
			int RB_J = RT_J;
	
			int currJBound = RB_J;
			int currIBound = LB_I;
	
			// explore the main diagonal
			boolean stop = false;
	
			// eladsh
//			for (int i = currI + 1, j = currJ + 1; i < Math.min(currI + this.maxDiff + 2 - currdiff, this.seq1.length)
//					&& j < Math.min(currJ + this.maxDiff + 2 - currdiff, this.seq2.length) && !stop; i++, j++) {
			for (int i = currI + 1, j = currJ + 1; i < Math.min(currI + this.maxDiff + 2 - currdiff, this.seq1.length)
					&& j < Math.min(currJ + this.maxDiff + 2 - currdiff, this.matrix[i].length) && !stop; i++, j++) {
				if (this.matrix[i][j]) {
	
					continuePath(startI, startJ, i, j, Math.min(i - startI, j - startJ) + 1,
							Math.max(i - currI, j - currJ) - 1 + currdiff, shiftJ, true);
					stop = true;
					currJBound = j;
					currIBound = i;
				}
			}
	
			for (int i = currIBound + 1; i < LB_I; i++) {
				if (this.matrix[i][currJBound]) {
					continuePath(startI, startJ, i, currJBound, Math.min(i - startI, currJBound - startJ) + 1,
							Math.max(i - currI, currJBound - currJ) - 1 + currdiff, shiftJ, false);
				}
			}
	
			for (int j = currJBound + 1; j < RT_J; j++) {
				if (this.matrix[currIBound][j]) {
					continuePath(startI, startJ, currIBound, j, Math.min(currIBound - startI, j - startJ) + 1,
							Math.max(currIBound - currI, j - currJ) - 1 + currdiff, shiftJ, false);
				}
			}
	
			// add an edge from next k upper and lower diagonals
			for (int k = 1; k < this.maxDiff + 1 - currdiff; k++) {
				// lower diagonals
				stop = false;
				for (int i = LT_I + k, j = LT_J; i < LB_I && j < currJBound && !stop; i++, j++) {
					if (this.matrix[i][j]) {
	
						continuePath(startI, startJ, i, j, Math.min(i - startI, j - startJ) + 1,
								Math.max(i - currI, j - currJ) - 1 + currdiff, shiftJ, true);
						if (j < currJBound) {
							currJBound = j;
							for (int m = i + 1; m < LB_I; m++) {
								if (this.matrix[m][currJBound])
									continuePath(startI, startJ, m, currJBound,
											Math.min(m - startI, currJBound - startJ) + 1,
											Math.max(m - currI, currJBound - currJ) - 1 + currdiff, shiftJ, false);
							}
	
						}
						stop = true;
					}
				}
	
				// upper diagonals
				stop = false;
				for (int i = LT_I, j = LT_J + k; i < currIBound && j < RT_J && !stop; i++, j++) {
					if (this.matrix[i][j]) {
	
						continuePath(startI, startJ, i, j, Math.min(i - startI, j - startJ) + 1,
								Math.max(i - currI, j - currJ) - 1 + currdiff, shiftJ, true);
						if (i < currIBound) {
							currIBound = i;
							for (int m = j + 1; m < RT_J; m++) {
	
								if (this.matrix[currIBound][m]) {
									continuePath(startI, startJ, currIBound, m,
											Math.min(currIBound - startI, m - startJ) + 1,
											Math.max(currIBound - currI, m - currJ) - 1 + currdiff, shiftJ, false);
								}
							}
	
						}
	
						stop = true;
					}
				}
			}
		}
	
		// the maximal path can not continue. Check if this is a solution
		if (currlen >= this.minLength) {
			addToSolutions(startI, startJ + shiftJ, currI, currJ + shiftJ);
		}
	
	}

	private void initializeMatrix(int from, int to) {
		Map<Integer, boolean[]> charPositions = new HashMap<Integer, boolean[]>(100);
		for (int j = from; j < Math.min(to, this.seq2.length); j++) {
			Integer curr = this.seq2[j];
			boolean[] row = (boolean[]) charPositions.get(curr);
			if (row == null) {
				row = new boolean[Math.min(this.chunkSize + this.maxLength, this.seq2.length)];
				charPositions.put(curr, row);
			}
			row[j - from] = true;
		}

		for (int i = 0; i < this.seq1.length; i++) {
			boolean[] row = charPositions.get(new Integer(this.seq1[i]));
			if (row != null) {
				this.matrix[i] = row;
			} else {
				this.matrix[i] = new boolean[this.seq2.length];
			}
		}

		this.state = new int[this.maxLength][Math.min(this.chunkSize + this.maxLength, this.seq2.length)][];

	}

	private void addToSolutions(int startI, int startJ, int currI, int currJ) {

		IndexPair start = null;
		IndexPair end = null;
		switch (this.processType) {
			case ROW:
				start = new IndexPair(startI, startJ);
				end = new IndexPair(currI, currJ);
				break;
			case COL :
				start = new IndexPair(startJ, startI);
				end = new IndexPair(currJ, currI);
				break;
			default: throw new IllegalArgumentException("Unknown "+ProcessType.class.getSimpleName()+": "+this.processType);
		}

		Interval interval = Interval.newIntervalByStartEnd(start, end);
		_solutions.add(interval);
	}

	private void createMaximalSolutions(List<Interval> overlappings, List<Interval> res) {
		if (overlappings.size() == 1) {
			res.add(overlappings.get(0));
			return;
		}
		for (int i = 0; i < overlappings.size(); i++) {
			Interval curr1 = (Interval) overlappings.get(i);
			for (int j = 0; j < overlappings.size(); j++) {
				if (i != j) {
					Interval curr2 = (Interval) overlappings.get(j);
					if (curr1 != null && curr2 != null) {
						if (curr2.getStart().getIndex1() >= curr1.getStart().getIndex1()
								&& curr2.getStart().getIndex2() >= curr1.getStart().getIndex2()
								&& curr2.getEnd().getIndex1() <= curr1.getEnd().getIndex1()
								&& curr2.getEnd().getIndex2() <= curr1.getEnd().getIndex2())
							overlappings.set(j, null);
					}
				}
			}
		}

		for (int i = 0; i < overlappings.size(); i++) {
			Interval curr = (Interval) overlappings.get(i);
			if (curr != null)
				res.add(curr);
		}
	}

}
