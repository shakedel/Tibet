package algorithms;

import general.IndexPair;
import general.Interval;
import general.IntervalComparator;
import general.SequenceFileReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import suffixtree.SuffixTreeNode;
import suffixtree.SuffixTreeWriter;
import suffixtree.UkkonenSuffixTree;
import suffixtree.UkkonenSuffixTree.SuffixNode;

public class BYGUmapString {

   SuffixTreeNode _tree1;
   SuffixTreeNode _tree2;
   public int _maxLength;
   public int INFINITY = 0;
   char[] _seq1;
   char[] _seq2;
   int _length1;
   int _length2;
   int _maxDiff;
   int _minLength;
   List _solutions = new LinkedList();
   int[][] _table;
   public boolean freeEnds = true;
   boolean _produceOutput = true;
   Map _previousResults;
   Map _monitor;
   static final int ROW_HEADER = 0;
   static final int COL_HEADER = 1;
   static final int LAST_ROW = 2;
   static final int LAST_COL = 3;
   char _terminator1;
   char _terminator2;


   public BYGUmapString(SuffixTreeNode tree1, SuffixTreeNode tree2, String seq1, String terminator1, String seq2, String terminator2, int maxDiff, int minLen) {
      this._tree1 = tree1;
      this._tree2 = tree2;
      this._seq1 = (seq1 + terminator1).toCharArray();
      this._length1 = this._seq1.length;
      this._seq2 = (seq2 + terminator2).toCharArray();
      this._length2 = this._seq2.length;
      this._minLength = minLen;
      this._maxDiff = maxDiff + 1;
      this._maxLength = general.Utils.min(2 * this._minLength, general.Utils.max(this._length1, this._length2));
      this.INFINITY = this._maxDiff + 1;
      this._terminator1 = terminator1.charAt(0);
      this._terminator2 = terminator2.charAt(0);
      System.out.println("started");
   }

   public void withOutput(boolean yesno) {
      this._produceOutput = yesno;
   }

   public void process() {
      List rootchildren1 = this._tree1.children;
      List rootchildren2 = this._tree2.children;

      for(int i = 0; i < rootchildren1.size(); ++i) {
         SuffixTreeNode node1 = (SuffixTreeNode)rootchildren1.get(i);

         for(int j = 0; j < rootchildren2.size(); ++j) {
            SuffixTreeNode node2 = (SuffixTreeNode)rootchildren2.get(j);
            this.processSubtrees(node1, node2);
         }
      }

      Collections.sort(this._solutions, new IntervalComparator());
   }

   public List getMaximalSolutions() {
      LinkedList ret = new LinkedList();
      if(this._solutions.size() <= 1) {
         return this._solutions;
      } else {
         boolean maxI = false;
         Interval first = (Interval)this._solutions.get(0);
         int var7 = first.getEnd().getIndex1();
         LinkedList temp = new LinkedList();

         for(int i = 0; i < this._solutions.size(); ++i) {
            Interval curr = (Interval)this._solutions.get(i);
            if(curr.getStart().getIndex1() <= var7 - this._minLength + 1) {
               temp.add(curr);
               var7 = Math.max(var7, curr.getEnd().getIndex1());
               if(i == this._solutions.size() - 1) {
                  this.createMaximalSolutions(temp, ret);
               }
            } else {
               this.createMaximalSolutions(temp, ret);
               temp = new LinkedList();
               temp.add(curr);
               var7 = curr.getEnd().getIndex1();
               if(i == this._solutions.size() - 1) {
                  this.createMaximalSolutions(temp, ret);
               }
            }
         }

         return ret;
      }
   }

   private void createMaximalSolutions(List overlappings, List res) {
      if(overlappings.size() == 1) {
         res.add(overlappings.get(0));
      } else {
         int i;
         Interval curr;
         for(i = 0; i < overlappings.size(); ++i) {
            curr = (Interval)overlappings.get(i);

            for(int j = 0; j < overlappings.size(); ++j) {
               if(i != j) {
                  Interval curr2 = (Interval)overlappings.get(j);
                  if(curr != null && curr2 != null && curr2.getStart().getIndex1() >= curr.getStart().getIndex1() && curr2.getStart().getIndex2() >= curr.getStart().getIndex2() && curr2.getEnd().getIndex1() <= curr.getEnd().getIndex1() && curr2.getEnd().getIndex2() <= curr.getEnd().getIndex2()) {
                     overlappings.set(j, (Object)null);
                  }
               }
            }
         }

         for(i = 0; i < overlappings.size(); ++i) {
            curr = (Interval)overlappings.get(i);
            if(curr != null) {
               res.add(curr);
            }
         }

      }
   }

