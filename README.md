# Text-Classification-Project

EmailRouter is a utility that seeks to expedite the process of monitoring and routing of customer emails to interested parties according to
both the topic categorization of the email as well as the sentiment of the email's author. Essentially, using the Stanford CoreNLP library
to perform sentiment analysis on text and the Scikit-learn to classify the text, we use the rules engine to decide to whom an email should
be routed to.

To perform topic categorization, users must provide the tool  of the tool may define 

## Sentiment Classification Service

## Document Classification Service
The text classification was done using the Scikit-learn library. Initially released in 2007, Scikit-learn is a free software machine learning
library for the Python programming language. For this project, the Naive Bayes (multinomial) algorithm was used. For the purpose of text
classification and in general machine learning with text, Scikit-learn provides a lot of useful tools and methods besides the algorithms
such as vectorizing the text data or removing some common terms among others.
Before, selecting the algorithm, both a generative (Naive Bayes) and discriminative (Logistic) classifier were trained and tested using labeled data,
essentially supervised learning. This labeled data (in the form of a csv file) contained 100 examples i.e the text and its category. The data was
split into four categories with 25 examples per category. The four categories included; Hotel Review, Movie Review, Product Review and Restaurant Review.
Our assumption was that the company X had four major kinds of businesses and would like their emails from customers or business partners to be
categorized automatically before being forwarded to the right person.

### The training process
The text data was vectorized before it was used for training and testing the algorithms. The vectorization (count vectorization) entailed getting the
number of times a unique word appeared in an example. Different metrics were used to judge the algorithms; Classification accuracy (i.e what percentage
of the examples were corrected classified?), Confusion matrix and Cross-validation. The Confusion matrix is an N by N matrix where N is the number of
categories. This is useful as it shows the number of correct and incorrect predictions made by the model compared to the actual outcomes (target value).
This matrix is used to show mre details about the classification accuracy. Cross-validation has an added advantage of using each example in the data set for both
training and testing.

The only features used were the words in the text i.e. the number of times a unique term appeared in a text example. The vectorization was done in two different
ways. For the Cross-validation metrics, since all the examples are used for both training and testing the algorithms, the vectorization was done for all the examples.
For the Classification Accuracy metrics, only the training data was vectorized. This was done because real world applications of text classification expect to see
words in the data that were not seen in the training process.

For the first trial, the data was split into training and test data. 75 examples were was to train the model and 25 were used to test the algorithms.
Classification accuracy was used to determine which algorithm performed better. Logistic Regression performed slightly better as it 96% correct compared to
92% by Naive Bayes. Using the confusion matrix (not shown) we can see which category(s) were most misclassified.

For the second trial, stop words were removed. There was an improvement in both of the algorithms as the classified all the test examples correctly.

For the third trial, the vectorizer used also took into consideration the type of terms (frequent or rare). A Term Frequency - Inverse Document Frequency (TF-IDF)
vectorizer was used. The performance declined in this trial to a classification accuracy of 92% for both algorithms.

The last run did not change the data but rather the metrics used to measure the performance of each algorithm. The Cross-validation measurement was used
and both algorithm had the same performance on each of the 10 folds and hence the same average of 97.9%

### Algorithm selection

### Training the Service


## Google Mail API Integration

## Configuration

### Configuring of Mail Lists

EmailRouter allows for the configuration of 

https://github.com/lumalaW/Text-Classification-Project/blob/master/images/email_lists.png

<div align="center">
  <img src="https://github.com/lumalaW/Text-Classification-Project/blob/master/images/email_lists.png"><br><br>
</div>


### Port Configuration

## Launching the Tool

### Prerequisites.

## Rebuilding Building the Tool

## Example Usage