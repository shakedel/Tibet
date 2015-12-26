import cPickle
import bisect
import numpy

f = open(r'C:\Users\Daniel\Documents\University\tesis\project\humanSeminarTextCompare\humanSeminarTextCompare\bin\x64\Release\transDict1.pic','rb')
dict1=cPickle.load(f)
f.close()
f = open(r'C:\Users\Daniel\Documents\University\tesis\project\humanSeminarTextCompare\humanSeminarTextCompare\bin\x64\Release\transDict2.pic','rb')
dict2=cPickle.load(f)
f.close()

sortedKeys1 = dict1.keys()
sortedKeys1.sort()

sortedKeys2 = dict2.keys()
sortedKeys2.sort()


def fixValue(value,sortedKeys,lookDict):
    ind = bisect.bisect_left(sortedKeys, value)

    if (ind == 0):
        return value

    refValue = sortedKeys[ind-1]

    delta = (value - refValue)

    newValue = lookDict[refValue] + delta

    #print value, refValue, newValue

    assert(delta >= 0)

    return newValue


import csv
q = open(r'C:\Users\Daniel\Documents\University\tesis\project\humanSeminarTextCompare\humanSeminarTextCompare\bin\x64\Release\matchesClean.txt','wb')
writer = csv.writer(q)

allLines = []
allScores = []
orgId = 0

#with open('c:\\mhumanseminar\\1364615862538_out.txt', 'rb') as f:


with open(r'C:\Users\Daniel\Documents\University\tesis\project\humanSeminarTextCompare\humanSeminarTextCompare\bin\x64\Release\matches.txt', 'rb') as f:
    reader = csv.reader(f)
    for row in reader:
        startOne = int(row[0])
        startTwo = int(row[1])
        endOne = int(row[2])
        endTwo = int(row[3])
        newStartOne = fixValue(startOne,sortedKeys1,dict1);
        newStartTwo = fixValue(startTwo,sortedKeys2,dict2);
        newEndOne = fixValue(endOne,sortedKeys1,dict1);
        newEndTwo = fixValue(endTwo,sortedKeys2,dict2);
        #print startOne, newStartOne
        #writer.writerow([newStartOne, newStartTwo, newEndOne, newEndTwo, row[4], row[5], row[6]])
        #allLines.append([newStartOne, newStartTwo, newEndOne, newEndTwo, row[4], row[5], row[6], startOne, startTwo, endOne, endTwo, orgId])
        allLines.append([newStartOne, newStartTwo, newEndOne, newEndTwo, row[4]])
        orgId = orgId + 1
        allScores.append(float(row[4]))

scoreOrder = numpy.array(allScores).argsort()

for ind in scoreOrder:
    writer.writerow(allLines[ind])

q.close()