   private void processSubtrees(SuffixTreeNode root1, SuffixTreeNode root2) {
      if(!this.freeEnds || this._seq1[root1.labelStart] == this._seq2[root2.labelStart]) {
         this._previousResults = new HashMap();
         this._monitor = new HashMap();
         System.out.println("Now processing root1 with " + root1.totalChildren + " children via root2 with " + root2.totalChildren + " children.");
         this.fillTableForRoots(root1, root2);
         this.fillTableForRoot1ChildrenRoot2(root1, root2);
         List root1children = root1.children;
         if(root1children != null && root1children.size() > 0) {
            for(int i = 0; i < root1children.size(); ++i) {
               SuffixTreeNode child1 = (SuffixTreeNode)root1children.get(i);
               this.fillTableForChild1ChildrenRoot2(child1, root2);
            }
         }

      }
   }

   private void fillTableForRoot1ChildrenRoot2(SuffixTreeNode root1, SuffixTreeNode subtree2) {
      List subtree2children = subtree2.children;
      if(subtree2children != null && subtree2.children.size() > 0) {
         this.fillTableForRoot1Children2(root1, subtree2children);
      }

   }

   private void fillTableForChild1ChildrenRoot2(SuffixTreeNode curr1, SuffixTreeNode subtree2) {
      this.fillTableForChild1Root2(curr1, subtree2);
      List subtree2children = subtree2.children;
      if(subtree2children != null && subtree2.children.size() > 0) {
         for(int curr1children = 0; curr1children < subtree2.children.size(); ++curr1children) {
            SuffixTreeNode i = (SuffixTreeNode)subtree2children.get(curr1children);
            this.fillTableForChild1Child2(curr1, i);
         }
      }

      List var7 = curr1.children;
      if(var7 != null && var7.size() > 0) {
         for(int var8 = 0; var8 < var7.size(); ++var8) {
            SuffixTreeNode curr1child = (SuffixTreeNode)var7.get(var8);
            this.fillTableForChild1ChildrenRoot2(curr1child, subtree2);
         }
      }

   }

