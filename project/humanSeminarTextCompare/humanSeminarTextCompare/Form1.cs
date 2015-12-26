using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using IntervalTreeLib;
using iTextSharp.text;
using iTextSharp.text.pdf;
using System.IO;

namespace humanSeminarTextCompare
{
    public partial class Form1 : Form
    {
        private LinkedList<matchResult> matchResults;
        private LinkedList<matchResult> tempResults;
        private IntervalTreeLib.IntervalTree<int, long> firstTextIntervals;
        private IntervalTreeLib.IntervalTree<int, long> secondTextIntervals;
        int currentTempIdx = 0;
        private WaitFrm _waitForm;
        private int bufferSize = 70;

        String cleanStr1 = "";
        String cleanStr2 = "";
        private LinkedList<matchResult> cleanMsatchResults;
        

        protected void ShowWaitForm(string message)
        {
            // don't display more than one wait form at a time
            if (_waitForm != null && !_waitForm.IsDisposed)
            {
                return;
            }

            _waitForm = new WaitFrm();
            //_waitForm.SetMessage(message); // "Loading data. Please wait..."
            _waitForm.TopMost = true;
            _waitForm.StartPosition = FormStartPosition.CenterScreen;
            _waitForm.Show();
            _waitForm.Refresh();

            // force the wait window to display for at least 700ms so it doesn't just flash on the screen
            System.Threading.Thread.Sleep(700);
            Application.Idle += OnLoaded;
        }

        private void OnLoaded(object sender, EventArgs e)
        {
            Application.Idle -= OnLoaded;
            _waitForm.Close();
        }

        public Form1()
        {
            InitializeComponent();
        }

        private void loadMatch(matchResult currentResult, matchResult cleanMatchResult)
        {
            //ShowWaitForm("");
            clearAll(scintilla1);
            clearAll(scintilla2);
            setText(currentResult.first.Item1, currentResult.first.Item2, scintilla1);
            setText(currentResult.second.Item1, currentResult.second.Item2, scintilla2);

            String str1ToAlign = cleanStr1.Substring((int)cleanMatchResult.first.Item1, (int)(cleanMatchResult.first.Item2 - cleanMatchResult.first.Item1 + 1));
            String str2ToAlign = cleanStr2.Substring((int)cleanMatchResult.second.Item1, (int)(cleanMatchResult.second.Item2 - cleanMatchResult.second.Item1 + 1));


            Tuple<String, String> resAlign = computeAlignment(str1ToAlign, str2ToAlign);

            scintilla3.IsReadOnly = false;
            scintilla3.Text = resAlign.Item1;
            scintilla3.Selection.SelectAll();
            ScintillaNET.Range currentRange = scintilla3.Selection.Range;
            currentRange.SetStyle(1);
            scintilla3.Text += "\n" + resAlign.Item2;
            scintilla3.Selection.SelectAll();
            ScintillaNET.Range newRange = scintilla3.Selection.Range;
            newRange.Start = currentRange.End + 1;
            currentRange.SetStyle(1);
            newRange.SetStyle(2);
            
            scintilla3.Refresh();
            ScintillaNET.Range r1 = new ScintillaNET.Range((int)resAlign.Item1.Length - 2, (int)resAlign.Item1.Length-1, scintilla3);
            r1.Select();  
            scintilla3.Caret.Goto(0);
            scintilla3.Scrolling.ScrollToCaret();
            scintilla3.IsReadOnly = true;
            scoreStripLabel.Text = "Score: " + (-currentResult.score).ToString();
            toolStripComboBox1.Text = currentResult.id.ToString();
        }

        private void clearAll(ScintillaNET.Scintilla sci)
        {
            sci.Selection.SelectAll();
            sci.Selection.Range.SetStyle(0);
            sci.Selection.SelectNone();
            setUnderline();
        }

