def IsUnique(s):
    usedChars = [False for x in range(256)];
    for letter in list(s):
        if usedChars[ord(letter)]:
            return False;
        usedChars[ord(letter)] = True;

    return True;

#print IsUnique("abcdefghijklmnop123456&!~")
#print IsUnique("abcdefghijklmnop1234i56&!~")


def my_reverse(s):
    n = len(s);
    end_p = n-2;
    start_p = 0;
    while end_p > start_p:
        tmp = s[start_p]
        s[start_p] = s[end_p]
        s[end_p] = tmp;
        start_p +=1;
        end_p -= 1;

def remove_dups(s):
    if s is None:
        return;
    if len(s) < 2:
        return;
    tail = 1;
    number_of_deletions =0;
    for i in range(1,len(s)):
        char_found = False;
        for j in range(tail):
            if s[i] == s[j]:
                char_found = True;
                break;

        if not char_found:
            s[tail] = s[i];
            tail +=1;
        else:
            number_of_deletions +=1;
    for x in range(number_of_deletions):
        s.pop();

def IsAnagrams(s1,s2):
    if len(s1) <> len(s2):
        return False;
    s1Chars = list(s1)
    s2Chars = list(s2);
    s1CharsCount = [0 for x in range(256)];
    for letter in s1Chars:
        s1CharsCount[ord(letter)] += 1;

    for letter in s2Chars:
        if s1CharsCount[ord(letter)] == 0:
            return False;
        s1CharsCount[ord(letter)] -= 1;

    return True;

