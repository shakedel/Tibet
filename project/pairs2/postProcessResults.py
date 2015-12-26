execfile("localalign.py")
execfile("union.py")

def postprocess(inResultsPath, outResultsPath, maxDistanceForUnion, alignPadRatio):
	noHeaderPath = outResultsPath + ".noHeader"
	firstUnionPath = outResultsPath + ".firstUnion"
	
	inResultsFile = open(inResultsPath,"r")
	firstInputFilePath = inResultsFile.readline().rstrip('\n')
	secondInputFilePath = inResultsFile.readline().rstrip('\n')
	inResultsFile.readline()
	inResultsFile.readline()
	inResultsFile.readline()
	inResultsFile.readline()
	
	allResults = inResultsFile.readlines()
	outPutFile  = open(noHeaderPath, "w")
	
	for line in allResults:
			outPutFile.write(line)
	
	outPutFile.close()
	inResultsFile.close()
	
	runUnion(noHeaderPath, firstUnionPath, maxDistanceForUnion)
	localAlign(firstInputFilePath, secondInputFilePath, firstUnionPath, outResultsPath, alignPadRatio)	
