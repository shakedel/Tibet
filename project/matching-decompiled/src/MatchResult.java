import general.Interval;

public class MatchResult {

   public int idM;
   public int idL;
   public Interval workInterval;
   public double score;


   public MatchResult(Interval workInterval, double score, int idM, int idL) {
      this.workInterval = workInterval;
      this.score = score;
      this.idM = idM;
      this.idL = idL;
   }

   public boolean equals(Object obj) {
      if(obj == this) {
         return true;
      } else if(obj != null && obj.getClass() == this.getClass()) {
         MatchResult iMatch = (MatchResult)obj;
         return this.idM == iMatch.idM && this.idL == iMatch.idL;
      } else {
         return false;
      }
   }

   public String toString() {
      String str = this.workInterval.getStart().getIndex1() + "," + this.workInterval.getStart().getIndex2() + "," + this.workInterval.getEnd().getIndex1() + "," + this.workInterval.getEnd().getIndex2() + "," + this.score + "," + this.idM + "," + this.idL;
      return str;
   }
}
