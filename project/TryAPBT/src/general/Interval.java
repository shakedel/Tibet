package general;




/**
 * @author mgbarsky
 * This class is intended to save the interval in a two-dimensional space.
 * Optionally it can save an additional inforamtion about the number of matches in two substrings
 * and a number of edit operations - errors - needed to transform one substring into another.
 * The class implements Serializable Interface in order to be saved to and read from disk.
 */

public class Interval implements java.io.Serializable
{

	IndexPair _first;

	IndexPair _last;
	int _length;
	int _diff;
	private static final long serialVersionUID = -3230705851675333275L;

	/**
	 * Constructor
	 * @param first - IndexPair object defining the coordinates of the start point
	 * @param last - IndexPair object defining the coordinates of the end point
	 */
	public Interval(IndexPair first, IndexPair last)
	{
		_first=first;
		_last=last;
	}
	
	/**
	 * Constructor
	 * @param first - IndexPair object defining the coordinates of the start point
	 * @param last - IndexPair object defining the coordinates of the end point
	 * @param len - the number of matches between two substrings 
	 * @param diff - edit distance between two substrings
	 */
	
	public Interval(IndexPair first, IndexPair last,int len,int diff)
	{
		_first=first;
		_last=last;
		_length=len;
		_diff=diff;
	}
	
	/**
	 * 
	 * @return returns the number of matches between two substrings
	 */
    public int getLength()
    {
    	return _length;
    }
    
    /**
     * 
     * @return the minimum number of differences (edit distance) between two substrings
     */
    public int getDiff()
    {
    	return _diff;
    }
    
    /**
     * 
     * @return the start point of an interval - as an IndexPair object
     */
	public IndexPair getStart()
	{
		return _first;
	}

	/**
	 * 
	 * @return the end point of an interval - as an IndexPair object
	 */
	public IndexPair getEnd()
	{
		return _last;
	}

	
	public String toString()
	{
		return _first+" - "+_last;
	}	

}




