c:\python27\python.exe matchDani.py --inFileA "C:\Users\Daniel\Documents\Univesity\tesis\project\texts\1.txt" --inFileB "C:\Users\Daniel\Documents\Univesity\tesis\project\texts\2.txt" --outFile testMatchPyOut.txt --minDistanceMatch 15 --maxErrorMatch 4 --minDistanceUnion 10 --localAlignPadRatio 0.12

Traceback (most recent call last):
  File "matchDani.py", line 25, in <module>
    postprocess(headerPath, args.outFile, minDistanceUnion, localAlignPadRatio)
  File "postProcessResultsDani.py", line 12, in postprocess
    localAlign(firstInputFilePath, secondInputFilePath, firstUnionPath, outResultsPath, alignPadRatio)
  File "localalign.py", line 48, in localAlign
    [newStartOne,newStartTwo,newEndOne,newEndTwo,score] = water(t1[startOne:endOne],t2[startTwo:endTwo])
  File "waterLocalAlign.py", line 150, in water
    i,j = max_i,max_j    # indices of path starting point
UnboundLocalError: local variable 'max_i' referenced before assignment



C:\Users\Daniel\Documents\Univesity\tesis\project\texts to match\Byang chub kyi sems bsgom pa - P.txt
C:\Users\Daniel\Documents\Univesity\tesis\project\texts to match\kun byed rgyal po e text-OA.txt


c:\python27\python.exe match.py --inFileA "C:\Users\Daniel\Documents\Univesity\tesis\project\texts\1.txt" --inFileB "C:\Users\Daniel\Documents\Univesity\tesis\project\texts\2.txt" --outFile testMatchPyOut2.txt --minDistanceMatch 60 --maxErrorMatch 13 --minDistanceUnion 10 --localAlignPadRatio 0.12
התחלת ריצה 19:53

c:\python27\python.exe matchAlign.py --inFileA "C:\Users\Daniel\Documents\Univesity\tesis\project\texts\1.txt" --inFileB "C:\Users\Daniel\Documents\Univesity\tesis\project\texts\2.txt" --outFile Results\testMatchPyOut2.txt --minDistanceMatch 20 --maxErrorMatch 5 --minDistanceUnion 10 --localAlignPadRatio 0.12

C:\Users\Daniel\Documents\Univesity\tesis\project\pairs2>c:\python27\python.exe matchUnion.py --inFileA "C:\Users\Daniel\Documents\Univesity\tesis\project\texts\1.txt" --inFileB "C:\Users\Daniel\Documents\Univesity\tesis\project\texts\2.txt" --outFile Results\testMatchPyOut7.txt --minDistanceMatch 20 --maxErrorMatch 5 --minDistanceUnion 10 --localAlignPadRatio 0.3

3:00 התחלת ריצה: