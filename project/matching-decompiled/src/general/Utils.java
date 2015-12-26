package general;


public class Utils {

   public static int max(int first, int second) {
      return first > second?first:second;
   }

   public static int min(int first, int second) {
      return first < second?first:second;
   }

   public static int min(int first, int second, int third) {
      int ret = first;
      if(second < first) {
         ret = second;
      }

      if(third < ret) {
         ret = third;
      }

      return ret;
   }

   public static void printEDMatrix(int[][] matrix, char[] s1, char[] s2) {
      System.out.print("\t\t");

      int i;
      for(i = 0; i < matrix[0].length - 1; ++i) {
         System.out.print(s2[i] + "\t");
      }

      System.out.print("\n");

      for(i = 0; i < matrix.length; ++i) {
         if(i > 0) {
            System.out.print(s1[i - 1] + "\t");
         } else {
            System.out.print("\t");
         }

         if(matrix[i] != null) {
            for(int j = 0; j < matrix[i].length; ++j) {
               System.out.print(matrix[i][j] + "\t");
            }
         } else {
            System.out.print("null");
         }

         System.out.print("\n");
      }

   }

   public static void printEDMatrix(int[][] matrix) {
      for(int i = 0; i < matrix.length; ++i) {
         if(matrix[i] != null) {
            for(int j = 0; j < matrix[i].length; ++j) {
               System.out.print(matrix[i][j] + "\t");
            }
         } else {
            System.out.print("null");
         }

         System.out.print("\n");
      }

   }

   public static void printEDMatrix(Integer[][] matrix) {
      for(int i = 0; i < matrix.length; ++i) {
         for(int j = 0; j < matrix[i].length; ++j) {
            System.out.print(matrix[i][j] + "\t");
         }

         System.out.print("\n");
      }

   }

   public static void printMatchingMatrix(boolean[][] matr) {
      for(int i = 0; i < matr.length; ++i) {
         for(int j = 0; j < matr[i].length; ++j) {
            if(matr[i][j]) {
               System.out.print("1\t");
            } else {
               System.out.print("0\t");
            }
         }

         System.out.println("\n");
      }

   }

   public static int edScore(char first, char second) {
      return first == second?0:1;
   }
}
