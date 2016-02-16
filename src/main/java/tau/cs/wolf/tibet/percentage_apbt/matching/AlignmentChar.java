package tau.cs.wolf.tibet.percentage_apbt.matching;

import java.util.ArrayList;
import java.util.List;

import tau.cs.wolf.tibet.percentage_apbt.data.IndexPair;
import tau.cs.wolf.tibet.percentage_apbt.data.Interval;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.misc.BaseModule;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public class AlignmentChar extends BaseModule {
	
	public AlignmentChar(Props props, Args args) {
		super(props, args);
	}

	public List<MatchResult> alignMatches(List<MatchResult> matches, String t1, String t2) {
		List<MatchResult> res = new ArrayList<MatchResult>(matches.size());
		
		for (MatchResult unitedMatch: matches) {
			IndexPair paddedSpan1 = padSpan(unitedMatch.workInterval.getSpan1());
			IndexPair paddedSpan2 = padSpan(unitedMatch.workInterval.getSpan2());

			int startOne = Math.max(0, paddedSpan1.getIndex1());
			int endOne = Math.min(paddedSpan1.getIndex2(), t1.length()-1);
			int startTwo = Math.max(0, paddedSpan2.getIndex1());
			int endTwo = Math.min(paddedSpan2.getIndex2() ,t2.length()-1);

			MatchResult wateredUnitedMatch = water(t1.substring(startOne, endOne), t2.substring(startTwo, endTwo));
			wateredUnitedMatch.workInterval.shiftSpans(startOne, startTwo);
			res.add(wateredUnitedMatch);
		}
		return res;
	}

	public IndexPair padSpan(IndexPair span) {
		int spanLen = span.getIndex2() - span.getIndex1() +1;
		int newStart = (int) Math.floor(span.getIndex1() - spanLen * args.getLocalAlignPadRatio());
		int newEnd = (int) Math.ceil(span.getIndex2() + spanLen * args.getLocalAlignPadRatio());
		return new IndexPair(newStart, newEnd);
	}

	private static enum PathTrace {
		UP, LEFT, DIAGONAL
	}

	public MatchResult water(String seq1, String seq2) {
		// m=len(seq1), n=len(seq2)
		// Generate DP table and traceback path pointer matrix

		int[][] scores = new int[seq1.length()+1][seq2.length()+1];  // the DP table               
		PathTrace[][] pointer = new PathTrace[seq1.length()+1][seq2.length()+1];// to store the traceback path

		int maxScore = 0;// initial maximum score in DP table

		int maxI = -1, maxJ = -1;

		// Calculate DP table and mark pointers
		for (int i=1; i<seq1.length()+1; i++) {
			for (int j=1; j<seq2.length()+1; j++) {
				int scoreDiagonal = scores[i-1][j-1] + matchScore(seq1.charAt(i-1), seq2.charAt(j-1));
				int scoreUp = scores[i-1][j] + props.getGapPenalty();
				int scoreLeft = scores[i][j-1] + props.getGapPenalty();
				int score = maxOf4(0,scoreLeft, scoreUp, scoreDiagonal);
				scores[i][j] = score;

				if (score==0) {
					// leave null
				} else if (score==scoreLeft) {
					pointer[i][j] = PathTrace.LEFT;
				} else if (score == scoreUp) {
					pointer[i][j] = PathTrace.UP;
				} else if (score == scoreDiagonal) {
					pointer[i][j] = PathTrace.DIAGONAL;
				} else {
					throw new IllegalStateException("Should not have reached this line");
				}

				if (scores[i][j] >= maxScore) {
					maxI = i;
					maxJ = j;
					maxScore = scores[i][j];
				}
			}
		}

		StringBuilder align1 = new StringBuilder(), align2 = new StringBuilder();// initial sequences
		int i = maxI, j = maxJ;// indices of path starting point

		// traceback, follow pointers
		while (pointer[i][j] != null) {
			switch (pointer[i][j]) {
			case DIAGONAL:
				align1.append(seq1.charAt(i-1));
				align2.append(seq2.charAt(j-1));
				i--;
				j--;
				break;
			case UP:
				align1.append('-');
				align2.append(seq2.charAt(j-1));
				j--;
				break;
			case LEFT:
				align1.append(seq1.charAt(i-1));
				align2.append('-');
				i--;
				break;
			default:
				throw new IllegalStateException("Unknown value: "+pointer[i][j]);
			}
		}
		return new MatchResult(Interval.newIntervalByStartEnd(new IndexPair(i, j), new IndexPair(maxI, maxJ)), maxScore);
	}

	private static int maxOf4(int n1, int n2, int n3, int n4) {
		return Math.max(Math.max(n1, n2), Math.max(n3, n4));
	}

	public int matchScore(char a, char b) {
		if (a == b) {
			return props.getMatchAward();
		} else if (a == '-' || b == '-') {
			return props.getGapPenalty();
		} else {
			return props.getMismatchPenalty();
		}
	}
}
