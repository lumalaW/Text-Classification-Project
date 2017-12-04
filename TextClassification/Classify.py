# The method to classify text
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB

from TextClassification.DataPreparation import remove_stop_words

# Get training data
names = ['Text', 'Category']
dataFrame = pd.read_csv("Examples.csv", header=None, names=names)  # The dataframe
dataFrame['Class'] = dataFrame.Category.map({'Hotel Review': 1, 'Movie Review': 2, 'Product Review': 3, 'Restaurant Review': 4})
X = dataFrame.Text    # X is the features (list of features)
y = dataFrame.Class   # y is the response (each set of features has a response)

# Fit the algorithm with all the training data
vector = TfidfVectorizer()
X = vector.fit_transform(X)
naive_bayes = MultinomialNB()
naive_bayes.fit(X, y)


# Method to classify a string or paragraph of text

def classify(text):
    result = remove_stop_words(text)
    result = [result, '']
    text_vector = vector.transform(result)
    ans = naive_bayes.predict(text_vector)
    if ans[0] == 1:
        return "Hotel Review"
    elif ans[0] == 2:
        return "Movie Review"
    elif ans[0] == 3:
        return "Product Review"
    else:
        return "Restaurant Review"
