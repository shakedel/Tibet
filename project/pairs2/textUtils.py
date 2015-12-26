import codecs
import os
import re
import cPickle as pickle

replaceCharsList = []

def saveUnicodeReplaceCharFiles(basePath, dictToDump, currentCharToDump):
    unicodeCharToReplaceDictOutFile = "unicodeCharToReplaceDict.bin"
    currentReplaceCharFile = "currentReplaceChar.bin"
    fOut = open(basePath + "\\out\\" + unicodeCharToReplaceDictOutFile, "wb")
    pickle.dump(dictToDump, fOut)
    fOut.close()
    fOut = open(basePath + "\\out\\" + currentReplaceCharFile, "wb")
    pickle.dump(currentCharToDump, fOut)
    fOut.close()
    
def getFilesInDir(path):
    files = (file for file in os.listdir(path) if os.path.isfile(os.path.join(path, file)))
    return files

def getHebrewEncodingFileObject(path):
    return codecs.open(path, encoding="windows-1255").read()

def removeHebrewPunctuation(strToProcess):
    newText = ""
    for currentChar in (strToProcess):
        getCurrentNonUnicode = repr(currentChar)
        if (len(getCurrentNonUnicode) > 6):
            if ((getCurrentNonUnicode[6] != 'b') and (getCurrentNonUnicode[6] != 'c')):
                newText = newText + currentChar
        else:
            newText = newText + currentChar
    return newText

def handleTextTypeThirteen(path, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = ""
    text = getHebrewEncodingFileObject(path)
    text = removeHebrewPunctuation(text)
    text = re.sub("\(.[^\)]*\)","",text)
    text = re.sub("\[.[^\]]*\]","",text)
    text = re.sub("[\".,:?'-]*","",text)
    text = re.sub("[ ][ ]+"," ",text)
    text, currentReplaceChar, unicodeCharToReplaceDict = translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict)
    lines = text.split('\r\n')
    numOfLines = len(lines)
    firstLine = 2
    lastLine = numOfLines - 2
    for i in range(firstLine, lastLine):
        currentLine = lines[i]
        finalText = finalText + currentLine.rstrip(u" ").lstrip(u" ") + " "
    return (finalText.rstrip(u" "), currentReplaceChar, unicodeCharToReplaceDict)

def handleTextTypeTwelve(path, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = ""
    text = getHebrewEncodingFileObject(path)
    text = removeHebrewPunctuation(text)
    text = re.sub("\(.[^\)]*\)","",text)
    text = re.sub("\[.[^\]]*\]","",text)
    text = re.sub("[\".,:?'-]*","",text)
    text = re.sub("[ ][ ]+"," ",text)
    text, currentReplaceChar, unicodeCharToReplaceDict = translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict)
    lines = text.split('\r\n')
    numOfLines = len(lines)
    firstLine = 2
    lastLine = numOfLines - 2
    for i in range(firstLine, lastLine):
        currentLine = lines[i]
        finalText = finalText + currentLine.rstrip(u" ").lstrip(u" ") + " "
    return (finalText.rstrip(u" "), currentReplaceChar, unicodeCharToReplaceDict)

def handleTextTypeEleven(path, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = ""
    text = getHebrewEncodingFileObject(path)
    text = removeHebrewPunctuation(text)
    text = re.sub("\(.[^\)]*\)","",text)
    text = re.sub("\[.[^\]]*\]","",text)
    text = re.sub("[\".,:?'-]*","",text)
    text = re.sub("[ ][ ]+"," ",text)
    text, currentReplaceChar, unicodeCharToReplaceDict = translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict)
    lines = text.split('\r\n')
    numOfLines = len(lines)
    firstLine = 2
    lastLine = numOfLines - 2
    for i in range(firstLine, lastLine):
        currentLine = lines[i]
        finalText = finalText + currentLine.rstrip(u" ").lstrip(u" ") + " "
    return (finalText.rstrip(u" "), currentReplaceChar, unicodeCharToReplaceDict)


