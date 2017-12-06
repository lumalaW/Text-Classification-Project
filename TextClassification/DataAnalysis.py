# A file to analyze the data and split it into training and testing data before testing different models
import pandas as pd
from sklearn.cross_validation import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer

from TextClassification.DataPreparation import remove_stop_words

# Load the data
fileName = "Examples.csv"
names = ['Text', 'Category']
dataFrame = pd.read_csv(fileName, header=None, names=names)  # The dataframe

# Get some of the data statistics e.g number of examples, number of categories.
print(dataFrame.shape)
print(dataFrame.head(10))
print(dataFrame.Category.value_counts())


# Method to remove all the stop words in the training data..(iterate over the entire list)
def modify_text(the_list):
    for i in range(0, len(the_list)):
        new_text = remove_stop_words(the_list[i])
        the_list[i] = new_text

modify_text(dataFrame.Text) # Use the above method to remove stop words from the data
print(dataFrame.head(10))

# Convert to a numerical response (not necessary to the performance of the models)
dataFrame['Class'] = dataFrame.Category.map({'Hotel Review': 1, 'Movie Review': 2, 'Product Review': 3, 'Restaurant Review':4})

# The feature and the category. X = features and Y is the class
X = dataFrame.Text
y = dataFrame.Class

# Split data into training and testing data
X_train, X_test, y_train, y_test = train_test_split(X, y, random_state=1)

# Convert the train / test data into vectors...using TFIDF vectorization
vector = TfidfVectorizer()
vector.fit(X_train)
X_train_dtm = vector.transform(X_train)
print(X_train_dtm.shape)
X_test_dtm = vector.transform(X_test)
print(X_test_dtm.shape)

# Convert the cross validation data into vectors...using TFIDF vectorization
cv_vector = TfidfVectorizer()
cv_vector.fit(X)
X_cv_dtm = cv_vector.transform(X)
print(X_cv_dtm.shape)