        private void setUnderline()
        {
            for (int i = 0; i < matchResults.Count; i++)
            {
                matchResult currentMatch = matchResults.ElementAt(i);
                var r1 = new ScintillaNET.Range((int)currentMatch.first.Item1, (int)currentMatch.first.Item2, scintilla1);
                var r2 = new ScintillaNET.Range((int)currentMatch.second.Item1, (int)currentMatch.second.Item2, scintilla2);

                r1.SetStyle(2);
                r2.SetStyle(2);

                ScintillaNET.Marker marker1 = scintilla1.Markers[2];
                marker1.BackColor = System.Drawing.Color.AliceBlue;
                marker1.Symbol = ScintillaNET.MarkerSymbol.RoundRectangle;

                ScintillaNET.Marker marker2 = scintilla2.Markers[2];
                marker2.BackColor = System.Drawing.Color.AliceBlue;
                marker2.Symbol = ScintillaNET.MarkerSymbol.RoundRectangle;

                for (int j = r1.StartingLine.Number; j <= r1.EndingLine.Number; j++)
                {
                    scintilla1.Lines[j].AddMarker(marker1);
                }

                for (int j = r2.StartingLine.Number; j <= r2.EndingLine.Number; j++)
                {
                    scintilla2.Lines[j].AddMarker(marker2);
                }
            }
        }