def handleTextTypeTen(path, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = ""
    text = getHebrewEncodingFileObject(path)
    text = removeHebrewPunctuation(text)
    text = re.sub("\[.[^\]]*\]","",text)
    text = re.sub("\(.[^\)]*\)","",text)
    text = re.sub("[\".,:?'-]*","",text)
    text = re.sub("[ ][ ]+"," ",text)
    text, currentReplaceChar, unicodeCharToReplaceDict = translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict)
    lines = text.split('\r\n')
    numOfLines = len(lines)
    firstLine = 2
    lastLine = numOfLines - 2
    for i in range(firstLine, lastLine):
        currentLine = lines[i]
        if (len(currentLine) > 10): # removes mishna, halacha, etc...
            finalText = finalText + currentLine.rstrip(u" ").lstrip(u" ") + " "
    return (finalText.rstrip(u" "), currentReplaceChar, unicodeCharToReplaceDict)


def handleTextTypeNine(path, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = ""
    text = getHebrewEncodingFileObject(path)
    text = removeHebrewPunctuation(text)
    text = re.sub("\(.[^\)]*\)","",text)
    text = re.sub("\[.[^\]]*\]","",text)
    text = re.sub("[\".,:?'-]*","",text)
    text = re.sub("[ ][ ]+"," ",text)
    text, currentReplaceChar, unicodeCharToReplaceDict = translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict)
    lines = text.split('\r\n')
    numOfLines = len(lines)
    firstLine = 2
    lastLine = numOfLines - 2
    for i in range(firstLine, lastLine):
        currentLine = lines[i]
        finalText = finalText + currentLine.rstrip(u" ").lstrip(u" ") + " "
    return (finalText.rstrip(u" "), currentReplaceChar, unicodeCharToReplaceDict)


def handleTextTypeEight(path, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = ""
    text = getHebrewEncodingFileObject(path)
    text = removeHebrewPunctuation(text)
    text = re.sub("\(.[^\)]*\)","",text)
    text = re.sub("\[.[^\]]*\]","",text)
    text = re.sub("[\".,:?'-]*","",text)
    text = re.sub("[ ][ ]+"," ",text)
    text, currentReplaceChar, unicodeCharToReplaceDict = translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict)
    lines = text.split('\r\n')
    numOfLines = len(lines)
    firstLine = 2
    lastLine = numOfLines - 2
    for i in range(firstLine, lastLine):
        currentLine = lines[i]
        finalText = finalText + currentLine.rstrip(u" ").lstrip(u" ") + " "
    return (finalText.rstrip(u" "), currentReplaceChar, unicodeCharToReplaceDict)


def handleTextTypeSeven(path, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = ""
    text = getHebrewEncodingFileObject(path)
    text = removeHebrewPunctuation(text)
    text = re.sub("\(.[^\)]*\)","",text)
    text = re.sub("\[.[^\]]*\]","",text)
    text = re.sub("[\".,:?'-]*","",text)
    text = re.sub("[ ][ ]+"," ",text)
    text, currentReplaceChar, unicodeCharToReplaceDict = translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict)
    lines = text.split('\r\n')
    numOfLines = len(lines)
    firstLine = 2
    lastLine = numOfLines - 2
    for i in range(firstLine, lastLine):
        currentLine = lines[i]
        finalText = finalText + currentLine.rstrip(u" ").lstrip(u" ") + " "
    return (finalText.rstrip(u" "), currentReplaceChar, unicodeCharToReplaceDict)

	
def handleTextTypeSixB(path, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = ""
    text = getHebrewEncodingFileObject(path)
    text = removeHebrewPunctuation(text)
    text = re.sub("\(.[^\)]*\)","",text)
    text = re.sub("\[.[^\]]*\]","",text)
    text = re.sub("[\".,:?'-]*","",text)
    text = re.sub("[ ][ ]+"," ",text)
    text, currentReplaceChar, unicodeCharToReplaceDict = translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict)
    lines = text.split('\r\n')
    numOfLines = len(lines)
    firstLine = 2
    lastLine = numOfLines - 2
    for i in range(firstLine, lastLine):
        currentLine = lines[i]
        finalText = finalText + currentLine.rstrip(u" ").lstrip(u" ") + " "
    return (finalText.rstrip(u" "), currentReplaceChar, unicodeCharToReplaceDict)
	
