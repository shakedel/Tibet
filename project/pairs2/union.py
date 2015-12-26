import numpy
import os
import cPickle
import csv

def checkSegment(seg1, seg2, maxDistance):
    if (seg2[0] < seg1[0]):
        return checkSegment(seg2, seg1, maxDistance)

    if (seg1[1] < seg2[0]):
        return ((seg2[0] - seg1[1]) < maxDistance)
    else:
        return True
    
def checkUnion(rec1, rec2, maxDistance):
    newMaxDistance = (len(rec1) + len(rec2)) * maxDistance
    flagSeg1 = checkSegment(rec1[[0,2]],rec2[[0,2]], newMaxDistance)
    flagSeg2 = checkSegment(rec1[[1,3]],rec2[[1,3]], newMaxDistance)
    return (flagSeg1 and flagSeg2)


def createUnion(rec1, rec2):
    newRecordFirstSegStart = min(rec1[0], rec2[0])
    newRecordFirstSegEnd = max(rec1[2], rec2[2])
    newRecordSecondSegStart = min(rec1[1], rec2[1])
    newRecordSecondSegEnd = max(rec1[3], rec2[3])
    return [newRecordFirstSegStart, newRecordSecondSegStart, newRecordFirstSegEnd, newRecordSecondSegEnd]


def uniteResults(allResults, unionPairs):
    elementToGroup = dict()
    groupToElements = dict()
    numOfGroups = len(allResults)

    newResults = []
    
    for i in range(len(allResults)):
        elementToGroup[i] = i
        groupToElements[i] = [i]

    for i in range(len(unionPairs)):
        firstElement = unionPairs[i][0]
        secondElement = unionPairs[i][1]
        
        firstGroup = elementToGroup[firstElement]
        secondGroup = elementToGroup[secondElement]
        
        if (firstGroup != secondGroup):
            newGroupName = min(firstGroup, secondGroup)
            
            newGroupElements = groupToElements[firstGroup]
            newGroupElements.extend(groupToElements[secondGroup])

            for element in groupToElements[firstGroup]:
                elementToGroup[element] = newGroupName
                
            groupToElements.pop(max(firstGroup,secondGroup), None)
            groupToElements[newGroupName] = newGroupElements
            
        numOfGroups = numOfGroups - 1

    for key in groupToElements.keys():
        allElements = groupToElements[key]
        if (len(allElements) == 1):
            newResults.append(list(allResults[allElements[0],0:4]))
        else:
            newResValue = createUnion(allResults[allElements[0]], allResults[allElements[1]])
            if (len(allElements) > 2):
                for j in range(2, len(allElements)):
                    newResValue = createUnion(newResValue, allResults[allElements[j]])
            newResults.append(newResValue)
    return newResults
     
def runUnion(inResultsPath, outResultsPath, maxDistanceForUnion):
	allResults=numpy.loadtxt(inResultsPath,delimiter=",")
	maxDistance = maxDistanceForUnion
	results = []
	
	for i in range(allResults.shape[0]):
		for j in range(i+1, allResults.shape[0]):
			if (checkUnion(allResults[i],allResults[j],maxDistance)):
				results.append((i,j))
                    
	resultsAfterUnion = uniteResults(allResults, results)


	q = open(outResultsPath,'wb')
	writer = csv.writer(q)

	for i in range(len(resultsAfterUnion)):
			currentResult = resultsAfterUnion[i]
			tScore = -1*(currentResult[2] - currentResult[0])
			currentResult.append(tScore)
			writer.writerow(map(int,currentResult))

	q.close()
