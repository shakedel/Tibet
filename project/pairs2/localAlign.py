import math

def padRange(start, end, ratio):
    rangeLen = (end - start) + 1
    newstart = start - rangeLen * ratio
    newend = end + rangeLen * ratio
    return (int(math.floor(newstart)), int(math.ceil(newend)))


execfile("waterWordsTibetLocalAlign.py")

# match_award = 1
# mismatch_penalty = -1
# gap_penalty = -1

import csv


def localAlign(text1Path, text2Path, resultsPath, newResultsPath, padRatio,wordCount):
    q = open(newResultsPath, 'wb')
    writer = csv.writer(q)

    allLines = []
    allScores = []
    orgId = 0

    #print text1Path
    f1 = open(text1Path, 'rb')
    f2 = open(text2Path, 'rb')
    t1 = f1.read()
    t2 = f2.read()
    f1.close()
    f2.close()

    t1Border = len(t1) - 1
    t2Border = len(t2) - 1
    #print str(t1Border) + "," + str(t2Border)

    with open(resultsPath, 'rb') as f:
        reader = csv.reader(f)
        for row in reader:
            startOne = int(row[0])
            startTwo = int(row[1])
            endOne = int(row[2])
            endTwo = int(row[3])

            padOne = padRange(startOne, endOne, padRatio)
            padTwo = padRange(startTwo, endTwo, padRatio)

            startOne = max(0, padOne[0])
            endOne = min(padOne[1], t1Border)
            startTwo = max(0, padTwo[0])
            endTwo = min(padTwo[1], t2Border)

            #print str(startOne) + "," + str(endOne)
            #print str(startTwo) + "," + str(endTwo)
            [newStartOne, newStartTwo, newEndOne, newEndTwo, score] = words_water(t1[startOne:endOne],
                                                                                  t2[startTwo:endTwo],wordCount)

            allLines.append([startOne + newStartOne, startTwo + newStartTwo, startOne + newEndOne, startTwo + newEndTwo,
                             -1 * score])
            orgId = orgId + 1

    for line in allLines:
        writer.writerow(line)
    q.close()

