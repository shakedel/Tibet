
public class LevenshteinDistance {

   private static int minimum(int a, int b, int c) {
      return Math.min(Math.min(a, b), c);
   }

   public static int computeLevenshteinDistance(CharSequence str1, CharSequence str2) {
      int[][] distance = new int[str1.length() + 1][str2.length() + 1];

      int i;
      for(i = 0; i <= str1.length(); distance[i][0] = i++) {
         ;
      }

      for(i = 1; i <= str2.length(); distance[0][i] = i++) {
         ;
      }

      for(i = 1; i <= str1.length(); ++i) {
         for(int j = 1; j <= str2.length(); ++j) {
            distance[i][j] = minimum(distance[i - 1][j] + 1, distance[i][j - 1] + 1, distance[i - 1][j - 1] + (str1.charAt(i - 1) == str2.charAt(j - 1)?-1:1));
         }
      }

      return distance[str1.length()][str2.length()];
   }
}