def handleTextTypeSixA(path, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = ""
    text = getHebrewEncodingFileObject(path)
    text = removeHebrewPunctuation(text)
    text = re.sub("\(.[^\)]*\)","",text)
    text = re.sub("\[.[^\]]*\]","",text)
    text = re.sub("[\".,:?'-]*","",text)
    text = re.sub("[ ][ ]+"," ",text)
    text, currentReplaceChar, unicodeCharToReplaceDict = translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict)
    lines = text.split('\r\n')
    numOfLines = len(lines)
    firstLine = 2
    lastLine = numOfLines - 2
    for i in range(firstLine, lastLine):
        currentLine = lines[i]
        finalText = finalText + currentLine.rstrip(u" ").lstrip(u" ") + " "
    return (finalText.rstrip(u" "), currentReplaceChar, unicodeCharToReplaceDict)	
	
def handleTextTypeFive(path, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = ""
    text = getHebrewEncodingFileObject(path)
    text = removeHebrewPunctuation(text)
    text = re.sub("\[.[^\]]*\]","",text)
    text = re.sub("\(.[^\)]*\)","",text)
    text = re.sub("[\".,:?'-]*","",text)
    text = re.sub("[ ][ ]+"," ",text)
    text, currentReplaceChar, unicodeCharToReplaceDict = translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict)
    lines = text.split('\r\n')
    numOfLines = len(lines)
    firstLine = 2
    lastLine = numOfLines - 2
    for i in range(firstLine, lastLine):
        currentLine = lines[i]
        if (len(currentLine) > 10): # removes mishna, halacha, etc...
            finalText = finalText + currentLine.rstrip(u" ").lstrip(u" ") + " "
    return (finalText.rstrip(u" "), currentReplaceChar, unicodeCharToReplaceDict)
	
def handleTextTypeFour(path, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = ""
    text = getHebrewEncodingFileObject(path)
    text = removeHebrewPunctuation(text)
    text = re.sub("\(.[^\)]*\)","",text)
    text = re.sub("\[.[^\]]*\]","",text)
    text = re.sub("[\".,:?'-]*","",text)
    text = re.sub("[ ][ ]+"," ",text)
    text, currentReplaceChar, unicodeCharToReplaceDict = translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict)
    lines = text.split('\r\n')
    numOfLines = len(lines)
    firstLine = 2
    lastLine = numOfLines - 2
    for i in range(firstLine, lastLine):
        currentLine = lines[i]
        finalText = finalText + currentLine.rstrip(u" ").lstrip(u" ") + " "
    return (finalText.rstrip(u" "), currentReplaceChar, unicodeCharToReplaceDict)

def handleTextTypeThree(path, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = ""
    text = getHebrewEncodingFileObject(path)
    text = removeHebrewPunctuation(text)
    text = re.sub("\(.[^\)]*\)","",text)
    text = re.sub("[\".,:?'-]*","",text)
    text = re.sub("[ ][ ]+"," ",text)
    text, currentReplaceChar, unicodeCharToReplaceDict = translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict)
    lines = text.split('\r\n')
    numOfLines = len(lines)
    firstLine = 2
    lastLine = numOfLines - 2
    for i in range(firstLine, lastLine):
        currentLine = lines[i]
        finalText = finalText + currentLine.rstrip(u" ").lstrip(u" ") + " "
    return (finalText.rstrip(u" "), currentReplaceChar, unicodeCharToReplaceDict)


def handleTextTypeTwo(path, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = ""
    text = getHebrewEncodingFileObject(path)
    text = removeHebrewPunctuation(text)
    text = re.sub("\[.[^\]]*\]","",text)
    text = re.sub("[\".,:?'-]*","",text)
    text = re.sub("[ ][ ]+"," ",text)
    text, currentReplaceChar, unicodeCharToReplaceDict = translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict)
    lines = text.split('\r\n')
    numOfLines = len(lines)
    firstLine = 2
    lastLine = numOfLines - 2
    for i in range(firstLine, lastLine):
        currentLine = lines[i]
        if (len(currentLine) > 10): # removes mishna, halacha, etc...
            finalText = finalText + currentLine.rstrip(u" ").lstrip(u" ") + " "
    return (finalText.rstrip(u" "), currentReplaceChar, unicodeCharToReplaceDict)


