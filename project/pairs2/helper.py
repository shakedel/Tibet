import codecs
import re
import operator
from itertools import islice
def getTextLinesPreprocessed(inputFilePath):
    lines = list()
    file1 = open(inputFilePath)
    for origLine in file1:
        line = re.sub('\[.*\]','', origLine.rstrip())
        line = re.sub('[!@#$\/%]', '', line)
        lines.append(line.rstrip())
    return lines

def countWordsInFile(filename,wordCount1):
    lines = getTextLinesPreprocessed(filename)
    for line in lines:
            words = line.split(' ')
            for word in words:
                if word not in wordCount1:
                    wordCount1[word] = 0
                wordCount1[word] += 1
    for word in wordCount1:
        wordCount1[word] = 1.0 / wordCount1[word]

