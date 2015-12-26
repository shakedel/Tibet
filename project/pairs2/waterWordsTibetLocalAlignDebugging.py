three_char_letters = ["tsh"];
two_char_letters = ["kh","ng","ch","ny","th","ph","ts","dz","zh","sh"];

vowels = ['i','u','o','e','a'];
possible_prefixes = ['g','d','b','m',"'"];
possible_superscripts = ['r','l','s'];
possible_subscripts = ['r','l','y','w'];
possible_final_letters = ['r','l','g',"ng",'d','n','b','m',"'",'s'];
possible_suffixes = ['d','s'];

class WordRep:
    def __init__(self,word):
        self.word =  word.translate(None, '!@#$/[]\\.,1234567890');
        self.letters = split_word_to_letters(self.word)
        self.vowel_index = self.__findVowelIndex();
        self.vowel = self.letters[self.vowel_index];

        self.prefix = '';
        self.stacked_letter = '';
        self.__setPref();
        self.suffix = '';
        self.final_letter = '';
        self.__sefSuffix();

    def GetRoot(self):
        return self.stacked_letter + self.vowel + self.final_letter;
    def GetRootWithoutVowel(self):
        return self.stacked_letter + self.final_letter;
    def __findVowelIndex(self):
        for vowel in vowels:
            if vowel in self.letters:
                return self.letters.index(vowel);
        return -1;
    def __setPref(self):

        # in order to find the prefix  we split the word to before the vowel
        before_vowel = self.letters[0:self.vowel_index];
        if len(before_vowel) == 0:
            raise Exception("word starts with vowel: "+ self.word);

        #check if the word has prefix

        # if single letter - I assume it is the root letter
        if len(before_vowel) == 1:
            self.stacked_letter = before_vowel[0];
        # if more than 4 letters - it should not occur
        if len(before_vowel) > 4:
            raise Exception("word starts too many letters before the vowel: " + self.word );
        # if 4 letters - it should be: prefix + superscript + root letter + subscript
        if len(before_vowel) == 4:
            if before_vowel[0] in possible_prefixes and before_vowel[1] in possible_superscripts and before_vowel[3] in possible_subscripts:
                self.prefix = before_vowel[0];
                self.stacked_letter = "".join(before_vowel[1:]);
            else:
                raise Exception("unknown sturcture: "+ self.word);
        # if 3 letters - I try in this order:
        # superscript + root letter + subscript
        # prefix + root letter + subscript
        # prefix + superscript + root letter
        # unknown structure
        if len(before_vowel) == 3:
            if before_vowel[0] in possible_superscripts and before_vowel[2] in possible_subscripts:
                self.stacked_letter = "".join(before_vowel);
            elif before_vowel[0] in possible_prefixes and before_vowel[2] in possible_subscripts:
                self.prefix = before_vowel[0];
                self.stacked_letter = "".join(before_vowel[1:]);
            elif before_vowel[0] in possible_prefixes and before_vowel[1] in possible_superscripts:
                self.prefix = before_vowel[0];
                self.stacked_letter = "".join(before_vowel[1:]);
            else:
                raise Exception("unknown sturcture: "+ self.word);

        # if 2 letters - I try in this order:
        # root letter + subscript
        # superscript + root letter
        # prefix + root letter
        # unknown structure
        if len(before_vowel) == 2:
            if before_vowel[1] in possible_subscripts:
                self.stacked_letter = "".join(before_vowel);
            elif before_vowel[0] in possible_superscripts:
                self.stacked_letter = "".join(before_vowel);
            elif before_vowel[0] in possible_prefixes:
                self.prefix = before_vowel[0];
                self.stacked_letter = "".join(before_vowel[1:]);
            else:
                raise Exception("unknown sturcture:"+ self.word);

    def __sefSuffix(self):
        # finding final letter + suffix (if they exists)

        # in order to find the suffix (if exists) we split the word to after the vowel
        after_vowel = self.letters[self.vowel_index+1 : ];

        # if no final letter + suffix - we do nothing

        # if one letter: it should be a final letter. o.w unknown structure
        if len(after_vowel) == 1:
            if after_vowel[0] in possible_final_letters:
                self.final_letter = after_vowel[0];
            else:
                raise Exception("the word end with suffix without a final letter: " + self.word);

        # if 2 letters: it should be a final letter + suffix. o.w unknown structure
        if len(after_vowel) == 2:
            if after_vowel[0] in possible_final_letters and after_vowel[1] in possible_suffixes:
                self.final_letter = after_vowel[0];
                self.suffix = after_vowel[1];

