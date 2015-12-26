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

# initilize vowels_subs
vowels_subs = dict()
for vowel in vowels:
    vowels_subs[vowel] = dict()
    for vowel2 in vowels:
        if vowel <> vowel2:
            vowels_subs[vowel][vowel2] = 0;

def add_vowel_subs(v1, v2):
    vowels_subs[v1][v2] +=1;
    vowels_subs[v2][v1] +=1;
    #print v1 + " " + v2;

import json

def write_vowel_subs_to_file():
    json.dump(vowels_subs, open("vowels_subs.txt",'w'))

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
        print alpha.word + " " + beta.word
        return EXACT_ROOT_SCORE;
    # same roots but the vowel changed:
    elif alpha.GetRootWithoutVowel() == beta.GetRootWithoutVowel():
        add_vowel_subs(alpha.vowel, beta.vowel)
        #print alpha.word + " " + beta.word
        return DIFF_VOWEL;
    return ERROR_WEIGHT * levenshtein(alpha.word, beta.word);





letters = split_word_to_letters("byung");
#print letters
rep = WordRep("byung")
alpha = WordRep("sgrubs")
beta = WordRep("bsgrub")
#print tibetan_words_match_score(alpha, beta)
#find_root(letters)
#chars =  list("bsgrubs");
#print "".join(chars[3:])