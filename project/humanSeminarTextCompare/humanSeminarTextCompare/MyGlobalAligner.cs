using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace humanSeminarTextCompare
{
    class MyGlobalAligner
    {

        /*static double matchScore = 1;
        static double mismatchScore = -1;
        static double gapScore = -2.5;
        static double gapScoreOpen = gapScore;
        static double gapScoreExtend = -0.2;
        */

        static double matchScore = 1;
        static double mismatchScore = -1;
        
        static double gapScore = 0;
        
        static double gapScoreOpen = 0;
        static double gapScoreExtend = 0;

        static double gapScoreBetween = 0;
        static double gapScoreWithin = -2;
        

        public static Tuple<String, String> globalAlignment(String str1, String str2)
        {
          

            int m = str1.Length;
            int n=str2.Length;
            Cell[,] Matrix = new Cell[m+1, n+1];

            for (int i = 1; i <= m; i++)
            {
                Matrix[i, 0] = new Cell(i, 0, gapScore * i, Matrix[i - 1, 0], Cell.PrevcellType.Above);
            }

            for (int i = 1; i <= n; i++)
            {
                Matrix[0, i] = new Cell(0, i, gapScore * i, Matrix[0, i - 1], Cell.PrevcellType.Left);
            }

            Matrix[0, 0] = new Cell(0, 0, 0, null);

            for (int i = 1; i <= m; i++)
            {
                for (int j = 1; j <= n; j++)
                {
                    char relevantStr1Char = str1[i - 1];
                    char relevantStr2Char = str2[j - 1];

                    double bestScore = double.NegativeInfinity;

                    double gapUpScore = Matrix[i - 1, j].CellScore + gapScore;
                    double gapLeftScore = Matrix[i, j-1].CellScore + gapScore;
                    double diagScore = Matrix[i - 1, j - 1].CellScore;

                    /*if (relevantStr1Char == relevantStr2Char)
                    {
                        diagScore += matchScore;
                    }
                    else
                    {
                        diagScore += mismatchScore;
                    }
                    */

                    diagScore += scorePairChars(relevantStr1Char, relevantStr2Char);

                    if (gapUpScore > bestScore)
                    {
                        bestScore = gapUpScore;
                        Matrix[i, j] = new Cell(i, j, gapUpScore, Matrix[i - 1, j], Cell.PrevcellType.Above);
                    }

                    if (gapLeftScore > bestScore)
                    {
                        bestScore = gapLeftScore;
                        Matrix[i, j] = new Cell(i, j, gapLeftScore, Matrix[i, j-1], Cell.PrevcellType.Left);
                    }

                    if (diagScore > bestScore)
                    {
                        bestScore = diagScore;
                        Matrix[i, j] = new Cell(i, j, diagScore, Matrix[i - 1, j-1], Cell.PrevcellType.Diagonal);
                    }
                }
            }

            String align1 = "";
            String align2 = "";

            Cell currentCell = Matrix[m, n];
            int currentStr1Idx = m-1;
            int currentStr2Idx = n-1;
            char gapChar = '-';
            while (currentCell.CellPointer != null)
            {
                switch (currentCell.Type)
                {
                    case Cell.PrevcellType.Diagonal:
                        align1 = str1[currentStr1Idx] + align1;
                        align2 = str2[currentStr2Idx] + align2;
                        currentStr1Idx--;
                        currentStr2Idx--;
                        break;
                    case Cell.PrevcellType.Above:
                        align1 = str1[currentStr1Idx] + align1;
                        align2 = gapChar + align2;
                        currentStr1Idx--;
                        break;
                    case Cell.PrevcellType.Left:
                        align1 = gapChar + align1;
                        align2 = str2[currentStr2Idx] + align2;
                        currentStr2Idx--;
                        break;
                }

                currentCell = currentCell.CellPointer;
            }

            return new Tuple<String, String>(align1, align2);
        }

        public static Tuple<String, String> globalAlignmentWithAffine(String str1, String str2)
        {
            int m = str1.Length;
            int n = str2.Length;
            Cell[,] M = new Cell[m + 1, n + 1];
            Cell[,] IX = new Cell[m + 1, n + 1];
            Cell[,] IY = new Cell[m + 1, n + 1];

            M[0, 0] = new Cell(0, 0, 0, null);
            IX[0, 0] = new Cell(0, 0, double.NegativeInfinity, null);
            IY[0, 0] = new Cell(0, 0, double.NegativeInfinity, null);

            for (int i = 1; i <= m; i++)
            {
                IX[i, 0] = new Cell(i, 0, gapScoreOpen + (i-1)*gapScoreExtend, IX[i - 1, 0], Cell.PrevcellType.Above);
                IY[i, 0] = new Cell(i, 0, double.NegativeInfinity, null);
                M[i, 0] = new Cell(i, 0, double.NegativeInfinity, null);
            }

            for (int i = 1; i <= n; i++)
            {
                IX[0, i] = new Cell(0, i, double.NegativeInfinity, null);
                IY[0, i] = new Cell(0, i, gapScoreOpen + (i - 1) * gapScoreExtend, IY[0, i - 1], Cell.PrevcellType.Left);
                M[0, i] = new Cell(0, i, double.NegativeInfinity, null);
            }

            
            for (int i = 1; i <= m; i++)
            {
                for (int j = 1; j <= n; j++)
                {
                    char relevantStr1Char = str1[i - 1];
                    char relevantStr2Char = str2[j - 1];

                    double MbestScore = double.NegativeInfinity;

                    double currentMatchScore;

                    currentMatchScore = scorePairChars(relevantStr1Char, relevantStr2Char);

                    /*if (relevantStr1Char == relevantStr2Char)
                    {
                        currentMatchScore = matchScore;
                    }
                    else
                    {
                        currentMatchScore = mismatchScore;
                    }*/

                    M[i, j] = new Cell(i, j, double.NegativeInfinity);

                    if ((M[i - 1, j - 1].CellScore + currentMatchScore) > MbestScore)
                    {
                        MbestScore = M[i - 1, j - 1].CellScore + currentMatchScore;
                        M[i, j] = new Cell(i, j, MbestScore, M[i - 1, j - 1], Cell.PrevcellType.Diagonal);
                    }

                    if (IX[i - 1, j - 1].CellScore + currentMatchScore > MbestScore)
                    {
                        MbestScore = IX[i - 1, j - 1].CellScore + currentMatchScore;
                        M[i, j] = new Cell(i, j, MbestScore, IX[i - 1, j - 1], Cell.PrevcellType.Diagonal);
                    }

                    if (IY[i - 1, j - 1].CellScore + currentMatchScore > MbestScore)
                    {
                        MbestScore = IY[i - 1, j - 1].CellScore + currentMatchScore;
                        M[i, j] = new Cell(i, j, MbestScore, IY[i - 1, j - 1], Cell.PrevcellType.Diagonal);
                    }

                    double IXbestScore = double.NegativeInfinity;

                    IX[i, j] = new Cell(i, j, double.NegativeInfinity);

                    if (M[i - 1, j].CellScore + gapScoreOpen > IXbestScore)
                    {
                        IXbestScore = M[i - 1, j].CellScore + gapScoreOpen;
                        IX[i, j] = new Cell(i, j, IXbestScore, M[i - 1, j], Cell.PrevcellType.Above);
                    }

                    if (IX[i - 1, j].CellScore + gapScoreExtend > IXbestScore)
                    {
                        IXbestScore = IX[i - 1, j].CellScore + gapScoreExtend;
                        IX[i, j] = new Cell(i, j, IXbestScore, IX[i - 1, j], Cell.PrevcellType.Above);
                    }

                    double IYbestScore = double.NegativeInfinity;

                    IY[i, j] = new Cell(i, j, double.NegativeInfinity);

                    if (M[i, j - 1].CellScore + gapScoreOpen > IYbestScore)
                    {
                        IYbestScore = M[i, j - 1].CellScore + gapScoreOpen;
                        IY[i, j] = new Cell(i, j, IYbestScore, M[i, j - 1], Cell.PrevcellType.Left);
                    }

                    if (IY[i, j - 1].CellScore + gapScoreExtend > IYbestScore)
                    {
                        IYbestScore = IY[i, j - 1].CellScore + gapScoreExtend;
                        IY[i, j] = new Cell(i, j, IYbestScore, IY[i, j - 1], Cell.PrevcellType.Left);
                    }
                }
            }

            String align1 = "";
            String align2 = "";

            double bestStartCellScore = int.MinValue;
            Cell currentCell = null;

            if (M[m, n].CellScore > bestStartCellScore)
            {
                bestStartCellScore = M[m, n].CellScore;
                currentCell = M[m, n];
            }

            if (IX[m, n].CellScore > bestStartCellScore)
            {
                bestStartCellScore = IX[m, n].CellScore;
                currentCell = IX[m, n];
            }

            if (IY[m, n].CellScore > bestStartCellScore)
            {
                bestStartCellScore = IY[m, n].CellScore;
                currentCell = IY[m, n];
            }

            int currentStr1Idx = m - 1;
            int currentStr2Idx = n - 1;
            char gapChar = '-';
            while (currentCell.CellPointer != null)
            {
                switch (currentCell.Type)
                {
                    case Cell.PrevcellType.Diagonal:
                        align1 = str1[currentStr1Idx] + align1;
                        align2 = str2[currentStr2Idx] + align2;
                        currentStr1Idx--;
                        currentStr2Idx--;
                        break;
                    case Cell.PrevcellType.Above:
                        align1 = str1[currentStr1Idx] + align1;
                        align2 = gapChar + align2;
                        currentStr1Idx--;
                        break;
                    case Cell.PrevcellType.Left:
                        align1 = gapChar + align1;
                        align2 = str2[currentStr2Idx] + align2;
                        currentStr2Idx--;
                        break;
                }

                currentCell = currentCell.CellPointer;
            }

            return new Tuple<String, String>(align1, align2);
        }

        public static Tuple<String, String> globalAlignmentWithAffineWordLevel(String str1, String str2)
        {
            int m = str1.Length;
            int n = str2.Length;
            Cell[,] M = new Cell[m + 1, n + 1];
            Cell[,] IX = new Cell[m + 1, n + 1];
            Cell[,] IY = new Cell[m + 1, n + 1];

            M[0, 0] = new Cell(0, 0, 0, null);
            IX[0, 0] = new Cell(0, 0, double.NegativeInfinity, null);
            IY[0, 0] = new Cell(0, 0, double.NegativeInfinity, null);

            for (int i = 1; i <= m; i++)
            {
                IX[i, 0] = new Cell(i, 0, gapScoreOpen + (i - 1) * gapScoreExtend, IX[i - 1, 0], Cell.PrevcellType.Above);
                IY[i, 0] = new Cell(i, 0, double.NegativeInfinity, null);
                M[i, 0] = new Cell(i, 0, double.NegativeInfinity, null);
            }

            for (int i = 1; i <= n; i++)
            {
                IX[0, i] = new Cell(0, i, double.NegativeInfinity, null);
                IY[0, i] = new Cell(0, i, gapScoreOpen + (i - 1) * gapScoreExtend, IY[0, i - 1], Cell.PrevcellType.Left);
                M[0, i] = new Cell(0, i, double.NegativeInfinity, null);
            }


            for (int i = 1; i <= m; i++)
            {
                for (int j = 1; j <= n; j++)
                {
                    char relevantStr1Char = str1[i - 1];
                    char relevantStr2Char = str2[j - 1];

                    double MbestScore = double.NegativeInfinity;

                    double currentMatchScore;

                    currentMatchScore = scorePairChars(relevantStr1Char, relevantStr2Char);

                    /*if (relevantStr1Char == relevantStr2Char)
                    {
                        currentMatchScore = matchScore;
                    }
                    else
                    {
                        currentMatchScore = mismatchScore;
                    }*/

                    M[i, j] = new Cell(i, j, double.NegativeInfinity);

                    if ((M[i - 1, j - 1].CellScore + currentMatchScore) > MbestScore)
                    {
                        MbestScore = M[i - 1, j - 1].CellScore + currentMatchScore;
                        M[i, j] = new Cell(i, j, MbestScore, M[i - 1, j - 1], Cell.PrevcellType.Diagonal);
                    }

                    if (relevantStr1Char == ' ')
                    {

                        if (IX[i - 1, j - 1].CellScore + currentMatchScore > MbestScore)
                        {
                            MbestScore = IX[i - 1, j - 1].CellScore + currentMatchScore;
                            M[i, j] = new Cell(i, j, MbestScore, IX[i - 1, j - 1], Cell.PrevcellType.Diagonal);
                        }
                    }

                    if (relevantStr2Char == ' ')
                    {
                        if (IY[i - 1, j - 1].CellScore + currentMatchScore > MbestScore)
                        {
                            MbestScore = IY[i - 1, j - 1].CellScore + currentMatchScore;
                            M[i, j] = new Cell(i, j, MbestScore, IY[i - 1, j - 1], Cell.PrevcellType.Diagonal);
                        }
                    }

                    double IXbestScore = double.NegativeInfinity;

                    IX[i, j] = new Cell(i, j, double.NegativeInfinity);

                    if (relevantStr1Char == ' ')
                    {
                        if (M[i - 1, j].CellScore + gapScoreOpen > IXbestScore)
                        {
                            IXbestScore = M[i - 1, j].CellScore + gapScoreOpen;
                            IX[i, j] = new Cell(i, j, IXbestScore, M[i - 1, j], Cell.PrevcellType.Above);
                        }
                    }

                    if (IX[i - 1, j].CellScore + gapScoreExtend > IXbestScore)
                    {
                        IXbestScore = IX[i - 1, j].CellScore + gapScoreExtend;
                        IX[i, j] = new Cell(i, j, IXbestScore, IX[i - 1, j], Cell.PrevcellType.Above);
                    }

                    double IYbestScore = double.NegativeInfinity;

                    IY[i, j] = new Cell(i, j, double.NegativeInfinity);

                    if (relevantStr2Char == ' ')
                    {
                        if (M[i, j - 1].CellScore + gapScoreOpen > IYbestScore)
                        {
                            IYbestScore = M[i, j - 1].CellScore + gapScoreOpen;
                            IY[i, j] = new Cell(i, j, IYbestScore, M[i, j - 1], Cell.PrevcellType.Left);
                        }
                    }

                    if (IY[i, j - 1].CellScore + gapScoreExtend > IYbestScore)
                    {
                        IYbestScore = IY[i, j - 1].CellScore + gapScoreExtend;
                        IY[i, j] = new Cell(i, j, IYbestScore, IY[i, j - 1], Cell.PrevcellType.Left);
                    }
                }
            }

            String align1 = "";
            String align2 = "";

            double bestStartCellScore = int.MinValue;
            Cell currentCell = null;

            if (M[m, n].CellScore > bestStartCellScore)
            {
                bestStartCellScore = M[m, n].CellScore;
                currentCell = M[m, n];
            }

            if (IX[m, n].CellScore > bestStartCellScore)
            {
                bestStartCellScore = IX[m, n].CellScore;
                currentCell = IX[m, n];
            }

            if (IY[m, n].CellScore > bestStartCellScore)
            {
                bestStartCellScore = IY[m, n].CellScore;
                currentCell = IY[m, n];
            }

            int currentStr1Idx = m - 1;
            int currentStr2Idx = n - 1;
            char gapChar = '-';
            while (currentCell.CellPointer != null)
            {
                switch (currentCell.Type)
                {
                    case Cell.PrevcellType.Diagonal:
                        align1 = str1[currentStr1Idx] + align1;
                        align2 = str2[currentStr2Idx] + align2;
                        currentStr1Idx--;
                        currentStr2Idx--;
                        break;
                    case Cell.PrevcellType.Above:
                        align1 = str1[currentStr1Idx] + align1;
                        align2 = gapChar + align2;
                        currentStr1Idx--;
                        break;
                    case Cell.PrevcellType.Left:
                        align1 = gapChar + align1;
                        align2 = str2[currentStr2Idx] + align2;
                        currentStr2Idx--;
                        break;
                }

                currentCell = currentCell.CellPointer;
            }

            return new Tuple<String, String>(align1, align2);
        }

        public static Tuple<String, String> globalAlignmentWordLevel(String str1, String str2)
        {
          

            int m = str1.Length;
            int n=str2.Length;
            Cell[,] Matrix = new Cell[m+1, n+1];

            Matrix[0, 0] = new Cell(0, 0, 0, null);


            for (int i = 1; i <= m; i++)
            {
                Matrix[i, 0] = new Cell(i, 0, gapScore * i, Matrix[i - 1, 0], Cell.PrevcellType.Above);
            }

            for (int i = 1; i <= n; i++)
            {
                Matrix[0, i] = new Cell(0, i, gapScore * i, Matrix[0, i - 1], Cell.PrevcellType.Left);
            }

            
            for (int i = 1; i <= m; i++)
            {
                for (int j = 1; j <= n; j++)
                {
                    char relevantStr1Char = str1[i - 1];
                    char relevantStr2Char = str2[j - 1];

                    double bestScore = double.NegativeInfinity;
                    double gapUpScore;
                    double gapLeftScore;

                    if ((relevantStr2Char == ' ') || (j == n))
                    {
                        gapUpScore = Matrix[i - 1, j].CellScore + gapScoreBetween;
                    }
                    else
                    {
                        gapUpScore = Matrix[i - 1, j].CellScore + gapScoreWithin;
                    }

                    if ((relevantStr1Char == ' ') || (i == m))
                    {
                        gapLeftScore = Matrix[i, j - 1].CellScore + gapScoreBetween;
                    }
                    else
                    {
                        gapLeftScore = Matrix[i, j - 1].CellScore + gapScoreWithin;
                    }
                    
                   
                    double diagScore = Matrix[i - 1, j - 1].CellScore;

                    /*if (relevantStr1Char == relevantStr2Char)
                    {
                        diagScore += matchScore;
                    }
                    else
                    {
                        diagScore += mismatchScore;
                    }
                    */

                    diagScore += scorePairChars(relevantStr1Char, relevantStr2Char);

                    
                    if (gapUpScore > bestScore)
                    {
                        bestScore = gapUpScore;
                        Matrix[i, j] = new Cell(i, j, gapUpScore, Matrix[i - 1, j], Cell.PrevcellType.Above);
                    }
                    

                
                    if (gapLeftScore > bestScore)
                    {
                        bestScore = gapLeftScore;
                        Matrix[i, j] = new Cell(i, j, gapLeftScore, Matrix[i, j - 1], Cell.PrevcellType.Left);
                    }
                 

                    if (diagScore > bestScore)
                    {
                        bestScore = diagScore;
                        Matrix[i, j] = new Cell(i, j, diagScore, Matrix[i - 1, j-1], Cell.PrevcellType.Diagonal);
                    }
                }
            }

            String align1 = "";
            String align2 = "";

            Cell currentCell = Matrix[m, n];
            int currentStr1Idx = m-1;
            int currentStr2Idx = n-1;
            char gapChar = ' ';
            while (currentCell.CellPointer != null)
            {
                switch (currentCell.Type)
                {
                    case Cell.PrevcellType.Diagonal:
                        align1 = str1[currentStr1Idx] + align1;
                        align2 = str2[currentStr2Idx] + align2;
                        currentStr1Idx--;
                        currentStr2Idx--;
                        break;
                    case Cell.PrevcellType.Above:
                        align1 = str1[currentStr1Idx] + align1;
                        align2 = gapChar + align2;
                        currentStr1Idx--;
                        break;
                    case Cell.PrevcellType.Left:
                        align1 = gapChar + align1;
                        align2 = str2[currentStr2Idx] + align2;
                        currentStr2Idx--;
                        break;
                }

                currentCell = currentCell.CellPointer;
            }

            return new Tuple<String, String>(align1, align2);
        }


        private static double scorePairChars(char c1, char c2)
        {
            if (c1 == c2)
            {
                    return matchScore;
            }
            else
            {
                    return mismatchScore;
            }
        }
    }
}
