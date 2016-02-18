package tau.cs.wolf.tibet.percentage_apbt.matching;

import java.util.ArrayList;
import java.util.List;

import tau.cs.wolf.tibet.percentage_apbt.data.IndexPair;
import tau.cs.wolf.tibet.percentage_apbt.data.Interval;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.data.Slicable;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.misc.BaseModule;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public class AlignmentGeneric<R extends Slicable<R>> extends BaseModule {
	
	public AlignmentGeneric(Props props, Args args) {
		super(props, args);
	}

	public List<MatchResult> alignMatches(List<MatchResult> matches, R seq1, R seq2) {
		List<MatchResult> res = new ArrayList<MatchResult>(matches.size());
		
		for (MatchResult unitedMatch: matches) {
			IndexPair paddedSpan1 = padSpan(unitedMatch.workInterval.getSpan1());
			IndexPair paddedSpan2 = padSpan(unitedMatch.workInterval.getSpan2());

			int startOne = Math.max(0, paddedSpan1.getIndex1());
			int endOne = Math.min(paddedSpan1.getIndex2(), seq1.length()-1);
			int startTwo = Math.max(0, paddedSpan2.getIndex1());
			int endTwo = Math.min(paddedSpan2.getIndex2() ,seq2.length()-1);

			MatchResult wateredUnitedMatch = water(seq1.slice(startOne, endOne), seq2.slice(startTwo, endTwo));
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

	public MatchResult water(R seq1, R seq2) {
		// m=len(seq1), n=len(seq2)
		// Generate DP table and traceback path pointer matrix

		int[][] scores = new int[seq1.length()+1][seq2.length()+1];  // the DP table               
		PathTrace[][] pointer = new PathTrace[seq1.length()+1][seq2.length()+1];// to store the traceback path

		int maxScore = 0;// initial maximum score in DP table

		int maxI = -1, maxJ = -1;

		// Calculate DP table and mark pointers
		for (int i=1; i<seq1.length()+1; i++) {
			for (int j=1; j<seq2.length()+1; j++) {
				int scoreDiagonal = scores[i-1][j-1] + matchScore(seq1.compare(i-1, seq2, j-1));
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

		int i = maxI, j = maxJ;// indices of path starting point

		// traceback, follow pointers
		while (pointer[i][j] != null) {
			switch (pointer[i][j]) {
			case DIAGONAL:
				i--;
				j--;
				break;
			case UP:
				j--;
				break;
			case LEFT:
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

	public int matchScore(boolean matches) {
		return matches ? props.getMatchAward() : props.getMismatchPenalty();
	}
}