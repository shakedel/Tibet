f1 = open("Klong.txt", "r")
f2 = open("phya.txt", "r")

alignments_file = open("correct alignment.csv", "r")
output_file = open("correct_alignment_spans.csv", "w")

def file_to_list(f):
    res = []
    for line in f:
        res.extend(line.split(" "))
    return res

def find_span_in_text(text_arr, file_content):
    first_word = text_arr[0]
    for i in range(len(file_content)):
        if first_word == file_content[i]:
            if text_arr == file_content[i:i+len(text_arr)]:
                return i, i+len(text_arr)
    return None

def get_text_by_span(span, file_content):
    return " ".join(file_content[span[0]: span[1]])


def load_csv(alignments):
    alignments.readline()
    file1 = file_to_list(f1)
    file2 = file_to_list(f2)
    i = 1
    for line in alignments:
        lst = line.split(",")
        first = lst[1].strip().split()
        second = lst[2].strip().split()
        first_span = find_span_in_text(first, file1)
        second_span = find_span_in_text(second, file2)
        print i , (first_span), (second_span)
        output_file.write(str(first_span[0]) + "," + str(second_span[0])  + "," + str(first_span[1]) + "," + str(second_span[1]) + ",1") 
        output_file.write("\n")
        i += 1
    output_file.close()
load_csv(alignments_file)
