using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;

namespace humanSeminarTextCompare
{
    class matchResult
    {
        public double score;
        public Tuple<Int64, Int64> first;
        public Tuple<Int64, Int64> second;
        public int id;
        public matchResult(double score, Tuple<Int64, Int64> first, Tuple<Int64, Int64> second, int id)
        {
            this.score = score;
            this.first = first;
            this.second = second;
            this.id = id;
        }

        public static LinkedList<matchResult> loadFromFile(String path)
        {
            String[] data = File.ReadAllLines(path);
            LinkedList<matchResult> allResults = new LinkedList<matchResult>();


            for (int i = 0; i < data.Length; i++)
            {
                
                String[] fields = data[i].Split(',');
                matchResult currentMatch = new matchResult(Double.Parse(fields[4]), new Tuple<Int64, Int64>(Int64.Parse(fields[0]), Int64.Parse(fields[2])), new Tuple<Int64, Int64>(Int64.Parse(fields[1]), Int64.Parse(fields[3])),i);
                allResults.AddLast(currentMatch);
            }

            return allResults;
        }
    }
}
