execfile("tibetanWordMatcher.py")

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
    return -1

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
        #print score
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
    write_vowel_subs_to_file()
    return (i,j,max_i,max_j,round(max_score))

