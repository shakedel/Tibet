package tau.cs.wolf.tibet.percentage_apbt.data;

import general.Interval;
import tau.cs.wolf.tibet.percentage_apbt.misc.Misc.Formatter;

public class MatchResult {
	
	public int idM;
	public int idL;
	public Interval workInterval;
	public double score;
	
	public MatchResult(Interval workInterval, double score, int idM, int idL)
	{
		this.workInterval = workInterval;
		this.score = score;
		this.idM = idM;
		this.idL = idL;	
	}
	
	@Override
    public boolean equals(Object obj) 
	{
        if (obj == this)
        {
            return true;
        }
        
        if (obj == null || obj.getClass() != this.getClass())
        {
            return false;
        }

        MatchResult iMatch = (MatchResult) obj;
        
        return ((this.idM == iMatch.idM) && (this.idL == iMatch.idL));
	}
	
	@Override 
	public String toString() 
	{
		String str = workInterval.getStart().getIndex1() + "," + workInterval.getStart().getIndex2() + "," + workInterval.getEnd().getIndex1() + "," + workInterval.getEnd().getIndex2() + "," + score + "," + idM + "," + idL;
		return str;
	}

	public static class DefaultFormatter implements Formatter<MatchResult> {
		@Override
		public String format(MatchResult match) {
			StringBuilder sb = new StringBuilder();
			sb.append(match.workInterval.getStart().getIndex1());
			sb.append(",");
			sb.append(match.workInterval.getStart().getIndex2());
			sb.append(",");
			sb.append(match.workInterval.getEnd().getIndex1());
			sb.append(",");
			sb.append(match.workInterval.getEnd().getIndex2());
			sb.append(",");
			sb.append(match.score);
			
			return sb.toString();
		}
		
	}
}
