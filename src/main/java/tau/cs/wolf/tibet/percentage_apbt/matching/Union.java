package tau.cs.wolf.tibet.percentage_apbt.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import tau.cs.wolf.tibet.percentage_apbt.data.IndexPair;
import tau.cs.wolf.tibet.percentage_apbt.data.Interval;
import tau.cs.wolf.tibet.percentage_apbt.data.MatchResult;
import tau.cs.wolf.tibet.percentage_apbt.main.args.Args;
import tau.cs.wolf.tibet.percentage_apbt.misc.BaseModule;
import tau.cs.wolf.tibet.percentage_apbt.misc.PropsBuilder.Props;

public class Union extends BaseModule {

	public Union(Props props, Args args) {
		super(props, args);
	}

	public List<MatchResult> uniteMatches(List<MatchResult> matches) {
		List<IndexPair> pairsToUnite = new ArrayList<IndexPair>();
		for (int i = 0; i < matches.size(); i++) {
			for (int j = i + 1; j < matches.size(); j++) {
				MatchPair pair = MatchPair.of(matches.get(i), matches.get(j));

				if (checkUnion(pair)) {
					pairsToUnite.add(new IndexPair(i, j));
				}

			}
		}

		List<MatchResult> unitedMatches = unitePairs(matches, pairsToUnite);
		for (MatchResult unitedMatch : unitedMatches) {
			int tScore = -1
					* (unitedMatch.workInterval.getEnd().getIndex1() - unitedMatch.workInterval.getStart().getIndex1());
			unitedMatch.score = tScore;
		}

		return unitedMatches;
	}

	private boolean checkUnion(MatchPair pair) {
		if (!checkSegment(pair.left.workInterval.getSpan1(), pair.right.workInterval.getSpan1())) {
			return false;
		}
		if (!checkSegment(pair.left.workInterval.getSpan2(), pair.right.workInterval.getSpan2())) {
			return false;
		}
		return true;
	}

	private boolean checkSegment(IndexPair spanA, IndexPair spanB) {
		if (spanB.getIndex1() < spanA.getIndex1()) {
			return checkSegment(spanB, spanA);
		}

		if (spanA.getIndex2() < spanB.getIndex1()) {
			return (spanB.getIndex1() - spanA.getIndex2() < args.getMinDistanceUnion());
		}
		return true;
	}

	private List<MatchResult> unitePairs(List<MatchResult> matches, List<IndexPair> pairs) {

		Map<Integer, Integer> elementToGroup = new TreeMap<Integer, Integer>();
		Map<Integer, List<Integer>> groupToElements = new TreeMap<Integer, List<Integer>>();

		List<MatchResult> unitesMatches = new ArrayList<MatchResult>();

		for (int i = 0; i < matches.size(); i++) {
			elementToGroup.put(i, i);

			List<Integer> initElements = new ArrayList<>();
			initElements.add(i);
			groupToElements.put(i, initElements);
		}

		for (IndexPair pair : pairs) {
			int firstElement = pair.getIndex1();
			int secondElement = pair.getIndex2();

			int firstGroup = elementToGroup.get(firstElement);
			int secondGroup = elementToGroup.get(secondElement);

			if (firstGroup != secondGroup) {
				int newGroupName = Math.min(firstGroup, secondGroup);
				int deletedGroupName = Math.max(firstGroup, secondGroup);
				List<Integer> newGroupElements = groupToElements.get(newGroupName);
				newGroupElements.addAll(groupToElements.get(deletedGroupName));
				for (int element : newGroupElements) {
					elementToGroup.put(element, newGroupName);
				}
				groupToElements.remove(deletedGroupName);
			}
		}

		for (List<Integer> elements : groupToElements.values()) {
			MatchResult union = null;
			for (int element : elements) {
				if (union == null) {
					union = new MatchResult(matches.get(element));
					continue;
				}
				union = uniteMatches(union, matches.get(element));
			}
			unitesMatches.add(union);

		}
		return unitesMatches;
	}

	private MatchResult uniteMatches(MatchResult m1, MatchResult m2) {
		IndexPair unitedSpan1 = closSpans(m1.workInterval.getSpan1(), m2.workInterval.getSpan1());
		IndexPair unitedSpan2 = closSpans(m1.workInterval.getSpan2(), m2.workInterval.getSpan2());
		return new MatchResult(Interval.newIntervalBySpans(unitedSpan1, unitedSpan2), -1);
	}

	private IndexPair closSpans(IndexPair spanA, IndexPair spanB) {
		int start = Math.min(spanA.getIndex1(), spanB.getIndex1());
		int end = Math.max(spanA.getIndex2(), spanB.getIndex2());
		return new IndexPair(start, end);
	}

}