   private void fillTableForChild1Child2(SuffixTreeNode curr1, SuffixTreeNode curr2) {
      int prevI = curr1.parentOrderNumber;
      int currI = curr1.orderNumber;
      int prevJ = curr2.parentOrderNumber;
      int currJ = curr2.orderNumber;
      int[][] cornerVals = (int[][])this._previousResults.get(prevI + "_" + prevJ);
      int[][] leftVals = (int[][])this._previousResults.get(currI + "_" + prevJ);
      int[][] upperVals = (int[][])this._previousResults.get(prevI + "_" + currJ);
      int i;
      if(this._monitor.get(prevI + "_" + prevJ) != null) {
         this._table = new int[this._maxLength + 2][this._maxLength + 2];
         int curr2children = cornerVals[1].length + 1;
         i = cornerVals[0].length + 1;
         if(i >= this._table.length - 1 || curr2children >= this._table[i].length - 1) {
            return;
         }

         int curr2child = general.Utils.min(curr2children + 1 + curr2.labelEnd - curr2.labelStart, this._table[0].length - 1);
         int lastRow = general.Utils.min(i + 1 + curr1.labelEnd - curr1.labelStart, this._table.length - 1);
         int[] prevrowheader = leftVals[0];
         int[] prevcolheader = upperVals[1];
         int[] prevlastRowVals = upperVals[2];
         int[] prevlastColVals = leftVals[3];
         int allLeaves1 = 2;

         int allLeaves2;
         for(allLeaves2 = 0; allLeaves1 <= prevlastRowVals.length + 1; ++allLeaves2) {
            this._table[i][allLeaves1] = prevlastRowVals[allLeaves2];
            ++allLeaves1;
         }

         allLeaves1 = 2;

         for(allLeaves2 = 0; allLeaves1 <= prevlastColVals.length + 1; ++allLeaves2) {
            this._table[allLeaves1][curr2children] = prevlastColVals[allLeaves2];
            ++allLeaves1;
         }

         List var31 = null;
         List var32 = null;
         if(this._produceOutput && curr2child - 1 >= this._minLength && lastRow - 1 >= this._minLength) {
            var31 = this.getAllLeaves(curr1);
            var32 = this.getAllLeaves(curr2);
         }

         int tableToSave = 2;

         int rowHeader;
         for(rowHeader = 0; tableToSave <= curr2child; ++rowHeader) {
            this._table[0][tableToSave] = prevcolheader[rowHeader];
            ++tableToSave;
         }

         tableToSave = 2;

         for(rowHeader = 0; tableToSave <= lastRow; ++rowHeader) {
            this._table[tableToSave][0] = prevrowheader[rowHeader];
            ++tableToSave;
         }

         tableToSave = i + 1;

         int colHeader;
         for(rowHeader = i + 1; tableToSave <= lastRow; ++rowHeader) {
            for(colHeader = general.Utils.max(curr2children + 1, rowHeader - this._maxDiff); colHeader <= general.Utils.min(curr2child, rowHeader + this._maxDiff); ++colHeader) {
               this._table[tableToSave][colHeader] = general.Utils.min(this._table[tableToSave - 1][colHeader - 1] + edScore(this._seq1[this._table[tableToSave][0]], this._seq2[this._table[0][colHeader]]), this.infinity(this._table[tableToSave][colHeader - 1]) + 1, this.infinity(this._table[tableToSave - 1][colHeader]) + 1);
               if(this._produceOutput && tableToSave - 1 >= this._minLength && colHeader - 1 >= this._minLength && this._table[tableToSave][colHeader] <= this._maxDiff && this._seq1[this._table[tableToSave][0]] != this._terminator1 && this._seq2[this._table[0][colHeader]] != this._terminator2) {
                  this.addToSolution(var31, var32, tableToSave - 1, colHeader - 1);
               }
            }

            ++tableToSave;
         }

         int[][] var33 = new int[4][];
         int[] var34 = new int[lastRow - 1];

         for(colHeader = 2; colHeader <= lastRow; ++colHeader) {
            var34[colHeader - 2] = this._table[colHeader][0];
         }

         var33[0] = var34;
         int[] var35 = new int[curr2child - 1];

         for(int lastRowVals = 2; lastRowVals <= curr2child; ++lastRowVals) {
            var35[lastRowVals - 2] = this._table[0][lastRowVals];
         }

         var33[1] = var35;
         int[] var36 = new int[curr2child - 1];
         int[] leftprevlastRowVals = leftVals[2];
         boolean continuecompare = false;

         int lastColVals;
         for(lastColVals = 2; lastColVals <= curr2children; ++lastColVals) {
            var36[lastColVals - 2] = leftprevlastRowVals[lastColVals - 2];
            if(!continuecompare && this.infinity(leftprevlastRowVals[lastColVals - 2]) <= this._maxDiff) {
               continuecompare = true;
            }
         }

         for(lastColVals = curr2children + 1; lastColVals <= curr2child; ++lastColVals) {
            var36[lastColVals - 2] = this._table[lastRow][lastColVals];
            if(!continuecompare && this.infinity(this._table[lastRow][lastColVals]) <= this._maxDiff) {
               continuecompare = true;
            }
         }

         var33[2] = var36;
         int[] var37 = new int[lastRow - 1];
         int[] upperprevlastColVals = upperVals[3];

         int i1;
         for(i1 = 2; i1 <= i; ++i1) {
            var37[i1 - 2] = upperprevlastColVals[i1 - 2];
            if(!continuecompare && this.infinity(upperprevlastColVals[i1 - 2]) <= this._maxDiff) {
               continuecompare = true;
            }
         }

         for(i1 = i + 1; i1 <= lastRow; ++i1) {
            var37[i1 - 2] = this._table[i1][curr2child];
            if(!continuecompare && this.infinity(this._table[i1][curr2child]) <= this._maxDiff) {
               continuecompare = true;
            }
         }

         var33[3] = var37;
         this._previousResults.put(curr1.orderNumber + "_" + curr2.orderNumber, var33);
         if(continuecompare) {
            this._monitor.put(curr1.orderNumber + "_" + curr2.orderNumber, new Boolean(continuecompare));
         }
      }

      List var29 = curr2.children;
      if(var29 != null && var29.size() > 0) {
         for(i = 0; i < var29.size(); ++i) {
            SuffixTreeNode var30 = (SuffixTreeNode)var29.get(i);
            this.fillTableForChild1Child2(curr1, var30);
         }
      }

   }

