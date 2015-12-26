import cPickle as pickle
import codecs

execfile("postProcessResults.py")

import argparse
parser = argparse.ArgumentParser()
parser.add_argument('--inFileA', type=str, required=True)
parser.add_argument('--inFileB', type=str, required=True)
parser.add_argument('--outFile', type=str, required=True)
parser.add_argument('--minDistanceMatch', type=int, required=True)
parser.add_argument('--maxErrorMatch', type=int, required=True)
parser.add_argument('--minDistanceUnion', type=int, required=True)
parser.add_argument('--localAlignPadRatio', type=float, required=True)

args = parser.parse_args()

headerPath = args.outFile + ".header"

import subprocess
subprocess.call(['java', '-jar', 'matching.jar', args.inFileA, args.inFileB, headerPath, str(args.minDistanceMatch), str(args.maxErrorMatch)])
postprocess(headerPath, args.outFile, args.minDistanceUnion, args.localAlignPadRatio)