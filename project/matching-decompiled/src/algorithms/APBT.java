package algorithms;

import general.IndexPair;
import general.Interval;
import general.IntervalComparator;
import general.SequenceFileReader;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class APBT {

   public final int CHUNK_SIZE = 1000;
   public int MAX_LENGTH = 300;
   int _chunkSize;
   int _maxLength;
   List _solutions = new LinkedList();
   Map _solutionsMap = new HashMap();
   int _maxDiff;
   int _minLength;
   Map _charPositions2;
   boolean[][] _matrix;
   public String _sequence1;
   public String _sequence2;
   int _length1;
   int _length2;
   char[] _seq1;
   char[] _seq2;
   char[] _originalSeq1;
   char[] _originalSeq2;
   Object[][] _state;
   private boolean byColumn = false;


   public APBT(char[] seq1arr, char[] seq2arr, int minLength, int maxDiff) {
      this._seq1 = seq1arr;
      this._seq2 = seq2arr;
      this._length1 = this._seq1.length;
      this._length2 = this._seq2.length;
      this._matrix = new boolean[this._length1][];
      this._minLength = minLength;
      this._maxDiff = maxDiff;
      this._maxLength = this.MAX_LENGTH;
      this._chunkSize = 1000;
   }

   private void continuePath(int startI, int startJ, int currI, int currJ, int currlen, int currdiff, int shiftJ, boolean continueFurther) {
      if(currlen < this._maxLength) {
         if(continueFurther) {
            if(currlen > 1) {
               int[] LT_I = (int[])this._state[currI % this._maxLength][currJ];
               if(LT_I == null) {
                  LT_I = new int[2];
                  if(currI - startI <= currJ - startJ) {
                     LT_I[0] = currdiff;
                     LT_I[1] = this._maxDiff + 1;
                  } else {
                     LT_I[1] = currdiff;
                     LT_I[0] = this._maxDiff + 1;
                  }

                  this._state[currI % this._maxLength][currJ] = LT_I;
               } else {
                  if(currI - startI <= currJ - startJ) {
                     if(LT_I[0] <= currdiff) {
                        return;
                     }

                     LT_I[0] = currdiff;
                  } else {
                     if(LT_I[1] <= currdiff) {
                        return;
                     }

                     LT_I[1] = currdiff;
                  }

                  this._state[currI % this._maxLength][currJ] = LT_I;
               }
            }

            int var21 = Math.min(currI + 1, this._length1 - 1);
            int LT_J = Math.min(currJ + 1, this._length2 - 1);
            int RT_J = Math.min(LT_J + this._maxDiff + 1 - currdiff, this._length2);
            int LB_I = Math.min(var21 + this._maxDiff + 1 - currdiff, this._length1);
            int currJBound = RT_J;
            int currIBound = LB_I;
            boolean stop = false;
            int k = currI + 1;

            int i;
            for(i = currJ + 1; k < Math.min(currI + this._maxDiff + 2 - currdiff, this._length1) && i < Math.min(currJ + this._maxDiff + 2 - currdiff, this._length2) && !stop; ++i) {
               if(this._matrix[k][i]) {
                  this.continuePath(startI, startJ, k, i, Math.min(k - startI, i - startJ) + 1, Math.max(k - currI, i - currJ) - 1 + currdiff, shiftJ, true);
                  stop = true;
                  currJBound = i;
                  currIBound = k;
               }

               ++k;
            }

            for(k = currIBound + 1; k < LB_I; ++k) {
               if(this._matrix[k][currJBound]) {
                  this.continuePath(startI, startJ, k, currJBound, Math.min(k - startI, currJBound - startJ) + 1, Math.max(k - currI, currJBound - currJ) - 1 + currdiff, shiftJ, false);
               }
            }

            for(k = currJBound + 1; k < RT_J; ++k) {
               if(this._matrix[currIBound][k]) {
                  this.continuePath(startI, startJ, currIBound, k, Math.min(currIBound - startI, k - startJ) + 1, Math.max(currIBound - currI, k - currJ) - 1 + currdiff, shiftJ, false);
               }
            }

            for(k = 1; k < this._maxDiff + 1 - currdiff; ++k) {
               stop = false;
               i = var21 + k;

               int j;
               int m;
               for(j = LT_J; i < LB_I && j < currJBound && !stop; ++j) {
                  if(this._matrix[i][j]) {
                     this.continuePath(startI, startJ, i, j, Math.min(i - startI, j - startJ) + 1, Math.max(i - currI, j - currJ) - 1 + currdiff, shiftJ, true);
                     if(j < currJBound) {
                        currJBound = j;

                        for(m = i + 1; m < LB_I; ++m) {
                           if(this._matrix[m][currJBound]) {
                              this.continuePath(startI, startJ, m, currJBound, Math.min(m - startI, currJBound - startJ) + 1, Math.max(m - currI, currJBound - currJ) - 1 + currdiff, shiftJ, false);
                           }
                        }
                     }

                     stop = true;
                  }

                  ++i;
               }

               stop = false;
               i = var21;

               for(j = LT_J + k; i < currIBound && j < RT_J && !stop; ++j) {
                  if(this._matrix[i][j]) {
                     this.continuePath(startI, startJ, i, j, Math.min(i - startI, j - startJ) + 1, Math.max(i - currI, j - currJ) - 1 + currdiff, shiftJ, true);
                     if(i < currIBound) {
                        currIBound = i;

                        for(m = j + 1; m < RT_J; ++m) {
                           if(this._matrix[currIBound][m]) {
                              this.continuePath(startI, startJ, currIBound, m, Math.min(currIBound - startI, m - startJ) + 1, Math.max(currIBound - currI, m - currJ) - 1 + currdiff, shiftJ, false);
                           }
                        }
                     }

                     stop = true;
                  }

                  ++i;
               }
            }
         }

         if(currlen >= this._minLength) {
            this.addToSolutions(startI, startJ + shiftJ, currI, currJ + shiftJ);
         }

      }
   }

   public void process() {
      int chunksNumber = this._length2 / this._chunkSize;

      for(int k = 0; k <= chunksNumber; ++k) {
         int startJ = k * this._chunkSize;
         this.process(startJ);
      }

   }

   public void processByColumn(char[] first, char[] second) {
      this._originalSeq1 = second;
      this._originalSeq2 = first;
      this._seq1 = first;
      this._seq2 = second;
      this._length1 = this._seq1.length;
      this._length2 = this._seq2.length;
      this._matrix = new boolean[this._length1][];
      int chunksNumber = this._length2 / this._chunkSize;
      this.byColumn = true;

      for(int k = 0; k <= chunksNumber; ++k) {
         int startJ = k * this._chunkSize;
         this.process(startJ);
      }

      this._seq1 = this._originalSeq1;
      this._seq2 = this._originalSeq2;
      Collections.sort(this._solutions, new IntervalComparator());
   }

   public List getSolutions() {
      return this._solutions;
   }

   public void savePatterns(List solutions, String outputfilename) {
      try {
         FileOutputStream out = new FileOutputStream(outputfilename);
         ObjectOutputStream s = new ObjectOutputStream(out);
         s.writeObject(solutions);
         s.flush();
         s.close();
         out.close();
      } catch (Exception var5) {
         ;
      }

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

   private void process(int startJ) {
      if(startJ <= this._length2 - this._minLength) {
         this.initializeMatrix(startJ, startJ + this._chunkSize + this._maxLength);

         for(int i = 0; i <= this._length1 - this._minLength; ++i) {
            for(int j = 0; j <= Math.min(this._chunkSize, this._length2 - this._minLength); ++j) {
               if(this._matrix[i][j]) {
                  this.createPaths(i, j, startJ);
               }
            }

            this._state[i % this._maxLength] = new Object[Math.min(this._chunkSize + this._maxLength, this._length2)];
         }

      }
   }

   private void createPaths(int startI, int startJ, int shiftJ) {
      this.continuePath(startI, startJ, startI, startJ, 1, 0, shiftJ, true);
   }

   private void initializeMatrix(int from, int to) {
      this._charPositions2 = new HashMap(20);

      int ccTemp;
      for(ccTemp = from; ccTemp < Math.min(to, this._length2); ++ccTemp) {
         char charSet = this._seq2[ccTemp];
         boolean[] i = (boolean[])this._charPositions2.get(new Character(charSet));
         if(i == null) {
            i = new boolean[Math.min(this._chunkSize + this._maxLength, this._length2)];
         }

         i[ccTemp - from] = true;
         this._charPositions2.put(new Character(charSet), i);
      }

      ccTemp = 0;
      HashSet var8 = new HashSet();

      for(int var9 = 0; var9 < this._length1; ++var9) {
         char curr = this._seq1[var9];
         if(this._charPositions2.get(new Character(curr)) != null) {
            boolean[] row = (boolean[])this._charPositions2.get(new Character(curr));
            this._matrix[var9] = row;
         } else {
            ++ccTemp;
            var8.add(new Character(curr));
            this._matrix[var9] = new boolean[this._length2];
         }
      }

      this._state = new Object[this._maxLength][Math.min(this._chunkSize + this._maxLength, this._length2)];
   }

   private void addToSolutions(int startI, int startJ, int currI, int currJ) {
      IndexPair start = null;
      IndexPair end = null;
      if(this.byColumn) {
         start = new IndexPair(startJ, startI);
         end = new IndexPair(currJ, currI);
      } else {
         start = new IndexPair(startI, startJ);
         end = new IndexPair(currI, currJ);
      }

      Interval interval = new Interval(start, end);
      if(!this._solutionsMap.containsKey(interval.toString())) {
         this._solutionsMap.put(interval.toString(), (Object)null);
         this._solutions.add(interval);
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

   public static void main(String[] args) {
      String file1 = null;
      String file2 = null;
      int maxDiff = 0;
      int minLen = 0;
      boolean maximalSolutions = false;
      boolean printOutput = false;

      try {
         file1 = args[0];
         file2 = args[1];
         minLen = Integer.parseInt(args[2]);
         maxDiff = Integer.parseInt(args[3]);
         int reader = Integer.parseInt(args[4]);
         if(reader == 1) {
            maximalSolutions = true;
         }

         int seq1 = Integer.parseInt(args[5]);
         if(seq1 == 1) {
            printOutput = true;
         }
      } catch (Exception var20) {
         System.out.println("Usage: \njava -Xmx512M -Xms512m  \n<filename1> <filename2>  \n<minLength> <maxDifferences> <maximalOutput: 1 | 0> <printStdOutput: 1 | 0>");
         System.exit(1);
      }

      System.out.println("APBT algorithm");
      SequenceFileReader var21 = new SequenceFileReader(file1);
      String var22 = var21.getSequence();
      var21 = new SequenceFileReader(file2);
      String seq2 = var21.getSequence();
      long start = System.currentTimeMillis();
      APBT algorithm = new APBT(var22.toCharArray(), seq2.toCharArray(), minLen, maxDiff);
      algorithm.process();
      algorithm.processByColumn(seq2.toCharArray(), var22.toCharArray());
      long howlong = System.currentTimeMillis() - start;
      System.out.println("Processed in " + howlong + " ms.");
      List solutions = algorithm.getSolutions();
      System.out.println("Produced output size=" + solutions.size());
      if(maximalSolutions) {
         if(solutions.size() > 10000) {
            System.out.println("Producing maximal solutions may be time consuming.");
            System.out.println("Do you still want to continue? y/n");
            String i = null;

            try {
               BufferedReader curr = new BufferedReader(new InputStreamReader(System.in));
               i = curr.readLine();
            } catch (Exception var19) {
               System.exit(1);
            }

            if(i != null && i.toLowerCase().startsWith("n")) {
               System.exit(0);
            }
         }

         List var23 = algorithm.getMaximalSolutions();
         System.out.println("Maximal output size=" + var23.size());
         if(printOutput) {
            for(int var25 = 0; var25 < var23.size(); ++var25) {
               Interval curr1 = (Interval)var23.get(var25);
               System.out.println(curr1);
               System.out.println(var22.substring(curr1.getStart().getIndex1(), curr1.getEnd().getIndex1() + 1));
               System.out.println(seq2.substring(curr1.getStart().getIndex2(), curr1.getEnd().getIndex2() + 1));
            }
         }
      } else if(printOutput) {
         for(int var24 = 0; var24 < solutions.size(); ++var24) {
            Interval var26 = (Interval)solutions.get(var24);
            System.out.println(var26);
            System.out.println(var22.substring(var26.getStart().getIndex1(), var26.getEnd().getIndex1() + 1));
            System.out.println(seq2.substring(var26.getStart().getIndex2(), var26.getEnd().getIndex2() + 1));
         }
      }

   }
}
