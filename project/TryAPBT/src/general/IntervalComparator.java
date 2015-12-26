
package general;
import java.util.*;

/**

 * 
 *
 * This class serves as a Comparator in order to order the list of the solutions intervals.
 * The order is by start position of the interval of the first substring.
 * If they are equal, then the order is defined by the start position in the second substring
 */

public class IntervalComparator implements Comparator
{
	public int compare(Object obj1, Object obj2)
	{
		Interval int1=(Interval)obj1;
		Interval int2=(Interval)obj2;
		
		IndexPair start1=int1.getStart();
		IndexPair start2=int2.getStart();
		
		if(start1.getIndex1()>start2.getIndex1())
			return 1;
		if(start1.getIndex1()<start2.getIndex1())
			return -1;
		
		return start1.getIndex2()-start2.getIndex2();
		
	}
}
