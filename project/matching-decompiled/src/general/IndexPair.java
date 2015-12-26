package general;

import java.io.Serializable;

public class IndexPair implements Serializable, Comparable {

   private static final long serialVersionUID = -3230705850675333275L;
   private int _index1;
   private int _index2;


   public IndexPair(int index1, int index2) {
      this._index1 = index1;
      this._index2 = index2;
   }

   public int getIndex1() {
      return this._index1;
   }

   public int getIndex2() {
      return this._index2;
   }

   public String toString() {
      return "(" + this._index1 + "," + this._index2 + ")";
   }

   public int compareTo(Object pair_to_compare) {
      IndexPair second = (IndexPair)pair_to_compare;
      if(this.getIndex1() == second.getIndex1()) {
         if(this.getIndex2() == second.getIndex2()) {
            return 0;
         }

         if(this.getIndex2() > second.getIndex2()) {
            return 1;
         }
      }

      return this.getIndex1() > second.getIndex1()?1:-1;
   }
}
