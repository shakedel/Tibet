package general;

import general.IndexPair;
import java.io.Serializable;

public class Interval implements Serializable {

   IndexPair _first;
   IndexPair _last;
   int _length;
   int _diff;
   private static final long serialVersionUID = -3230705851675333275L;


   public Interval(IndexPair first, IndexPair last) {
      this._first = first;
      this._last = last;
   }

   public Interval(IndexPair first, IndexPair last, int len, int diff) {
      this._first = first;
      this._last = last;
      this._length = len;
      this._diff = diff;
   }

   public int getLength() {
      return this._length;
   }

   public int getDiff() {
      return this._diff;
   }

   public IndexPair getStart() {
      return this._first;
   }

   public IndexPair getEnd() {
      return this._last;
   }

   public String toString() {
      return this._first + " - " + this._last;
   }
}