   private void fillTableForChild1Root2(SuffixTreeNode curr1, SuffixTreeNode root2) {
      byte prevJ = 0;
      int prevI = curr1.parentOrderNumber;
      int[][] prevValues = (int[][])this._previousResults.get(prevI + "_" + prevJ);
      if(this._monitor.get(prevI + "_" + prevJ) != null) {
         int[] prevrowheader = prevValues[0];
         int[] prevcolheader = prevValues[1];
         int[] prevlastRowVals = prevValues[2];
         int[] prevlastColVals = prevValues[3];
         int prevlastCol = prevcolheader.length + 1;
         int prevlastRow = prevrowheader.length + 1;
         if(prevlastRow >= this._table.length - 1 || prevlastCol >= this._table[prevlastRow].length - 1) {
            return;
         }

         int lastCol = prevlastCol;
         int lastRow = general.Utils.min(prevlastRow + 1 + curr1.labelEnd - curr1.labelStart, this._table.length - 1);
         this._table = new int[general.Utils.min(this._maxLength + 2, lastRow + 1)][general.Utils.min(this._maxLength + 2, prevlastCol + 1)];
         int allLeaves1 = 2;

         int allLeaves2;
         for(allLeaves2 = 0; allLeaves1 <= prevlastCol; ++allLeaves2) {
            this._table[prevlastRow][allLeaves1] = prevlastRowVals[allLeaves2];
            ++allLeaves1;
         }

         for(allLeaves1 = prevlastRow; allLeaves1 <= lastRow; this._table[allLeaves1][1] = allLeaves1++) {
            ;
         }

         allLeaves1 = 2;

         for(allLeaves2 = 0; allLeaves1 <= prevlastCol; ++allLeaves2) {
            this._table[0][allLeaves1] = prevcolheader[allLeaves2];
            ++allLeaves1;
         }

         List var24 = null;
         List var25 = null;
         if(this._produceOutput && prevlastCol - 1 >= this._minLength && lastRow - 1 >= this._minLength) {
            var24 = this.getAllLeaves(curr1);
            var25 = this.getAllLeaves(root2);
         }

         int pos = curr1.labelStart;

         int tableToSave;
         for(tableToSave = prevlastRow + 1; tableToSave <= lastRow; ++pos) {
            this._table[tableToSave][0] = pos;
            ++tableToSave;
         }

         tableToSave = prevlastRow + 1;

         int colHeader;
         for(int rowHeader = prevlastRow + 1; tableToSave <= lastRow; ++rowHeader) {
            for(colHeader = general.Utils.max(2, rowHeader - this._maxDiff); colHeader <= general.Utils.min(lastCol, rowHeader + this._maxDiff); ++colHeader) {
               this._table[tableToSave][colHeader] = general.Utils.min(this._table[tableToSave - 1][colHeader - 1] + edScore(this._seq1[this._table[tableToSave][0]], this._seq2[this._table[0][colHeader]]), this.infinity(this._table[tableToSave][colHeader - 1]) + 1, this.infinity(this._table[tableToSave - 1][colHeader]) + 1);
               if(this._produceOutput && tableToSave - 1 >= this._minLength && colHeader - 1 >= this._minLength && this._table[tableToSave][colHeader] <= this._maxDiff && this._seq1[this._table[tableToSave][0]] != this._terminator1 && this._seq2[this._table[0][colHeader]] != this._terminator2) {
                  this.addToSolution(var24, var25, tableToSave - 1, colHeader - 1);
               }
            }

            ++tableToSave;
         }

         int[][] var26 = new int[4][];
         int[] var27 = new int[lastRow - 1];

         for(colHeader = 2; colHeader <= prevlastRow; ++colHeader) {
            var27[colHeader - 2] = prevrowheader[colHeader - 2];
         }

         for(colHeader = prevlastRow + 1; colHeader <= lastRow; ++colHeader) {
            var27[colHeader - 2] = this._table[colHeader][0];
         }

         var26[0] = var27;
         int[] var29 = new int[lastCol - 1];

         for(int continuecompare = 2; continuecompare <= lastCol; ++continuecompare) {
            var29[continuecompare - 2] = this._table[0][continuecompare];
         }

         var26[1] = var29;
         boolean var28 = false;
         int[] lastRowVals = new int[lastCol - 1];

         for(int lastColVals = 2; lastColVals <= lastCol; ++lastColVals) {
            if(!var28 && this.infinity(this._table[lastRow][lastColVals]) <= this._maxDiff) {
               var28 = true;
            }

            lastRowVals[lastColVals - 2] = this._table[lastRow][lastColVals];
         }

         var26[2] = lastRowVals;
         int[] var30 = new int[lastRow - 1];

         int i;
         for(i = 2; i <= prevlastRow; ++i) {
            if(!var28 && this.infinity(prevlastColVals[i - 2]) <= this._maxDiff) {
               var28 = true;
            }

            var30[i - 2] = prevlastColVals[i - 2];
         }

         for(i = prevlastRow + 1; i <= lastRow; ++i) {
            if(!var28 && this.infinity(this._table[i][lastCol]) <= this._maxDiff) {
               var28 = true;
            }

            var30[i - 2] = this._table[i][lastCol];
         }

         var26[3] = var30;
         this._previousResults.put(curr1.orderNumber + "_" + 0, var26);
         if(var28) {
            this._monitor.put(curr1.orderNumber + "_" + 0, new Boolean(var28));
         }
      }

   }

