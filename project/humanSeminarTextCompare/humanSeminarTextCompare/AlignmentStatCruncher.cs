using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace humanSeminarTextCompare
{
    class AlignmentStatCruncher
    {
        private Dictionary<Tuple<char, char>, int> charStats;
        private SortedList<char,char> allChars;
        private Dictionary<String, int> text1WordsGap;
        private Dictionary<String, int> text2WordsGap;

        public AlignmentStatCruncher()
        {
            charStats = new Dictionary<Tuple<char, char>, int>();
            
            text1WordsGap = new Dictionary<string, int>();
            text2WordsGap = new Dictionary<string, int>();
            allChars = new SortedList<char, char>();
        }

        public void crunchAlignment(String alignStr1, String alignStr2)
        {
            bool canStartNewWordText1 = true;
            bool canStartNewWordText2 = true;
            bool wordText1EmptyText2 = true;
            bool wordText2EmptyText1 = true;

            String currentWord1 = "";
            String currentWord2 = "";

            for (int i = 0; i < alignStr1.Length; i++)
            {
                char firstChar = alignStr1[i];
                char secondChar = alignStr2[i];

                Tuple<char,char> currentCharStatKey = new Tuple<char, char>(firstChar, secondChar);

                if (allChars.ContainsKey(firstChar) == false)
                {
                    allChars.Add(firstChar, firstChar);
                }

                if (allChars.ContainsKey(secondChar) == false)
                {
                    allChars.Add(secondChar, secondChar);
                }

                if (charStats.ContainsKey(currentCharStatKey))
                {
                    charStats[currentCharStatKey] = charStats[currentCharStatKey] + 1;
                }
                else
                {
                    charStats[currentCharStatKey] = 1;
                }


                // words in text1 that were not found in text 2
                if ((firstChar == '-') || (firstChar == ' ') || (firstChar == ','))
                {
                    if ((currentWord1.Length > 0) && (wordText1EmptyText2))
                    {
                        if (text1WordsGap.ContainsKey(currentWord1))
                        {
                            text1WordsGap[currentWord1] = text1WordsGap[currentWord1] + 1;
                        }
                        else
                        {
                            text1WordsGap[currentWord1] = 1;
                        }
                    }

                    canStartNewWordText1 = true;
                    currentWord1 = "";
                    wordText1EmptyText2 = true;
                }
                else
                {
                    if (canStartNewWordText1)
                    {
                        currentWord1 = currentWord1 + firstChar;

                        if (secondChar != '-')
                        {
                            wordText1EmptyText2 = false;
                        }
                    }
                }

                // words in text2 that were not found in text1

                if ((secondChar == '-') || (secondChar == ' ') || (secondChar == ','))
                {
                    if ((currentWord2.Length > 0) && (wordText2EmptyText1))
                    {
                        if (text2WordsGap.ContainsKey(currentWord2))
                        {
                            text2WordsGap[currentWord2] = text2WordsGap[currentWord2] + 1;
                        }
                        else
                        {
                            text2WordsGap[currentWord2] = 1;
                        }
                    }

                    canStartNewWordText2 = true;
                    currentWord2 = "";
                    wordText2EmptyText1 = true;
                }
                else
                {
                    if (canStartNewWordText2)
                    {
                        currentWord2 = currentWord2 + secondChar;

                        if (firstChar != '-')
                        {
                            wordText2EmptyText1 = false;
                        }
                    }
                }

            
            }

            if ((currentWord1.Length > 0) && (wordText1EmptyText2))
            {
                if (text1WordsGap.ContainsKey(currentWord1))
                {
                    text1WordsGap[currentWord1] = text1WordsGap[currentWord1] + 1;
                }
                else
                {
                    text1WordsGap[currentWord1] = 1;
                }
            }

            if ((currentWord2.Length > 0) && (wordText2EmptyText1))
            {
                if (text1WordsGap.ContainsKey(currentWord2))
                {
                    text2WordsGap[currentWord2] = text2WordsGap[currentWord2] + 1;
                }
                else
                {
                    text2WordsGap[currentWord2] = 1;
                }
            }
        }

        public void export(String resPath)
        {
            System.IO.StreamWriter resfile = new System.IO.StreamWriter(resPath + "table.csv");

            String currentLine = " ";
            foreach (char currentChar in allChars.Keys)
            {
                currentLine += "\t" + currentChar;
            }

            resfile.WriteLine(currentLine);

            foreach (char currentChar in allChars.Keys)
            {
                currentLine = "";
                currentLine += currentChar;

                foreach (char iterChar in allChars.Keys)
                {
                    Tuple<char, char> currentTuple = new Tuple<char, char>(currentChar, iterChar);

                    if (charStats.ContainsKey(currentTuple))
                    {
                        currentLine += "\t" + charStats[currentTuple].ToString();
                    }
                    else
                    {
                        currentLine += "\t0";
                    }
                }

                resfile.WriteLine(currentLine);
            }

            resfile.Close();

            resfile = new System.IO.StreamWriter(resPath + "text1WordsThatWereNotFoundInText2.csv");

            foreach (String key in text1WordsGap.Keys)
            {
                resfile.WriteLine(key + "\t" + text1WordsGap[key].ToString());
            }

            resfile.Close();

            resfile = new System.IO.StreamWriter(resPath + "text2WordsThatWereNotFoundInText1.csv");

            foreach (String key in text2WordsGap.Keys)
            {
                resfile.WriteLine(key + "\t" + text2WordsGap[key].ToString());
            }

            resfile.Close();

        }
    }
}
