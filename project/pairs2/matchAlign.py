import cPickle as pickle
import codecs

execfile("localalign.py")
execfile("helper.py")

import argparse
parser = argparse.ArgumentParser()
parser.add_argument('--inFileA', type=str, required=True)
parser.add_argument('--inFileB', type=str, required=True)
parser.add_argument('--outFile', type=str, required=True)
parser.add_argument('--minDistanceMatch', type=str, required=True)
parser.add_argument('--maxErrorMatch', type=str, required=True)
parser.add_argument('--minDistanceUnion', type=str, required=True)
parser.add_argument('--localAlignPadRatio', type=str, required=True)

args = parser.parse_args()

minDistanceMatch = float(args.minDistanceMatch)
maxErrorMatch = float(args.maxErrorMatch)
minDistanceUnion = float(args.minDistanceUnion)
localAlignPadRatio = float(args.localAlignPadRatio)

firstUnionPath = args.outFile + ".firstUnion"

wordCount1 = dict()
countWordsInFile(args.inFileA,wordCount1)
countWordsInFile(args.inFileB,wordCount1)

localAlign(args.inFileA, args.inFileB, firstUnionPath, args.outFile, localAlignPadRatio, wordCount1)