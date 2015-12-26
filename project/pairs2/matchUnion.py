import cPickle as pickle
import codecs

execfile("localalign.py")
execfile("union.py")

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

headerPath = args.outFile + ".header"
noHeaderPath = args.outFile + ".noHeader"
firstUnionPath = args.outFile + ".firstUnion"

runUnion(noHeaderPath, firstUnionPath, minDistanceUnion)
localAlign(args.inFileA, args.inFileB, firstUnionPath, args.outFile, localAlignPadRatio)