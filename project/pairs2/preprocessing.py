f=open(r"C:\Users\Daniel\Documents\University\tesis\project\humanSeminarTextCompare\humanSeminarTextCompare\bin\x64\Release\1.txt","rb")
b=f.read()
#b=a.replace("\r\n"," ").replace("||"," ").replace("|"," ")
#b[0:100]
import re
#c=re.sub(' +',' ',b)

reStr = '( )+(\|( )+\r\n( )+\|)( )+|( )+(\|)+( )+|(\r\n)+|\r|\n| +'

q=re.finditer(reStr,b)
counter=0
mapToOriginal = dict()

while (True):
    try:
        currentMatch = q.next()
        originalStartIdx = currentMatch.start()
        originalEndIdx = currentMatch.end()
        delta=(originalEndIdx - originalStartIdx - 1)
        newRelevantIdx = originalEndIdx - delta - counter
        if (delta  > 0):
            counter = counter + delta
            mapToOriginal[newRelevantIdx] = originalEndIdx
    except StopIteration:
        break


c=re.sub(reStr,' ',b)
c=c.replace("\x92","'")
q=open(r"C:\Users\Daniel\Documents\University\tesis\project\humanSeminarTextCompare\humanSeminarTextCompare\bin\x64\Release\1Clean.txt","wb")
q.write(c)
q.close()

import cPickle
f=open(r"C:\Users\Daniel\Documents\University\tesis\project\humanSeminarTextCompare\humanSeminarTextCompare\bin\x64\Release\transDict1.pic","wb")
cPickle.dump(mapToOriginal,f)
f.close()
