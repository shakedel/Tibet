package general;



import java.io.Serializable;



/**

 * @author mgbarsky
 *Simple class consists of two integer fields. Can be used in any place where two-dimensional point coordinates or 
 *the start and and end indices of a substring should be saved or passed.
 *The class implements Comparable - in order to sort List of indexpair objects, and 
 *Serializable - in order to be able to save and to read from secondary storage.
 */

public class IndexPair implements Serializable, Comparable

{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3230705850675333275L;

	private int _index1;

	private int _index2;

	public IndexPair(int index1, int index2)

	{

		_index1=index1;

		_index2=index2;

	}

	public int getIndex1()

	{

		return _index1;

	}

	public int getIndex2()

	{

		return _index2;

	}

	public String toString()

	{

		return "("+_index1+","+_index2+")";

	}

	/**
	 * The implementation of Comparable Interface. The comparison of two points is performed:
	 * if first coordinates are equal, then IndexPair is bigger if the second coordinate is bigger.
	 * Otherwise, the IndexPair with a bigger first coordinate is bigger. 
	 * Used to sort List of IndexPair objects in ascending order.
	 */

	public int compareTo(Object pair_to_compare)

	{

		IndexPair second=(IndexPair)pair_to_compare;

		if(this.getIndex1()==second.getIndex1())

		{

			if(this.getIndex2()==second.getIndex2())

				return 0;

			if(this.getIndex2()>second.getIndex2())

				return 1;

		}

			

		if(this.getIndex1()>second.getIndex1())

		{

			return 1;

		}

		return -1;

	}

	

}

