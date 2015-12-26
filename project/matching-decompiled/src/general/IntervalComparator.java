package general;

import general.IndexPair;
import general.Interval;
import java.util.Comparator;

public class IntervalComparator implements Comparator {

   public int compare(Object obj1, Object obj2) {
      Interval int1 = (Interval)obj1;
      Interval int2 = (Interval)obj2;
      IndexPair start1 = int1.getStart();
      IndexPair start2 = int2.getStart();
      return start1.getIndex1() > start2.getIndex1()?1:(start1.getIndex1() < start2.getIndex1()?-1:start1.getIndex2() - start2.getIndex2());
   }
}