        private void setText(Int64 begin, Int64 end, ScintillaNET.Scintilla sci)
        {
            int q = sci.Lines.VisibleCount;
            
            var r = new ScintillaNET.Range((int)begin, (int)end, sci);

            sci.Caret.Goto((int)begin);
            sci.Caret.LineNumber = r.StartingLine.Number +(q / 2);


            r.SetStyle(1);
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            double g = double.NegativeInfinity - 1;
            Tuple<String, String> strAlign = MyGlobalAligner.globalAlignmentWordLevel("du","dang du"); ;
            //Tuple<String, String> strAlign = MyGlobalAligner.globalAlignmentWordLevel("dang du la gu", "dang du gu"); ;

            String strrrr1 = strAlign.Item1;
            String strrrr2 = strAlign.Item2;

            System.IO.StreamReader iFile = new System.IO.StreamReader("1.txt");
            string str1 = iFile.ReadToEnd();
            iFile.Close();

            bool[] str1Busy = new bool[str1.Length];
            for (int i = 0; i < str1.Length; i++)
            {
                str1Busy[i] = false;
            }

            iFile = new System.IO.StreamReader("2.txt");
            string str2 = iFile.ReadToEnd();
            iFile.Close();

            bool[] str2Busy = new bool[str2.Length];
            for (int i = 0; i < str2.Length; i++)
            {
                str2Busy[i] = false;
            }

            iFile = new System.IO.StreamReader("1Clean.txt");
            cleanStr1 = iFile.ReadToEnd();
            iFile.Close();


            iFile = new System.IO.StreamReader("2Clean.txt");
            cleanStr2 = iFile.ReadToEnd();
            iFile.Close();

            matchResults = matchResult.loadFromFile("matches.txt");
            cleanMsatchResults = matchResult.loadFromFile("matchesClean.txt");

            scintilla1.Margins[0].Width = 90;
            scintilla2.Margins[0].Width = 90;

            
            for (int i = 0; i < matchResults.Count; i++)
            {
                for (long j = matchResults.ElementAt(i).first.Item1; j < matchResults.ElementAt(i).first.Item2; j++)
                {
                    str1Busy[j] = true;
                }

                for (long j = matchResults.ElementAt(i).second.Item1; j < matchResults.ElementAt(i).second.Item2; j++)
                {
                    str2Busy[j] = true;
                }

                toolStripComboBox1.Items.Add(i); 
            }

            double totalBusy1 = 0.0;
            double totalBusy2 = 0.0;

            for (int i = 0; i < str2.Length; i++)
            {
                if (str2Busy[i])
                {
                    totalBusy2++;
                }
            }

            for (int i = 0; i < str1.Length; i++)
            {
                if (str1Busy[i])
                {
                    totalBusy1++;
                }
            }

            double busyPercent1 = totalBusy1 / str1.Length;
            double busyPercent2 = totalBusy2 / str2.Length;

            string stBusy1 = busyPercent1.ToString();
            string stBusy2 = busyPercent2.ToString();


            firstTextIntervals = new IntervalTree<int, long>();
            secondTextIntervals = new IntervalTree<int, long>();
            
            for(int i=0; i<matchResults.Count; i++)
            {
                matchResult item = matchResults.ElementAt(i);
                firstTextIntervals.AddInterval(item.first.Item1, item.first.Item2, i);
                secondTextIntervals.AddInterval(item.second.Item1, item.second.Item2, i);
            }

            firstTextIntervals.Build();
            secondTextIntervals.Build();

            scintilla1.Styles[1].ForeColor = Color.Red;
            scintilla1.Styles[1].Font = new System.Drawing.Font(scintilla1.Font, FontStyle.Bold);

            scintilla2.Styles[1].ForeColor = Color.Blue;
            scintilla2.Styles[1].Font = new System.Drawing.Font(scintilla2.Font, FontStyle.Bold);

            scintilla1.Styles[2].Font = new System.Drawing.Font(scintilla1.Font, FontStyle.Bold);
            scintilla2.Styles[2].Font = new System.Drawing.Font(scintilla1.Font, FontStyle.Bold);

            scintilla3.Styles[1].ForeColor = Color.Red;
            scintilla3.Styles[1].Font = new System.Drawing.Font(scintilla3.Font, FontStyle.Bold);
            scintilla3.Styles[2].ForeColor = Color.Blue;
            scintilla3.Styles[2].Font = new System.Drawing.Font(scintilla3.Font, FontStyle.Bold);

            scintilla1.Text = str1;
            scintilla1.IsReadOnly = true;
            
            scintilla2.Text = str2;
            scintilla2.IsReadOnly = true;

            scintilla3.IsReadOnly = true;

            setUnderline();

            Dictionary<int, int> firstTextLinksHist = new Dictionary<int, int>();
            Dictionary<int, int> secondTextLinksHist = new Dictionary<int, int>();
            Dictionary<int, int> overAllHist = new Dictionary<int, int>();
            
            List<Tuple<long, int>> lengthNumMatches = new List<Tuple<long, int>>();
            List<Tuple<long, int>> lengthNumMatches1 = new List<Tuple<long, int>>();
            List<Tuple<long, int>> lengthNumMatches2 = new List<Tuple<long, int>>();

            List<int> tempLarge = new List<int>();

            foreach (matchResult iMatch in matchResults)
            {
                List<int> results = firstTextIntervals.Get(iMatch.first.Item1, iMatch.first.Item2);
                int cKey = results.Count;

                int a = 0;
                if (cKey >= 50)
                {
                    a++;
                    tempLarge.Add(iMatch.id);
                }

                lengthNumMatches.Add(new Tuple<long,int>((iMatch.first.Item2 - iMatch.first.Item1), cKey));
                lengthNumMatches1.Add(new Tuple<long, int>((iMatch.first.Item2 - iMatch.first.Item1), cKey));

                if (firstTextLinksHist.ContainsKey(cKey))
                {
                    firstTextLinksHist[cKey] = firstTextLinksHist[cKey] + 1;
                }
                else
                {
                    firstTextLinksHist[cKey] = 1;
                }

                if (overAllHist.ContainsKey(cKey))
                {
                    overAllHist[cKey] = overAllHist[cKey] + 1;
                }
                else
                {
                    overAllHist[cKey] = 1;
                }
            }

            foreach (matchResult iMatch in matchResults)
            {
                List<int> results = secondTextIntervals.Get(iMatch.second.Item1, iMatch.second.Item2);
                int cKey = results.Count;

                lengthNumMatches.Add(new Tuple<long, int>((iMatch.second.Item2 - iMatch.second.Item1), cKey));
                lengthNumMatches2.Add(new Tuple<long, int>((iMatch.second.Item2 - iMatch.second.Item1), cKey));

                if (secondTextLinksHist.ContainsKey(cKey))
                {
                    secondTextLinksHist[cKey] = secondTextLinksHist[cKey] + 1;
                }
                else
                {
                    secondTextLinksHist[cKey] = 1;
                }

                if (overAllHist.ContainsKey(cKey))
                {
                    overAllHist[cKey] = overAllHist[cKey] + 1;
                }
                else
                {
                    overAllHist[cKey] = 1;
                }
            }

            System.IO.StreamWriter linksHist1File = new System.IO.StreamWriter("C:\\itext\\linksHist1.csv");
            System.IO.StreamWriter linksHist2File = new System.IO.StreamWriter("C:\\itext\\linksHist2.csv");
            System.IO.StreamWriter linksHistAllFile = new System.IO.StreamWriter("C:\\itext\\linksHistBoth.csv");
            
            System.IO.StreamWriter lengthMatch = new System.IO.StreamWriter("C:\\itext\\lengthMatch.csv");
            System.IO.StreamWriter lengthMatch1 = new System.IO.StreamWriter("C:\\itext\\lengthMatch1.csv");
            System.IO.StreamWriter lengthMatch2 = new System.IO.StreamWriter("C:\\itext\\lengthMatch2.csv");


            foreach (int cKey in firstTextLinksHist.Keys)
            {
                linksHist1File.WriteLine(cKey.ToString() + ";" + firstTextLinksHist[cKey].ToString());
            }

            foreach (int cKey in secondTextLinksHist.Keys)
            {
                linksHist2File.WriteLine(cKey.ToString() + ";" + secondTextLinksHist[cKey].ToString());
            }

            foreach (int cKey in overAllHist.Keys)
            {
                linksHistAllFile.WriteLine(cKey.ToString() + ";" + overAllHist[cKey].ToString());
            }

            foreach (Tuple<long, int> currentTupple in lengthNumMatches)
            {
                lengthMatch.WriteLine(currentTupple.Item1.ToString() + ";" + currentTupple.Item2.ToString());
            }

            foreach (Tuple<long, int> currentTupple in lengthNumMatches1)
            {
                lengthMatch1.WriteLine(currentTupple.Item1.ToString() + ";" + currentTupple.Item2.ToString());
            }

            foreach (Tuple<long, int> currentTupple in lengthNumMatches2)
            {
                lengthMatch2.WriteLine(currentTupple.Item1.ToString() + ";" + currentTupple.Item2.ToString());
            }

            linksHist1File.Close();
            linksHist2File.Close();
            lengthMatch.Close();
            lengthMatch1.Close();
            lengthMatch2.Close();
            linksHistAllFile.Close();

        }

        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            //try
            {
                int id = int.Parse(toolStripComboBox1.Text.ToString());

                if ((id >= 0) && (id < matchResults.Count))
                {
                    disableFindTextMatchBtns();
                    matchResult currentMatchResult = matchResults.ElementAt(id);
                    loadMatch(currentMatchResult, cleanMsatchResults.ElementAt(id));
                }
            }
           // catch (Exception exp)
            {
                
            }
        }