def handleTextTypeOne(path, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = ""
    text = getHebrewEncodingFileObject(path)
    text = removeHebrewPunctuation(text)
    text = re.sub("\(.[^\)]*\)","",text)
    text = re.sub("[\".,:?'-]*","",text)
    text = re.sub("[ ][ ]+"," ",text)
    text, currentReplaceChar, unicodeCharToReplaceDict = translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict)
    lines = text.split('\r\n')
    numOfLines = len(lines)
    firstLine = 2
    lastLine = numOfLines - 2
    for i in range(firstLine, lastLine):
        currentLine = lines[i]
        finalText = finalText + currentLine.rstrip(u" ").lstrip(u" ") + " "
    return (finalText.rstrip(u" "), currentReplaceChar, unicodeCharToReplaceDict)


def handleTextTypeZero(path, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = ""
    text = getHebrewEncodingFileObject(path)
    text = removeHebrewPunctuation(text)
    text = re.sub("[ ][ ]+"," ",text)
    text, currentReplaceChar, unicodeCharToReplaceDict = translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict)
    lines = text.split('\r\n')
    numOfLines = len(lines)
    firstLine = 2
    lastLine = numOfLines - 3
    for i in range(firstLine, lastLine):
        currentLine = lines[i]
        startLine = currentLine.find(")") + 1
        endLine = currentLine.find(":")
        if (endLine > 0):
            finalText =  finalText + currentLine[startLine:endLine].rstrip(u" ").lstrip(u" ") + " "
    return (finalText.rstrip(u" "), currentReplaceChar, unicodeCharToReplaceDict)

def handleZeroFolder(path, currentReplaceChar, unicodeCharToReplaceDict):
    allFiles = getFilesInDir(path)
    for currentFile in allFiles:
        currentText, currentReplaceChar, unicodeCharToReplaceDict = handleTextTypeZero(path + "\\" + currentFile, currentReplaceChar, unicodeCharToReplaceDict)
        outFile = codecs.open(path + "\\out\\" + currentFile, "w", "utf-8")
        outFile.write(currentText)
        outFile.close()
    saveUnicodeReplaceCharFiles(path, unicodeCharToReplaceDict, currentReplaceChar)
    return (currentReplaceChar, unicodeCharToReplaceDict)

def handleOneFolder(path, currentReplaceChar, unicodeCharToReplaceDict):
    allFiles = getFilesInDir(path)
    for currentFile in allFiles:
        print currentFile
        currentText, currentReplaceChar, unicodeCharToReplaceDict = handleTextTypeOne(path + "\\" + currentFile, currentReplaceChar, unicodeCharToReplaceDict)
        outFile = codecs.open(path + "\\out\\" + currentFile, "w", "utf-8")
        outFile.write(currentText)
        outFile.close()
    saveUnicodeReplaceCharFiles(path, unicodeCharToReplaceDict, currentReplaceChar)
    return (currentReplaceChar, unicodeCharToReplaceDict)

def handleTwoFolder(path, currentReplaceChar, unicodeCharToReplaceDict):
    allFiles = getFilesInDir(path)
    for currentFile in allFiles:
        print currentFile
        currentText, currentReplaceChar, unicodeCharToReplaceDict = handleTextTypeTwo(path + "\\" + currentFile, currentReplaceChar, unicodeCharToReplaceDict)
        outFile = codecs.open(path + "\\out\\" + currentFile, "w", "utf-8")
        outFile.write(currentText)
        outFile.close()
    saveUnicodeReplaceCharFiles(path, unicodeCharToReplaceDict, currentReplaceChar)
    return (currentReplaceChar, unicodeCharToReplaceDict)

def handleThreeFolder(path, currentReplaceChar, unicodeCharToReplaceDict):
    allFiles = getFilesInDir(path)
    for currentFile in allFiles:
        print currentFile
        currentText, currentReplaceChar, unicodeCharToReplaceDict = handleTextTypeThree(path + "\\" + currentFile, currentReplaceChar, unicodeCharToReplaceDict)
        outFile = codecs.open(path + "\\out\\" + currentFile, "w", "utf-8")
        outFile.write(currentText)
        outFile.close()
    saveUnicodeReplaceCharFiles(path, unicodeCharToReplaceDict, currentReplaceChar)
    return (currentReplaceChar, unicodeCharToReplaceDict)