   private void fillTableForRoot1Children2(SuffixTreeNode root1, List children2) {
      for(int c = 0; c < children2.size(); ++c) {
         SuffixTreeNode child2 = (SuffixTreeNode)children2.get(c);
         int prevJ = child2.parentOrderNumber;
         byte prevI = 0;
         int[][] prevValues = (int[][])this._previousResults.get(prevI + "_" + prevJ);
         if(this._monitor.get(prevI + "_" + prevJ) != null) {
            int[] grandchildren = prevValues[0];
            int[] prevcolheader = prevValues[1];
            int[] prevlastRowVals = prevValues[2];
            int[] prevlastColVals = prevValues[3];
            this._table = new int[this._maxLength + 2][this._maxLength + 2];
            int prevlastCol = prevcolheader.length + 1;
            int prevlastRow = grandchildren.length + 1;
            if(prevlastRow >= this._table.length - 1 || prevlastCol >= this._table[prevlastRow].length - 1) {
               return;
            }

            int lastCol = general.Utils.min(prevlastCol + 1 + child2.labelEnd - child2.labelStart, this._table[0].length - 1);
            int lastRow = prevlastRow;
            int allLeaves1 = 2;

            int allLeaves2;
            for(allLeaves2 = 0; allLeaves1 <= prevlastRow; ++allLeaves2) {
               this._table[allLeaves1][prevlastCol] = prevlastColVals[allLeaves2];
               ++allLeaves1;
            }

            for(allLeaves1 = prevlastCol; allLeaves1 <= lastCol; this._table[1][allLeaves1] = allLeaves1++) {
               ;
            }

            allLeaves1 = 2;

            for(allLeaves2 = 0; allLeaves1 <= prevlastRow; ++allLeaves2) {
               this._table[allLeaves1][0] = grandchildren[allLeaves2];
               ++allLeaves1;
            }

            List var27 = null;
            List var28 = null;
            if(this._produceOutput && lastCol - 1 >= this._minLength && prevlastRow - 1 >= this._minLength) {
               var27 = this.getAllLeaves(root1);
               var28 = this.getAllLeaves(child2);
            }

            int pos = child2.labelStart;

            int tableToSave;
            for(tableToSave = prevlastCol + 1; tableToSave <= lastCol; ++pos) {
               this._table[0][tableToSave] = pos;
               ++tableToSave;
            }

            tableToSave = 2;

            int colHeader;
            for(int rowHeader = 2; tableToSave <= lastRow; ++rowHeader) {
               for(colHeader = general.Utils.max(prevlastCol + 1, rowHeader - this._maxDiff); colHeader <= general.Utils.min(lastCol, rowHeader + this._maxDiff + 1); ++colHeader) {
                  this._table[tableToSave][colHeader] = general.Utils.min(this._table[tableToSave - 1][colHeader - 1] + edScore(this._seq1[this._table[tableToSave][0]], this._seq2[this._table[0][colHeader]]), this.infinity(this._table[tableToSave][colHeader - 1]) + 1, this.infinity(this._table[tableToSave - 1][colHeader]) + 1);
                  if(this._produceOutput && tableToSave - 1 >= this._minLength && colHeader - 1 >= this._minLength && this._table[tableToSave][colHeader] <= this._maxDiff && this._seq1[this._table[tableToSave][0]] != this._terminator1 && this._seq2[this._table[0][colHeader]] != this._terminator2) {
                     this.addToSolution(var27, var28, tableToSave - 1, colHeader - 1);
                  }
               }

               ++tableToSave;
            }

            int[][] var29 = new int[4][];
            int[] var30 = new int[lastRow - 1];

            for(colHeader = 2; colHeader <= lastRow; ++colHeader) {
               var30[colHeader - 2] = this._table[colHeader][0];
            }

            var29[0] = var30;
            int[] var31 = new int[lastCol - 1];

            int continuecompare;
            for(continuecompare = 2; continuecompare <= prevlastCol; ++continuecompare) {
               var31[continuecompare - 2] = prevcolheader[continuecompare - 2];
            }

            for(continuecompare = prevlastCol + 1; continuecompare <= lastCol; ++continuecompare) {
               var31[continuecompare - 2] = this._table[0][continuecompare];
            }

            var29[1] = var31;
            boolean var32 = false;
            int[] lastRowVals = new int[lastCol - 1];

            int lastColVals;
            for(lastColVals = 2; lastColVals <= prevlastCol; ++lastColVals) {
               lastRowVals[lastColVals - 2] = prevlastRowVals[lastColVals - 2];
               if(!var32 && this.infinity(prevlastRowVals[lastColVals - 2]) <= this._maxDiff) {
                  var32 = true;
               }
            }

            for(lastColVals = prevlastCol + 1; lastColVals <= lastCol; ++lastColVals) {
               lastRowVals[lastColVals - 2] = this._table[lastRow][lastColVals];
               if(!var32 && this.infinity(this._table[lastRow][lastColVals]) <= this._maxDiff) {
                  var32 = true;
               }
            }

            var29[2] = lastRowVals;
            int[] var33 = new int[lastRow - 1];

            for(int i = 2; i <= lastRow; ++i) {
               var33[i - 2] = this._table[i][lastCol];
               if(!var32 && this.infinity(this._table[i][lastCol]) <= this._maxDiff) {
                  var32 = true;
               }
            }

            var29[3] = var33;
            this._previousResults.put("0_" + child2.orderNumber, var29);
            if(var32) {
               this._monitor.put("0_" + child2.orderNumber, new Boolean(var32));
            }
         }

         List var26 = child2.children;
         if(var26 != null && var26.size() > 0) {
            this.fillTableForRoot1Children2(root1, var26);
         }
      }

   }

