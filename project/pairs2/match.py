import cPickle as pickle
import codecs

execfile("postProcessResults.py")

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

import subprocess
subprocess.call(['java', '-jar', 'matching.jar', args.inFileA, args.inFileB, headerPath, args.minDistanceMatch, args.maxErrorMatch])

postprocess(headerPath, args.outFile, minDistanceUnion, localAlignPadRatio)