        private void toolStrip3_ItemClicked(object sender, ToolStripItemClickedEventArgs e)
        {

        }

        private void findResults(int id)
        {
            currentTempIdx = 0;

            ScintillaNET.Scintilla sci;
            List<int> results;

            if (id == 1)
            {
                sci = scintilla1;
                results = firstTextIntervals.Get(sci.Selection.Start, sci.Selection.End);

            }
            else
            {
                sci = scintilla2;
                results = secondTextIntervals.Get(sci.Selection.Start, sci.Selection.End);
            }
                
            tempResults = new LinkedList<matchResult>();

            foreach (int currentId in results)
            {
                tempResults.AddLast(matchResults.ElementAt(currentId));
            }

            disableFindTextMatchBtns();

            if (results.Count != 0)
            {
                matchResult currentMatchResult = tempResults.ElementAt(0);
                loadMatch(currentMatchResult, cleanMsatchResults.ElementAt(currentMatchResult.id));
                enableFindTextMatchBtns(id);
            }      
        }

        private void disableFindTextMatchBtns()
        {
            firstBackBtn.Enabled = false;
            firstFwdBtn.Enabled = false;
            firstResultCount.Enabled = false;
            firstResultCount.Text = "0 / 0";
           
            secondBackBtn.Enabled = false;
            secondFwdBtn.Enabled = false;
            secondResultCount.Enabled = false;
            secondResultCount.Text = "0 / 0";
            scoreStripLabel.Text = "";
        }

        private void enableFindTextMatchBtns(int id)
        {
            if (id == 1)
            {
                firstBackBtn.Enabled = false;

                if (tempResults.Count > 1)
                {
                    firstFwdBtn.Enabled = true;
                }

                firstResultCount.Enabled = true;
                firstResultCount.Text = "1 / " + tempResults.Count.ToString();
            }
            else
            {
                secondBackBtn.Enabled = false;

                if (tempResults.Count > 1)
                {
                    secondFwdBtn.Enabled = true;
                }

                secondResultCount.Enabled = true;
                secondResultCount.Text = "1 / " + tempResults.Count.ToString();
            }
        }

        private void toolStripButton2_Click(object sender, EventArgs e)
        {
            findResults(1);
        }