def handleFourFolder(path, currentReplaceChar, unicodeCharToReplaceDict):
    allFiles = getFilesInDir(path)
    for currentFile in allFiles:
        print currentFile
        currentText, currentReplaceChar, unicodeCharToReplaceDict = handleTextTypeFour(path + "\\" + currentFile, currentReplaceChar, unicodeCharToReplaceDict)
        outFile = codecs.open(path + "\\out\\" + currentFile, "w", "utf-8")
        outFile.write(currentText)
        outFile.close()
    saveUnicodeReplaceCharFiles(path, unicodeCharToReplaceDict, currentReplaceChar)
    return (currentReplaceChar, unicodeCharToReplaceDict)

def handleFiveFolder(path, currentReplaceChar, unicodeCharToReplaceDict):
    allFiles = getFilesInDir(path)
    for currentFile in allFiles:
        print currentFile
        currentText, currentReplaceChar, unicodeCharToReplaceDict = handleTextTypeFive(path + "\\" + currentFile, currentReplaceChar, unicodeCharToReplaceDict)
        outFile = codecs.open(path + "\\out\\" + currentFile, "w", "utf-8")
        outFile.write(currentText)
        outFile.close()
    saveUnicodeReplaceCharFiles(path, unicodeCharToReplaceDict, currentReplaceChar)
    return (currentReplaceChar, unicodeCharToReplaceDict)

def handleSixAFolder(path, currentReplaceChar, unicodeCharToReplaceDict):
    allFiles = getFilesInDir(path)
    for currentFile in allFiles:
        print currentFile
        currentText, currentReplaceChar, unicodeCharToReplaceDict = handleTextTypeSixA(path + "\\" + currentFile, currentReplaceChar, unicodeCharToReplaceDict)
        outFile = codecs.open(path + "\\out\\" + currentFile, "w", "utf-8")
        outFile.write(currentText)
        outFile.close()
    saveUnicodeReplaceCharFiles(path, unicodeCharToReplaceDict, currentReplaceChar)
    return (currentReplaceChar, unicodeCharToReplaceDict)
	
def handleSixBFolder(path, currentReplaceChar, unicodeCharToReplaceDict):
    allFiles = getFilesInDir(path)
    for currentFile in allFiles:
        print currentFile
        currentText, currentReplaceChar, unicodeCharToReplaceDict = handleTextTypeSixB(path + "\\" + currentFile, currentReplaceChar, unicodeCharToReplaceDict)
        outFile = codecs.open(path + "\\out\\" + currentFile, "w", "utf-8")
        outFile.write(currentText)
        outFile.close()
    saveUnicodeReplaceCharFiles(path, unicodeCharToReplaceDict, currentReplaceChar)
    return (currentReplaceChar, unicodeCharToReplaceDict)


def handleSevenFolder(path, currentReplaceChar, unicodeCharToReplaceDict):
    allFiles = getFilesInDir(path)
    for currentFile in allFiles:
        print currentFile
        currentText, currentReplaceChar, unicodeCharToReplaceDict = handleTextTypeSeven(path + "\\" + currentFile, currentReplaceChar, unicodeCharToReplaceDict)
        outFile = codecs.open(path + "\\out\\" + currentFile, "w", "utf-8")
        outFile.write(currentText)
        outFile.close()
    saveUnicodeReplaceCharFiles(path, unicodeCharToReplaceDict, currentReplaceChar)
    return (currentReplaceChar, unicodeCharToReplaceDict)


def handleEightFolder(path, currentReplaceChar, unicodeCharToReplaceDict):
    allFiles = getFilesInDir(path)
    for currentFile in allFiles:
        print currentFile
        currentText, currentReplaceChar, unicodeCharToReplaceDict = handleTextTypeEight(path + "\\" + currentFile, currentReplaceChar, unicodeCharToReplaceDict)
        outFile = codecs.open(path + "\\out\\" + currentFile, "w", "utf-8")
        outFile.write(currentText)
        outFile.close()
    saveUnicodeReplaceCharFiles(path, unicodeCharToReplaceDict, currentReplaceChar)
    return (currentReplaceChar, unicodeCharToReplaceDict)


