def find_root(letters):
    rep = WordRep();
    # we start by finding the vowel
    #vowel = '';
    for vowel in vowels:
        if vowel in letters:
            rep.vowel = vowel;
            rep.vowel_index = letters.index(vowel);
            break;
    if rep.vowel == '':
        raise Exception("no vowel");

    # in order to find prefix and suffix (if exists) we split the word to before and after the vowel
    before_vowel = letters[0:rep.vowel_index];
    after_vowel = letters[rep.vowel_index+1 : ];


    if len(before_vowel) == 0:
        raise Exception("word starts with vowel");
    #prefix = ''
    #check if the word has prefix

    # if single letter - I assume it is the root letter
    if len(before_vowel) == 1:
        rep.stacked_letter = before_vowel[0];
    # if more than 4 letters - it should not occur
    if len(before_vowel) > 4:
        raise Exception("word starts too many letters before the vowel");
    # if 4 letters - it should be: prefix + superscript + root letter + subscript
    if len(before_vowel) == 4:
        if before_vowel[0] in possible_prefixes and before_vowel[1] in possible_superscripts and before_vowel[3] in possible_subscripts:
            rep.prefix = before_vowel[0];
            rep.stacked_letter = "".join(before_vowel[1:]);
        else:
            raise Exception("unknown sturcture");
    # if 3 letters - I try in this order:
    # superscript + root letter + subscript
    # prefix + root letter + subscript
    # prefix + superscript + root letter
    # unknown structure
    if len(before_vowel) == 3:
        if before_vowel[0] in possible_superscripts and before_vowel[2] in possible_subscripts:
            rep.stacked_letter = "".join(before_vowel);
        elif before_vowel[0] in possible_prefixes and before_vowel[2] in possible_subscripts:
            rep.prefix = before_vowel[0];
            rep.stacked_letter = "".join(before_vowel[1:]);
        elif before_vowel[0] in possible_prefixes and before_vowel[1] in possible_superscripts:
            rep.prefix = before_vowel[0];
            rep.stacked_letter = "".join(before_vowel[1:]);
        else:
            raise Exception("unknown sturcture");

    # if 2 letters - I try in this order:
    # root letter + subscript
    # superscript + root letter
    # prefix + root letter
    # unknown structure
    if len(before_vowel) == 2:
        if before_vowel[1] in possible_subscripts:
            rep.stacked_letter = "".join(before_vowel);
        elif before_vowel[0] in possible_superscripts:
            rep.stacked_letter = "".join(before_vowel);
        elif before_vowel[0] in possible_prefixes:
            rep.prefix = before_vowel[0];
            rep.stacked_letter = "".join(before_vowel[1:]);
        else:
            raise Exception("unknown sturcture");

    # finding final letter + suffix (if they exists)
    #suffix = '';
    #final_letter = '';
    # if no final letter + suffix - we do nothing

    # if one letter: it should be a final letter. o.w unknown structure
    if len(after_vowel) == 1:
        if after_vowel[0] in possible_final_letters:
            rep.final_letter = after_vowel[0];
        else:
            raise Exception("the word end with suffix without a final letter");

    # if 2 letters: it should be a final letter + suffix. o.w unknown structure
    if len(after_vowel) == 2:
        if after_vowel[0] in possible_final_letters and after_vowel[1] in possible_suffixes:
            rep.final_letter = after_vowel[0];
            rep.suffix = after_vowel[1];

    return rep;
