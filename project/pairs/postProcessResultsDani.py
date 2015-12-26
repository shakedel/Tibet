execfile("localalign.py")
execfile("union.py")

def postprocess(inResultsPath, outResultsPath, maxDistanceForUnion, alignPadRatio):
	noHeaderPath = outResultsPath + ".noHeader"
	firstUnionPath = outResultsPath + ".firstUnion"
	
	inResultsFile = open(inResultsPath,"r")
	firstInputFilePath = inResultsFile.readline().rstrip('\n')
	secondInputFilePath = inResultsFile.readline().rstrip('\n')

	localAlign(firstInputFilePath, secondInputFilePath, firstUnionPath, outResultsPath, alignPadRatio)	