def split_word_to_letters(word):
    letters = list();
    chars = list(word);
    while (len(chars) > 0):
        if len(chars) >= 3 and "".join(chars[0:3]) in three_char_letters:
            letters.append("".join(chars[0:3]));
            chars = chars[3:];
            continue
        if len(chars) >= 2 and "".join(chars[0:2]) in two_char_letters:
            letters.append("".join(chars[0:2]));
            chars = chars[2:];
            continue
        letters.append("".join(chars[0]));
        chars = chars[1:];
    return letters;






def levenshtein(s1, s2):
    if len(s1) < len(s2):
        return levenshtein(s2, s1)

    # len(s1) >= len(s2)
    if len(s2) == 0:
        return len(s1)

    previous_row = xrange(len(s2) + 1)
    for i, c1 in enumerate(s1):
        current_row = [i + 1]
        for j, c2 in enumerate(s2):
            insertions = previous_row[j + 1] + 1 # j+1 instead of j since previous_row and current_row are one character longer
            deletions = current_row[j] + 1       # than s2
            substitutions = previous_row[j] + (c1 != c2)
            current_row.append(min(insertions, deletions, substitutions))
        previous_row = current_row

    return previous_row[-1]




EXACT_MATCH_SCORE = 10;
EXACT_ROOT_SCORE = 7;
DIFF_VOWEL = 5;
ERROR_WEIGHT = -4;
# we calculate all wordRepresentation in advanced
### alpha , beta are wordRep
def tibetan_words_match_score(alpha, beta):
    # if words are the same:
    if alpha.word == beta.word:
        return EXACT_MATCH_SCORE;
     # if roots are exactly the same:
    elif alpha.GetRoot() == beta.GetRoot():
        return EXACT_ROOT_SCORE;
    # same roots but the vowel changed:
    elif alpha.GetRootWithoutVowel() == beta.GetRootWithoutVowel():
        return DIFF_VOWEL;
    return ERROR_WEIGHT * levenshtein(alpha.word, beta.word);





letters = split_word_to_letters("byung");
print letters
rep = WordRep("byung")
alpha = WordRep("sgrubs")
beta = WordRep("bsgrub")
print tibetan_words_match_score(alpha, beta)
#find_root(letters)
#chars =  list("bsgrubs");
#print "".join(chars[3:])

# zeros() was origianlly from NumPy.
# This version is implemented by alevchuk 2011-04-10
def zeros(shape):
    retval = []
    for x in range(shape[0]):
        retval.append([])
        for y in range(shape[1]):
            retval[-1].append(0)
    return retval


####### Words Water!

Particles = ["su","ra","ru","du","tu","na","la",
            "kyi","gyi","gi","yi","'i",
            "kyis", "gyis", "gis","yis","'is",
            "kyang","yang","'ang",
            "ste","te","de",
            "gam","ngam","dam","nam","bam","mam","'am","ram","lam","sam","tam",
            "nas","las",
            "go","ngo","do","no","bo","mo","'o","ro","lo","so","to",
            "dang",
            "ni"];


Significant_Particles = ["ma","mi",
                         "pa","ba","po","bo" ];

#words_mismatch_penalty = -1
words_gap_penalty      = -10 # both for opening and extanding
Particles_gap_penalty = 0;
Significant_Particles_gap_penalty = -4;

def word_freq_penalty(word,wordCount):
    if word in wordCount:
        return -1 * wordCount[word]
    return words_gap_penalty

#def words_match_score(alpha, beta,wordCount):
#    dist = levenshtein(alpha, beta)
#    if dist <= min(len(alpha), len(beta))/2:
#        return 1-max(word_freq_penalty(alpha,wordCount),word_freq_penalty(beta,wordCount))+water(alpha, beta)
#    return word_freq_penalty(alpha,wordCount) + word_freq_penalty(beta,wordCount)