        private Tuple<String,String> computeAlignment(String str1, String str2)
        {
            //str1 = str1.Replace("\n", " ");
            //str2 = str2.Replace("\n", " ");
            //str1 = str1.Replace("\r", " ");
            //str2 = str2.Replace("\r", " ");
            
            str1 = str1.Replace(",","");
            str2 = str2.Replace(",", "");


            //List<char> SeqAlign1 = new List<char>();
            //List<char> SeqAlign2 = new List<char>();
            //Cell[,] Matrix = DynamicProgramming.Intialization_Step(str1, str2, 1, -1, -1);
            //DynamicProgramming.Traceback_Step(Matrix, str1, str2, SeqAlign1, SeqAlign2);
            //String str1Aligned = "";
            //String str2Aligned = "";


            //for (int j = SeqAlign1.Count - 1; j >= 0; j--)
            //{
            //    str1Aligned = str1Aligned + (SeqAlign1[j].ToString());
            //}

            //for (int j = SeqAlign2.Count - 1; j >= 0; j--)
            //{
            //    str2Aligned = str2Aligned + (SeqAlign2[j].ToString());
            //}

           // MessageBox.Show(str1Aligned);
           // MessageBox.Show(str2Aligned);
            Tuple<String, String> strAlign = MyGlobalAligner.globalAlignmentWordLevel(str1, str2); ;
            return strAlign;
        }

        private String getSubText(int id, matchResult result)
        {
            if (id == 1)
            {
                return scintilla1.Text.Substring((int)(result.first.Item1), (int)(result.first.Item2 - result.first.Item1));
            }
            else
            {
                return scintilla2.Text.Substring((int)(result.second.Item1), (int)(result.second.Item2 - result.second.Item1));
            }
        }

        private void secondMatchBtn_Click(object sender, EventArgs e)
        {
            findResults(2);
        }

        private void firstBackBtn_Click(object sender, EventArgs e)
        {
            currentTempIdx = currentTempIdx - 1;
            matchResult currentMatchResult = tempResults.ElementAt(currentTempIdx);
            loadMatch(currentMatchResult, cleanMsatchResults.ElementAt(currentMatchResult.id));

            firstFwdBtn.Enabled = true;
            firstResultCount.Text = (currentTempIdx + 1).ToString() + " / " + tempResults.Count.ToString();

            firstBackBtn.Enabled = (currentTempIdx != 0);
        }

        private void firstFwdBtn_Click(object sender, EventArgs e)
        {
            currentTempIdx = currentTempIdx + 1;
            matchResult currentMatchResult = tempResults.ElementAt(currentTempIdx);
            loadMatch(currentMatchResult, cleanMsatchResults.ElementAt(currentMatchResult.id));

            firstBackBtn.Enabled = true;
            firstResultCount.Text = (currentTempIdx + 1).ToString() + " / " + tempResults.Count.ToString();

            firstFwdBtn.Enabled = (currentTempIdx != (tempResults.Count - 1));
        }

        private void secondBackBtn_Click(object sender, EventArgs e)
        {
            currentTempIdx = currentTempIdx - 1;
            matchResult currentMatchResult = tempResults.ElementAt(currentTempIdx);
            loadMatch(currentMatchResult, cleanMsatchResults.ElementAt(currentMatchResult.id));

            secondFwdBtn.Enabled = true;
            secondResultCount.Text = (currentTempIdx + 1).ToString() + " / " + tempResults.Count.ToString();

            secondBackBtn.Enabled = (currentTempIdx != 0);
        }

        private void secondFwdBtn_Click(object sender, EventArgs e)
        {
            currentTempIdx = currentTempIdx + 1;
            matchResult currentMatchResult = tempResults.ElementAt(currentTempIdx);
            loadMatch(currentMatchResult, cleanMsatchResults.ElementAt(currentMatchResult.id));

            secondBackBtn.Enabled = true;
            secondResultCount.Text = (currentTempIdx + 1).ToString() + " / " + tempResults.Count.ToString();

            secondFwdBtn.Enabled = (currentTempIdx != (tempResults.Count - 1));
        }

