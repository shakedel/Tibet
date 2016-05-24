def slice_file(file_name, start, end):
    f = open(file_name, "r")
    content = f.read()
    words = content.split()
    return " ".join(words[start:end])


print slice_file("enum_stem/Klong.txt", 8190, 8210)
print slice_file("enum_stem/phya.txt", 6415, 6440)
print slice_file("Klong.txt", 8190, 8210)
print slice_file("phya.txt", 6415, 6440)