   private List getAllLeaves(SuffixTreeNode node) {
      ArrayList ret = new ArrayList();
      this.appendChildLeaves(ret, node);
      return ret;
   }

   private int infinity(int val) {
      return val == 0?this.INFINITY:val;
   }

   private void appendChildLeaves(List res, SuffixTreeNode parent) {
      if(parent.children != null && parent.children.size() != 0) {
         List childNodes = parent.children;

         for(int i = 0; i < childNodes.size(); ++i) {
            SuffixTreeNode child = (SuffixTreeNode)childNodes.get(i);
            this.appendChildLeaves(res, child);
         }

      } else {
         res.add(new Integer(parent.leafNumber));
      }
   }

   private void fillTableForRoots(SuffixTreeNode root1, SuffixTreeNode root2) {
      this._table = new int[this._maxLength + 2][this._maxLength + 2];
      int lastRow = general.Utils.min(2 + (root1.labelEnd - root1.labelStart), this._table.length - 1);
      int lastCol = general.Utils.min(2 + (root2.labelEnd - root2.labelStart), this._table[0].length - 1);
      int pos = root1.labelStart;

      int allLeaves1;
      for(allLeaves1 = 2; allLeaves1 <= lastRow; ++pos) {
         this._table[allLeaves1][0] = pos;
         ++allLeaves1;
      }

      pos = root2.labelStart;

      for(allLeaves1 = 2; allLeaves1 <= lastCol; ++pos) {
         this._table[0][allLeaves1] = pos;
         ++allLeaves1;
      }

      List var15 = null;
      List allLeaves2 = null;
      if(this._produceOutput && lastCol - 1 >= this._minLength && lastRow - 1 >= this._minLength) {
         var15 = this.getAllLeaves(root1);
         allLeaves2 = this.getAllLeaves(root2);
      }

      int tableToSave;
      for(tableToSave = 1; tableToSave <= lastRow; this._table[tableToSave][1] = tableToSave++) {
         ;
      }

      for(tableToSave = 1; tableToSave <= lastCol; this._table[1][tableToSave] = tableToSave++) {
         ;
      }

      tableToSave = 2;

      int colHeader;
      for(int rowHeader = 2; tableToSave <= lastRow; ++rowHeader) {
         for(colHeader = general.Utils.max(2, rowHeader - this._maxDiff); colHeader <= general.Utils.min(lastCol, rowHeader + this._maxDiff); ++colHeader) {
            this._table[tableToSave][colHeader] = general.Utils.min(this._table[tableToSave - 1][colHeader - 1] + edScore(this._seq1[this._table[tableToSave][0]], this._seq2[this._table[0][colHeader]]), this.infinity(this._table[tableToSave][colHeader - 1]) + 1, this.infinity(this._table[tableToSave - 1][colHeader]) + 1);
            if(tableToSave - 1 >= this._minLength && colHeader - 1 >= this._minLength && this._table[tableToSave][colHeader] <= this._maxDiff && this._seq1[this._table[tableToSave][0]] != this._terminator1 && this._seq2[this._table[0][colHeader]] != this._terminator2) {
               this.addToSolution(var15, allLeaves2, tableToSave - 1, colHeader - 1);
            }
         }

         ++tableToSave;
      }

      int[][] var17 = new int[4][];
      int[] var16 = new int[lastRow - 1];

      for(colHeader = 2; colHeader <= lastRow; ++colHeader) {
         var16[colHeader - 2] = this._table[colHeader][0];
      }

      var17[0] = var16;
      int[] var18 = new int[lastCol - 1];

      for(int continuecompare = 2; continuecompare <= lastCol; ++continuecompare) {
         var18[continuecompare - 2] = this._table[0][continuecompare];
      }

      var17[1] = var18;
      boolean var19 = false;
      int[] lastRowVals = new int[lastCol - 1];

      for(int lastColVals = 2; lastColVals <= lastCol; ++lastColVals) {
         if(!var19 && this.infinity(this._table[lastRow][lastColVals]) <= this._maxDiff) {
            var19 = true;
         }

         lastRowVals[lastColVals - 2] = this._table[lastRow][lastColVals];
      }

      var17[2] = lastRowVals;
      int[] var20 = new int[lastRow - 1];

      for(int i = 2; i <= lastRow; ++i) {
         if(!var19 && this.infinity(this._table[i][lastCol]) <= this._maxDiff) {
            var19 = true;
         }

         var20[i - 2] = this._table[i][lastCol];
      }

      var17[3] = var20;
      this._previousResults.put("0_0", var17);
      if(var19) {
         this._monitor.put("0_0", new Boolean(var19));
      }

   }

