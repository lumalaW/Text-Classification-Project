# This file contains methods that are used to prepare the text data
# Method to get the stop words from the file
array = []
with open("stopwords.txt", "r") as ins:
    for line in ins:
        line = line.rstrip('\n')
        array.append(line)

# Method to remove stop words when given a String


def remove_stop_words(text):
    text_words = text.split()
    result_words = [word for word in text_words if word.lower() not in array]
    result = ' '.join(result_words)
    return result