        private void createResultsPdf(Interval<int,int> resIntervals, String pdfName)
        {
            AlignmentStatCruncher statAlignments = new AlignmentStatCruncher();
            Document doc = new Document(PageSize.A4);

            int extraBeforeAndAfter = 150;

            var output = new FileStream(@"C:\itext\3.pdf", FileMode.Create);
            var writer = PdfWriter.GetInstance(doc, output);


            doc.Open();

            iTextSharp.text.Font contentFont1 = FontFactory.GetFont(FontFactory.TIMES, 14, iTextSharp.text.Font.NORMAL);
            iTextSharp.text.Font contentFont2 = FontFactory.GetFont(FontFactory.TIMES, 12, iTextSharp.text.Font.NORMAL);


            iTextSharp.text.Font contentsFont = FontFactory.GetFont(FontFactory.TIMES_BOLD, 14, iTextSharp.text.Font.UNDERLINE);
            iTextSharp.text.Font linkFont = FontFactory.GetFont(FontFactory.TIMES, 12, iTextSharp.text.Font.UNDERLINE);
            linkFont.Color = BaseColor.BLUE;

            Chunk cContents = new Chunk("Contents:", contentsFont);
            doc.Add(new Paragraph(cContents));
            doc.Add(Chunk.NEWLINE);

            for (int i = resIntervals.Start; i < resIntervals.End; i++)
            {
                Chunk cLink = new Chunk("Result #" + i.ToString(), linkFont);
                cLink.SetLocalGoto(i.ToString());
                doc.Add(new Paragraph(cLink));
            }

            doc.NewPage();

            for (int i = resIntervals.Start; i < resIntervals.End; i++)
            {
                matchResult currentResult = matchResults.ElementAt(i);
                Chunk c1 = new Chunk("Result #" + i.ToString(), contentFont1);
                Chunk c2 = new Chunk("Text1 Position:" + " (" + currentResult.first.Item1 + " , " + currentResult.first.Item2 + ")", contentFont2);
                Chunk c3 = new Chunk("Text2 Position:" + " (" + currentResult.second.Item1 + " , " + currentResult.second.Item2 + ")", contentFont2);
                List<String> pageMarksText1 = findPageMarks(getSubText(1, currentResult));
                List<String> pageMarksText2 = findPageMarks(getSubText(2, currentResult));
                String strPageMarksText1 = "";
                String strPageMarksText2 = "";

                foreach (String pageMark in pageMarksText1)
                {
                    if (strPageMarksText1 == "")
                    {
                        strPageMarksText1 = pageMark;
                    }
                    else
                    {
                        strPageMarksText1 += " " + pageMark;
                    }
                }

                foreach (String pageMark in pageMarksText2)
                {
                    if (strPageMarksText2 == "")
                    {
                        strPageMarksText2 = pageMark;
                    }
                    else
                    {
                        strPageMarksText2 += " " + pageMark;
                    }
                }

                c1.SetLocalDestination(i.ToString());
                doc.Add(new Paragraph(c1));
                //doc.Add(Chunk.NEWLINE);
                doc.Add(new Paragraph(c2));
                //doc.Add(Chunk.NEWLINE);
                doc.Add(new Paragraph(c3));

                if (pageMarksText1.Count == 0)
                {
                    strPageMarksText1 = "None";
                }

                if (pageMarksText2.Count == 0)
                {
                    strPageMarksText2 = "None";
                }
                
                Chunk c4 = new Chunk("Text1 - Page Numbers Found: " + strPageMarksText1, contentFont2);
                doc.Add(new Paragraph(c4));

                Chunk c5 = new Chunk("Text2 - Page Numbers Found: " + strPageMarksText2, contentFont2);
                doc.Add(new Paragraph(c5));

                matchResult currentCleanMatchResult = cleanMsatchResults.ElementAt(currentResult.id);

                //doc.Add(Chunk.NEWLINE);

                long beforeFirstEnd = currentCleanMatchResult.first.Item1 - 1;
                long beforeFirstStart = beforeFirstEnd - extraBeforeAndAfter;

                long beforeSecondEnd = currentCleanMatchResult.second.Item1 - 1;
                long beforeSecondStart = beforeSecondEnd - extraBeforeAndAfter;

                
                if ((beforeFirstStart > 0) && (beforeSecondStart > 0))
                {
                    doc.Add(Chunk.NEWLINE);
                    doc.Add(new Paragraph("Before The Match:", contentFont2));
                    doc.Add(Chunk.NEWLINE);
                    String beforeText1 = cleanStr1.Substring((int)(beforeFirstStart), (int)(beforeFirstEnd - beforeFirstStart + 1));
                    String beforeText2 = cleanStr2.Substring((int)(beforeSecondStart), (int)(beforeSecondEnd - beforeSecondStart + 1));
                    Tuple<String, String> beforeResAlign = computeAlignment(beforeText1, beforeText2);
                    addMatch(beforeResAlign.Item1, beforeResAlign.Item2, doc, new BaseColor(255, 0, 255), new BaseColor(0, 180, 255));
                }

                doc.Add(new Paragraph("The Match:", contentFont2));
                doc.Add(Chunk.NEWLINE);

                String str1ToAlign = cleanStr1.Substring((int)currentCleanMatchResult.first.Item1, (int)(currentCleanMatchResult.first.Item2 - currentCleanMatchResult.first.Item1));
                String str2ToAlign = cleanStr2.Substring((int)currentCleanMatchResult.second.Item1, (int)(currentCleanMatchResult.second.Item2 - currentCleanMatchResult.second.Item1));

                Tuple<String, String> resAlign = computeAlignment(str1ToAlign, str2ToAlign);

                statAlignments.crunchAlignment(resAlign.Item1, resAlign.Item2);
                addMatch(resAlign.Item1, resAlign.Item2, doc, BaseColor.RED, BaseColor.BLUE);

                long afterFirstStart = currentCleanMatchResult.first.Item2;
                long afterFirstEnd = afterFirstStart + extraBeforeAndAfter;

                long afterSecondStart = currentCleanMatchResult.second.Item2;
                long afterSecondEnd = afterSecondStart + extraBeforeAndAfter;

                doc.Add(Chunk.NEWLINE);


                if ((afterFirstEnd < cleanStr1.Length) && (afterSecondStart < cleanStr2.Length))
                {
                    doc.Add(new Paragraph("After The Match:", contentFont2));
                    doc.Add(Chunk.NEWLINE);
                    String afterText1 = cleanStr1.Substring((int)(afterFirstStart), (int)(afterFirstEnd - afterFirstStart + 1));
                    String afterText2 = cleanStr2.Substring((int)(afterSecondStart), (int)(afterSecondEnd - afterSecondStart + 1));
                    Tuple<String, String> beforeResAlign = computeAlignment(afterText1, afterText2);
                    addMatch(beforeResAlign.Item1, beforeResAlign.Item2, doc, new BaseColor(255, 0, 255), new BaseColor(0, 180, 255));
                }

               
                doc.NewPage();
            }

            doc.Close();   
            statAlignments.export(@"C:\itext\");
        }

        private void addMatch(String text1, String text2, Document doc, BaseColor text1Color, BaseColor text2Color)
        {
            int currentPtr = 0;
            //MessageBox.Show(FontFactory.COURIER);
            int textLength = text1.Length;

            iTextSharp.text.Font contentFontRed = FontFactory.GetFont(FontFactory.COURIER, 12, iTextSharp.text.Font.NORMAL);
            contentFontRed.Color = text1Color;
            iTextSharp.text.Font contentFontBlue = FontFactory.GetFont(FontFactory.COURIER, 12, iTextSharp.text.Font.NORMAL);
            contentFontBlue.Color = text2Color;

            while (currentPtr < textLength)
            {
                String currentStr1 = text1.Substring(currentPtr, Math.Min(bufferSize, textLength - currentPtr));
                String currentStr2 = text2.Substring(currentPtr, Math.Min(bufferSize, textLength - currentPtr));
                currentPtr += bufferSize;
                Chunk p1 = new Chunk(currentStr1, contentFontRed);
                Chunk p2 = new Chunk(currentStr2, contentFontBlue);

                Paragraph pNew = new Paragraph();
                pNew.Add(p1);
                pNew.Add(Chunk.NEWLINE);
                pNew.Add(p2);
                pNew.Add(Chunk.NEWLINE);
                pNew.Add(Chunk.NEWLINE);
                pNew.KeepTogether = true;
                doc.Add(pNew);
                //doc.Add(new Paragraph(p1));
                //doc.Add(new Paragraph(p2));
                //doc.Add(Chunk.NEWLINE);
            }

            //MessageBox.Show("ttt");
         
        }

        private void toolStripButton2_Click_1(object sender, EventArgs e)
        {
            createResultsPdf(new Interval<int,int>(0,3,-1), "1");
        }

        public List<String> findPageMarks(String str)
        {
            List<string> found = new List<string>();
            foreach (var item in str.Split(' '))
            {
                if (item.Contains("@"))
                {
                    int startIdx = item.IndexOf('@');
                    found.Add(item.Substring(startIdx));
                }
            }

            return found;
        }

        private void toolStrip1_ItemClicked(object sender, ToolStripItemClickedEventArgs e)
        {

        }


    }
}
