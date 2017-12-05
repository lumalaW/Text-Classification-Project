# The method to classify text
import pandas as pd
import csv
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB

from TextClassification.DataPreparation import remove_stop_words

def getLabelsDict(categories_file="./labels.csv"):
    d = {}
    with open(categories_file, 'rt') as csvfile:
        labels = csv.reader(csvfile, delimiter=' ', quotechar='|')
        i = 1
        for row in labels:
            s = row[0]
            # print("Row {} = {}".format(i, row[0]))
            d[row[0]]=i
            i+=1
    return d

# Get training data
names = ['Text', 'Category']
dataFrame = pd.read_csv("Data.csv", header=None, names=names)  # The dataframe
dataFrame['Class'] = dataFrame.Category.map(getLabelsDict())
X = dataFrame.Text    # X is the features (list of features)
y = dataFrame.Class   # y is the response (each set of features has a response)

# Fit the algorithm with all the training data
vector = TfidfVectorizer()
X = vector.fit_transform(X)
naive_bayes = MultinomialNB()
naive_bayes.fit(X, y)
labels = getLabelsDict()


# Method to classify a string or paragraph of text

def classify(text):
    result = remove_stop_words(text)
    result = [result, '']
    text_vector = vector.transform(result)
    ans = naive_bayes.predict(text_vector)
    category = ""
    c = lookup_category_from_number(ans[0])
    return c
    # if ans[0] == 1:
    #     return "Hotel Review"
    # elif ans[0] == 2:
    #     return "Movie Review"
    # elif ans[0] == 3:
    #     return "Product Review"
    # else:
    #     return "Restaurant Review"

def train_model(samples_file, categories_file):
    print("Loading samples from {}".format(samples_file))
    names = ['Text', 'Category']
    dataFrame = pd.read_csv(samples_file, header=None, names=names)  # The dataframe
    print("Loading labels from {}".format(categories_file))
    classes_dict = getLabelsDict(categories_file)
    dataFrame['Class'] = dataFrame.Category.map(classes_dict)
    X = dataFrame.Text    # X is the features (list of features)
    y = dataFrame.Class   # y is the response (each set of features has a response)

    # Fit the algorithm with all the training data
    print("Retraining....")
    vector = TfidfVectorizer()
    X = vector.fit_transform(X)
    naive_bayes = MultinomialNB()
    naive_bayes.fit(X, y)
    return "Finished Model Retraining."

def lookup_category_from_number(number):
    for row in labels:
        value = labels[row]
        if value==number:
            return row
    return "unknown"