def handleNineFolder(path, currentReplaceChar, unicodeCharToReplaceDict):
    allFiles = getFilesInDir(path)
    for currentFile in allFiles:
        print currentFile
        currentText, currentReplaceChar, unicodeCharToReplaceDict = handleTextTypeNine(path + "\\" + currentFile, currentReplaceChar, unicodeCharToReplaceDict)
        outFile = codecs.open(path + "\\out\\" + currentFile, "w", "utf-8")
        outFile.write(currentText)
        outFile.close()
    saveUnicodeReplaceCharFiles(path, unicodeCharToReplaceDict, currentReplaceChar)
    return (currentReplaceChar, unicodeCharToReplaceDict)


def handleTenFolder(path, currentReplaceChar, unicodeCharToReplaceDict):
    allFiles = getFilesInDir(path)
    for currentFile in allFiles:
        print currentFile
        currentText, currentReplaceChar, unicodeCharToReplaceDict = handleTextTypeTen(path + "\\" + currentFile, currentReplaceChar, unicodeCharToReplaceDict)
        outFile = codecs.open(path + "\\out\\" + currentFile, "w", "utf-8")
        outFile.write(currentText)
        outFile.close()
    saveUnicodeReplaceCharFiles(path, unicodeCharToReplaceDict, currentReplaceChar)
    return (currentReplaceChar, unicodeCharToReplaceDict)


def handleElevenFolder(path, currentReplaceChar, unicodeCharToReplaceDict):
    allFiles = getFilesInDir(path)
    for currentFile in allFiles:
        print currentFile
        currentText, currentReplaceChar, unicodeCharToReplaceDict = handleTextTypeEleven(path + "\\" + currentFile, currentReplaceChar, unicodeCharToReplaceDict)
        outFile = codecs.open(path + "\\out\\" + currentFile, "w", "utf-8")
        outFile.write(currentText)
        outFile.close()
    saveUnicodeReplaceCharFiles(path, unicodeCharToReplaceDict, currentReplaceChar)
    return (currentReplaceChar, unicodeCharToReplaceDict)


def handleTwelveFolder(path, currentReplaceChar, unicodeCharToReplaceDict):
    allFiles = getFilesInDir(path)
    for currentFile in allFiles:
        print currentFile
        currentText, currentReplaceChar, unicodeCharToReplaceDict = handleTextTypeTwelve(path + "\\" + currentFile, currentReplaceChar, unicodeCharToReplaceDict)
        outFile = codecs.open(path + "\\out\\" + currentFile, "w", "utf-8")
        outFile.write(currentText)
        outFile.close()
    saveUnicodeReplaceCharFiles(path, unicodeCharToReplaceDict, currentReplaceChar)
    return (currentReplaceChar, unicodeCharToReplaceDict)


def handleThirteenFolder(path, currentReplaceChar, unicodeCharToReplaceDict):
    allFiles = getFilesInDir(path)
    for currentFile in allFiles:
        print currentFile
        currentText, currentReplaceChar, unicodeCharToReplaceDict = handleTextTypeThirteen(path + "\\" + currentFile, currentReplaceChar, unicodeCharToReplaceDict)
        outFile = codecs.open(path + "\\out\\" + currentFile, "w", "utf-8")
        outFile.write(currentText)
        outFile.close()
    saveUnicodeReplaceCharFiles(path, unicodeCharToReplaceDict, currentReplaceChar)
    return (currentReplaceChar, unicodeCharToReplaceDict)



def translateTextWithDict(text, currentReplaceChar, unicodeCharToReplaceDict):
    finalText = " "
    k = 0
    totalLength = len(text)
    for currentChar in text:
        #if ((k % 1000) == 1):
        #    print str(k / float(totalLength))
        getCurrentNonUnicode = repr(currentChar)
        if (len(getCurrentNonUnicode) > 6):
            if unicodeCharToReplaceDict.has_key(currentChar):
                replaceChar = unicodeCharToReplaceDict[currentChar]
            else:
                replaceChar = currentReplaceChar
                unicodeCharToReplaceDict[currentChar] = replaceChar
                currentReplaceChar = replaceCharsList[replaceCharsList.index(currentReplaceChar)+1]
                print currentReplaceChar
            finalText = finalText + replaceChar
        else:
            if (currentChar == u" "):
                finalText = finalText + " "
            else:
                finalText = finalText + currentChar
        k = k + 1
    return (finalText, currentReplaceChar, unicodeCharToReplaceDict)