def words_match_score(alpha, beta,wordCount):
    try:
        alpha_rep = WordRep(alpha);
        beta_rep = WordRep(beta);
        score =  tibetan_words_match_score(alpha_rep, beta_rep)
        print score
        return score
    except:
        return -20;

def words_water(seq1, seq2,wordCount):
    #print "1: " + seq1
    #print "2: " + seq2
    seq1_words = seq1.split()
    seq2_words = seq2.split()
    m, n = len(seq1_words), len(seq2_words)  # length of two sequences

    # Generate DP table and traceback path pointer matrix
    score = zeros((m+1, n+1))      # the DP table
    pointer = zeros((m+1, n+1))    # to store the traceback path

    max_score = 0        # initial maximum score in DP table
    # Calculate DP table and mark pointers
    for i in range(1, m + 1):
        for j in range(1, n + 1):
            score_diagonal = score[i-1][j-1] + words_match_score(seq1_words[i-1], seq2_words[j-1],wordCount)
            score_up = score[i][j-1] + word_freq_penalty(seq1_words[i-1],wordCount)
            score_left = score[i-1][j] + word_freq_penalty(seq2_words[j-1],wordCount)
            score[i][j] = max(0,score_left, score_up, score_diagonal)
            if score[i][j] == 0:
                pointer[i][j] = 0 # 0 means end of the path
            if score[i][j] == score_left:
                pointer[i][j] = 1 # 1 means trace up
            if score[i][j] == score_up:
                pointer[i][j] = 2 # 2 means trace left
            if score[i][j] == score_diagonal:
                pointer[i][j] = 3 # 3 means trace diagonal
            if score[i][j] >= max_score:
                max_i = i-1
                max_j = j-1
                max_score = score[i][j];

    align1, align2 = [], []    # initial sequences

    i,j = max_i+1,max_j+1    # indices of path starting point

    #print "End: ", i,j

	# TO DO: FIX!!
    #traceback, follow pointers
    while pointer[i][j] != 0:
        #print pointer[i][j]
        if pointer[i][j] == 3:
            align1.append(seq1_words[i-1])
            align2.append(seq2_words[j-1])
            i -= 1
            j -= 1
        elif pointer[i][j] == 2:
            align1.append('-'*len(seq2_words[j-1]))
            align2.append(seq2_words[j-1])
            j -= 1
        elif pointer[i][j] == 1:
            align1.append(seq1_words[i-1])
            align2.append('-'*len(seq1_words[i-1]))
            i -= 1

    # align1.reverse()
    # align2.reverse()
    #print "Start: ", i,j

    #finalize(align1, align2)
    # print
    # print ' '.join(align1)
    # print
    # print ' '.join(align2)

    words_before_sizes_i = map(len,seq1_words[0:i])
    i = sum(words_before_sizes_i)+len(words_before_sizes_i)

    words_before_sizes_j = map(len,seq2_words[0:j])
    j = sum(words_before_sizes_j)+len(words_before_sizes_j)

    words_before_sizes_maxi = map(len,seq1_words[0:max_i])
    max_i = sum(words_before_sizes_maxi)+len(words_before_sizes_maxi)+len(seq1_words[max_i])

    words_before_sizes_maxj = map(len,seq2_words[0:max_j])
    max_j = sum(words_before_sizes_maxj)+len(words_before_sizes_maxj)+len(seq2_words[max_j])

    return (i,j,max_i,max_j,round(max_score))

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
    return t[startOne:endOne]

path1 = r"C:\Users\Daniel\Documents\University\tesis\project\texts\1.txt"
path2 = r"C:\Users\Daniel\Documents\University\tesis\project\texts\2.txt"

seq1 = print_section(path1,1254,1573)
print "-----------------------"
seq2 = print_section(path2,111143,111353)
word_count = dict()
res = words_water(seq1, seq2,word_count)
rseq1 = print_section(path1,1254 + res[0],1573+res[2])
rseq2 = print_section(path2,111143+ res[1],111353+res[3])