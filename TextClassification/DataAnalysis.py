# Data analysis and preparation
# Load the training data
import pandas as pd
from sklearn.cross_validation import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer

fileName = "Examples.csv"
names = ['Text', 'Category']
dataFrame = pd.read_csv(fileName, header=None, names=names)  # The dataframe

print(dataFrame.shape)
print(dataFrame.head(10))
print(dataFrame.Category.value_counts())  # Check the class distribution

# Remove the stop words... text file with stop words provided
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

# Method to modify a list of strings by removing stop words


def modify_text(the_list):
    for i in range(0, len(the_list)):
        new_text = remove_stop_words(the_list[i])
        the_list[i] = new_text

modify_text(dataFrame.Text)
print(dataFrame.head(10))

# Convert to a numerical response
dataFrame['Class']=dataFrame.Category.map({'Hotel Review':1,'Movie Review':2,'Product Review':3,'Restaurant Review':4})
print(dataFrame.head(10))

# Features and response (category)
X = dataFrame.Text
y = dataFrame.Class

print(X.shape)
print(y.shape)

# Split data into training and testing data
X_train, X_test, y_train, y_test = train_test_split(X, y, random_state=1)
print(X_train.shape)
print(X_test.shape)
print(y_train.shape)
print(y_test.shape)

# Vectorize the data
vector = TfidfVectorizer()
vector.fit(X_train)
X_train_dtm = vector.transform(X_train)
print(X_train_dtm.shape)
X_test_dtm = vector.transform(X_test)
print(X_test_dtm.shape)

# The data for cross-validation
vect = TfidfVectorizer()
vect.fit(X)
X_cv_dtm = vect.transform(X)
print(X_cv_dtm.shape)