   private void addToSolution(List positions1, List positions2, int len1, int len2) {
      for(int i = 0; i < positions1.size(); ++i) {
         int startI = ((Integer)positions1.get(i)).intValue();
         int endI = startI + len1 - 1;

         for(int j = 0; j < positions2.size(); ++j) {
            int startJ = ((Integer)positions2.get(j)).intValue();
            int endJ = startJ + len2 - 1;
            if(this.freeEnds && this._seq1[startI] == this._seq2[startJ] && this._seq1[endI] == this._seq2[endJ] && this._table[len1][len2] <= general.Utils.min(this.infinity(this._table[len1 + 1][len2]), this.infinity(this._table[len1][len2 + 1])) + 1 || !this.freeEnds) {
               IndexPair start = new IndexPair(startI, startJ);
               IndexPair end = new IndexPair(endI, endJ);
               Interval interval = new Interval(start, end);
               this._solutions.add(interval);
            }
         }
      }

   }

   public List getSolutions() {
      return this._solutions;
   }

   private static int edScore(char first, char second) {
      return first == second?0:1;
   }

   public static void main(String[] args) {
      String file1 = null;
      String file2 = null;
      String terminator1 = null;
      String terminator2 = null;
      int maxDiff = 0;
      int minLen = 0;
      boolean withOutput = true;
      boolean maximalOutput = false;
      boolean printStdOutput = false;
      boolean treesFromFile = true;
      String suffixTreeFileName1 = null;
      String suffixTreeFileName2 = null;

      try {
         file1 = args[0];
         file2 = args[1];
         terminator1 = args[2];
         terminator2 = args[3];
         if(terminator1.length() > 1 || terminator2.length() > 1 || terminator1.equals(terminator2)) {
            System.out.println("The termination chars should be 2 different characters not containing in the main alphabet.");
            System.exit(1);
         }

         minLen = Integer.parseInt(args[4]);
         maxDiff = Integer.parseInt(args[5]);
         int reader = Integer.parseInt(args[6]);
         if(reader == 0) {
            withOutput = false;
         }

         reader = Integer.parseInt(args[7]);
         if(reader == 1) {
            maximalOutput = true;
         }

         reader = Integer.parseInt(args[8]);
         if(reader == 1) {
            printStdOutput = true;
         }

         reader = Integer.parseInt(args[9]);
         if(reader == 0) {
            treesFromFile = false;
         }

         if(treesFromFile) {
            suffixTreeFileName1 = args[10];
            suffixTreeFileName2 = args[11];
         }
      } catch (Exception var30) {
         System.out.println("Usage: \njava -Xmx512M -Xms512m -classpath classes algorithms.BYGU \\ \n<filename1> <filename2> \\ \n<terminationchar1> <terminationchar2> \\ \n<minLength> <maxDifferences> <produceOutput: 1 | 0>\\ \n<maximalOutput: 1 | 0> <printStdOutput: 1 | 0> <readTreesFromFiles: 1 | 0>\\ \n if readTreesFromFiles <suffixtreefilename1> <suffixtreefilename2>");
         System.exit(1);
      }

      SequenceFileReader var31 = new SequenceFileReader(file1);
      String seq1 = var31.getSequence();
      var31 = new SequenceFileReader(file2);
      String seq2 = var31.getSequence();
      SuffixTreeNode tree1 = null;
      SuffixTreeNode tree2 = null;
      if(treesFromFile) {
         FileInputStream start;
         ObjectInputStream root1;
         try {
            start = new FileInputStream(suffixTreeFileName1);
            root1 = new ObjectInputStream(start);
            tree1 = (SuffixTreeNode)root1.readObject();
            root1.close();
            start.close();
         } catch (Exception var29) {
            System.out.println("File " + suffixTreeFileName1 + " not found where expected or is of an invalid type");
            var29.printStackTrace();
            System.exit(1);
         }

         try {
            start = new FileInputStream(suffixTreeFileName2);
            root1 = new ObjectInputStream(start);
            tree2 = (SuffixTreeNode)root1.readObject();
            root1.close();
            start.close();
         } catch (Exception var28) {
            System.out.println("File " + suffixTreeFileName2 + " not found where expected or is of an invalid type");
            var28.printStackTrace();
            return;
         }
      } else {
         UkkonenSuffixTree var32 = new UkkonenSuffixTree();
         var32.setTerminationChar(terminator1.charAt(0));
         var32.addSequence(seq1 + terminator1, file1, true);
         SuffixNode var34 = var32.getRoot();
         SuffixTreeWriter algorithm = new SuffixTreeWriter();
         tree1 = algorithm.convertTree(var34);
         UkkonenSuffixTree howlong = new UkkonenSuffixTree();
         var32.setTerminationChar(terminator2.charAt(0));
         var32.addSequence(seq2 + terminator2, file2, true);
         SuffixNode root2 = howlong.getRoot();
         algorithm = new SuffixTreeWriter();
         tree2 = algorithm.convertTree(root2);
      }

      System.out.println("BYGU algorithm");
      long var33 = System.currentTimeMillis();
      BYGUmapString var35 = new BYGUmapString(tree1, tree2, seq1, terminator1, seq2, terminator2, maxDiff, minLen);
      var35.withOutput(withOutput);
      var35.process();
      long var36 = System.currentTimeMillis() - var33;
      System.out.println("Processed in " + var36 + " ms");
      if(withOutput) {
         List solutions = var35.getSolutions();
         System.out.println("Produced output size=" + solutions.size());
         if(maximalOutput) {
            if(solutions.size() > 10000) {
               System.out.println("Producing maximal solutions may be time consuming.");
               System.out.println("Do you still want to continue? y/n");
               String i = null;

               try {
                  BufferedReader curr = new BufferedReader(new InputStreamReader(System.in));
                  i = curr.readLine();
               } catch (Exception var27) {
                  System.exit(1);
               }

               if(i != null && i.toLowerCase().startsWith("n")) {
                  System.exit(0);
               }
            }

            List var37 = var35.getMaximalSolutions();
            System.out.println("Maximal output size=" + var37.size());
            if(printStdOutput) {
               for(int var39 = 0; var39 < var37.size(); ++var39) {
                  Interval curr1 = (Interval)var37.get(var39);
                  System.out.println(curr1);
                  System.out.println(seq1.substring(curr1.getStart().getIndex1(), curr1.getEnd().getIndex1() + 1));
                  System.out.println(seq2.substring(curr1.getStart().getIndex2(), curr1.getEnd().getIndex2() + 1));
               }
            }
         } else if(printStdOutput) {
            for(int var38 = 0; var38 < solutions.size(); ++var38) {
               Interval var40 = (Interval)solutions.get(var38);
               System.out.println(var40);
               System.out.println(seq1.substring(var40.getStart().getIndex1(), var40.getEnd().getIndex1() + 1));
               System.out.println(seq2.substring(var40.getStart().getIndex2(), var40.getEnd().getIndex2() + 1));
            }
         }
      }

   }
}