fillChar = 'a'
while (fillChar <= 'z'):
    replaceCharsList.append(fillChar)
    fillChar = chr(ord(fillChar) + 1)

fillChar = 'A'
while (fillChar <= 'Z'):
    replaceCharsList.append(fillChar)
    fillChar = chr(ord(fillChar) + 1)
        

currentReplaceChar = replaceCharsList[0]
unicodeCharToReplaceDict = dict()
            
zeroPath = r"F:\lior_text_project_2\Data\0"
onePath = r"F:\lior_text_project_2\Data\1"
twoPath = r"F:\lior_text_project_2\Data\2"
threePath = r"F:\lior_text_project_2\Data\3"
fourPath = r"F:\lior_text_project_2\Data\4"
fivePath = r"F:\lior_text_project_2\Data\5"
sixAPath = r"F:\lior_text_project_2\Data\6a"
sixBPath = r"F:\lior_text_project_2\Data\6b"
sevenPath = r"F:\lior_text_project_2\Data\7"
eightPath = r"F:\lior_text_project_2\Data\8"
ninePath = r"F:\lior_text_project_2\Data\9"
tenPath = r"F:\lior_text_project_2\Data\10"
elevenPath = r"F:\lior_text_project_2\Data\11"
twelvePath = r"F:\lior_text_project_2\Data\12"
thirteenPath = r"F:\lior_text_project_2\Data\13"

currentReplaceChar, unicodeCharToReplaceDict = handleZeroFolder(zeroPath, currentReplaceChar, unicodeCharToReplaceDict)
currentReplaceChar, unicodeCharToReplaceDict = handleOneFolder(onePath, currentReplaceChar, unicodeCharToReplaceDict)
currentReplaceChar, unicodeCharToReplaceDict = handleTwoFolder(twoPath, currentReplaceChar, unicodeCharToReplaceDict)
currentReplaceChar, unicodeCharToReplaceDict = handleThreeFolder(threePath, currentReplaceChar, unicodeCharToReplaceDict)

##unicodeCharToReplaceDictOutFile = "unicodeCharToReplaceDict.bin"
##currentReplaceCharFile = "currentReplaceChar.bin"
##threeUnicodeCharFile = open(threePath + "\\out\\" + unicodeCharToReplaceDictOutFile)
##unicodeCharToReplaceDict = pickle.load(threeUnicodeCharFile)
##threeUnicodeCharFile.close()
##threeCurrentReplaceCharFile = open(threePath + "\\out\\" + currentReplaceCharFile)
##currentReplaceChar = pickle.load(threeCurrentReplaceCharFile)
##threeCurrentReplaceCharFile.close()

currentReplaceChar, unicodeCharToReplaceDict = handleFourFolder(fourPath, currentReplaceChar, unicodeCharToReplaceDict)
currentReplaceChar, unicodeCharToReplaceDict = handleFiveFolder(fivePath, currentReplaceChar, unicodeCharToReplaceDict)
currentReplaceChar, unicodeCharToReplaceDict = handleSixAFolder(sixAPath, currentReplaceChar, unicodeCharToReplaceDict)
currentReplaceChar, unicodeCharToReplaceDict = handleSixBFolder(sixBPath, currentReplaceChar, unicodeCharToReplaceDict)

currentReplaceChar, unicodeCharToReplaceDict = handleSevenFolder(sevenPath, currentReplaceChar, unicodeCharToReplaceDict)
currentReplaceChar, unicodeCharToReplaceDict = handleEightFolder(eightPath, currentReplaceChar, unicodeCharToReplaceDict)
currentReplaceChar, unicodeCharToReplaceDict = handleNineFolder(ninePath, currentReplaceChar, unicodeCharToReplaceDict)
currentReplaceChar, unicodeCharToReplaceDict = handleTenFolder(tenPath, currentReplaceChar, unicodeCharToReplaceDict)
currentReplaceChar, unicodeCharToReplaceDict = handleElevenFolder(elevenPath, currentReplaceChar, unicodeCharToReplaceDict)
currentReplaceChar, unicodeCharToReplaceDict = handleTwelveFolder(twelvePath, currentReplaceChar, unicodeCharToReplaceDict)
currentReplaceChar, unicodeCharToReplaceDict = handleThirteenFolder(thirteenPath, currentReplaceChar, unicodeCharToReplaceDict)
