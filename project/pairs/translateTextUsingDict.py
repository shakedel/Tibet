import cPickle as pickle
import codecs

import argparse
parser = argparse.ArgumentParser()
parser.add_argument('--inFile', type=str, required=False)
parser.add_argument('--inDict', type=str, required=False)
parser.add_argument('--outFile', type=str, required=False)
args = parser.parse_args()

fIn = open(args.inDict, "rb")
unicodeToChar = pickle.load(fIn)
fIn.close()

charToUnicode = dict()
for k in unicodeToChar.keys():
    charToUnicode[unicodeToChar[k]] = k

fIn = open(args.inFile, 'r')
text = fIn.read()
fIn.close()

newText = ""
for currentChar in text:
    if currentChar in charToUnicode.keys():
        newText = newText + charToUnicode[currentChar]
    else:
        newText = newText + currentChar

if (args.outFile is None):        
	print newText
else:
	fOut = codecs.open(args.outFile, "w",encoding="windows-1255")
	fOut.write(newText)
	fOut.close()
