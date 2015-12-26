import math;
def padRange(start, end, ratio):
    rangeLen = (end - start) + 1
    newstart = start - rangeLen * ratio
    newend = end + rangeLen * ratio
    return (int(math.floor(newstart)), int(math.ceil(newend)))

padRatio = 0.12
def print_section(textPath, start, end):
    f = open(textPath, 'rb')
    t = f.read();
    f.close();
    tBorder = len(t) - 1
    pad = padRange(start, end, padRatio)
    startOne = max(0, pad[0])
    endOne = min(pad[1], tBorder)
    print t[startOne:endOne]

path1 = r"C:\Users\Daniel\Documents\University\tesis\project\texts\1.txt"
path2 = r"C:\Users\Daniel\Documents\University\tesis\project\texts\2.txt"
print 0,227,99,358
print_section(path1,1254,1573)
print "-----------------------"
print_section(path2,111143,111353)