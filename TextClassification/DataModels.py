# Building and evaluating models
from TextClassification import DataAnalysis

X_train_dtm = DataAnalysis.X_train_dtm
y_train = DataAnalysis.y_train
X_test_dtm = DataAnalysis.X_test_dtm
y_test = DataAnalysis.y_test
X_cv_dtm = DataAnalysis.X_cv_dtm
y = DataAnalysis.y

# Naive Bayes Algorithm
print("Naive Bayes algorithm")
from sklearn.naive_bayes import MultinomialNB
NB = MultinomialNB()
print(NB)
NB.fit(X_train_dtm, y_train)

# Predictions
y_predictions = NB.predict(X_test_dtm)

# Accuracy
from sklearn import metrics
classification_accuracy = metrics.accuracy_score(y_test, y_predictions)
print(classification_accuracy)

confusion_matrix = metrics.confusion_matrix(y_test, y_predictions)
print(confusion_matrix)

# Logistic Regression
print("Logistic Regression")
from sklearn.linear_model import LogisticRegression
logreg = LogisticRegression()
print(logreg)
logreg.fit(X_train_dtm, y_train)

# Predictions
y_predictions = logreg.predict(X_test_dtm)

# Accuracy
classification_accuracy = metrics.accuracy_score(y_test, y_predictions)
print(classification_accuracy)

confusion_matrix = metrics.confusion_matrix(y_test, y_predictions)
print(confusion_matrix)


print("Using Cross Validation")
from sklearn.cross_validation import cross_val_score
NB = MultinomialNB()
NB_score = cross_val_score(NB, X_cv_dtm, y, cv=10, scoring='accuracy')
print(NB_score)
print(NB_score.mean())

LR = LogisticRegression()
LR_score = cross_val_score(NB, X_cv_dtm, y, cv=10, scoring='accuracy')
print(LR_score)
print(LR_score.mean())