# Text-Classification-Project

EmailRouter is a utility that seeks to expedite the process of monitoring and routing of customer emails to interested parties according to
both the topic categorization of the email as well as the sentiment of the email's author. Essentially, using the Stanford CoreNLP library
to perform sentiment analysis on text and the Scikit-learn to classify the text, we use the rules engine to decide to whom an email should
be routed to.

To perform topic categorization, users must provide the tool  of the tool may define 

## Sentiment Classification Service

The Sentiment Classification Microservices is built upon existing technology developed by Stanford’s NLP group.

https://nlp.stanford.edu/

The Stanford CoreNLP library performs multiple Natural Language Processing tasks on the text from each email such as Parts of Speech tagging and Entity Extraction, then uses a Recurrent Neural Network (RNN) to provide Sentiment Analysis from a model that as been pretrained at Stanford.
EmailRouter invokes the CoreNLP library via a custom RESTful microservice built upon the Apache Spark framework.

Each sentence is graded on a scale from Very Negative to Very Positive, and the scores are averaged to calculate the overall sentiment of the Email.

Once deployed, the stateless service provides a classify() method to score the text provided, and returns the results as a JSON object.

Request: 
	GET http://localhost:4567/classify&text=‘Im+very+angry+with+my+service!+Call+me!’
Request: 
	{
		score: 1.5,
		text: “I’m very angry with my service! Call me!”
	}


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
and both algorithm had the same performance on each of the 10 folds and hence the same average of 97.9%.

### Algorithm selection
The Naive Bayes algorithm was chosen in the end because it is simple (relies on counting the number of words and using conditional probabilities) and
it converges faster than discriminative models like logistic regression, so you need less training data. Essentially, it is fast and performs really
well.

### Training the Service

By default, the Document Classification Service has been trained over example sample provided by our team.

To customize the Service to suit your organization’s needs you must collect examples of the emails that you’d like the service to recognize and classify.

At minimum, the Document Classification Service requires 25 examples of each unique email classification, however providing significantly more examples is highly recommended and will increase classification accuracy. 

Collect these example and separate them into category folders.  The name of each folder will serve as the class type for all documents inside. The name is not important for the examples inside each folder.

Once an adequate number of samples have been collected for every category of email, make sure the Document Training Service has been started and call the train method to retrain.

From a web browser or a RESTful web service utility such as Postman, run the following request:

	http://<HOST>:<PORT>/train?examples_dir=<PATH>

Where HOST is the system that your service that the service is running on, PORT is the correct port for the service, and PATH is the location of the examples to train on.

	Example:  http://localhost:5000/train?examples_dir=./examples

If the service trains successfully, a message similar to this one will be returned.  The labels field contains a set of categories that the service now recognizes.  Save this information for later.

	{"labels": ["hotel", "movie", "product", "restaurant"], "message": "Finished Model Retraining."}


## Google Mail API Integration

Building and the Service:
1. Create a Gmail Account
-This will be both the account that is monitored by EmailRouter, and the account used to forward emails.
Make sure the Browser is logged into this Gmail account at all times.

-Generate a Secret Key File
Follow the instructions to enable access to your Gmail Account: https://console.developers.google.com/flows/enableapi?apiid=gmail 

2. Generate a Credential to Access Application Data.

Download the generated client_id.json file and save it for later use.
Provide EmailRouter with access to the Credential by providing the path to your Secret Key file in EmailRouter’s configuration files.


## Configuration

### Configuring of Mail Lists

For each Email Category, configure a <CATEGORY>.properties file where CATEGORY is the name of the Email class you expect to be returned from the Document Classification Service.

Within each file you may define a different email list for every sentiment type returned by the Sentiment Classification Service.  

Supported Sentiment Types are: angry, negative, neutral, positive, very_positive

For each sentiment level you may define a To, Cc, and Bcc list.  


<div align="center">
  <img src="https://github.com/lumalaW/Text-Classification-Project/blob/master/images/email_lists.png"><br><br>
</div>


### Port Configuration

Configure the EmailRouter Properties to Suit Your Organization's Needs.  From the EmailRouter/conf directory:

Modify the general.properties file

- Provide the Host and Port to both services

- Provide the path to your client_id file

- (OPTIONAL) Change the wait interval for email checks.

## Launching the Tool

### Prerequisites.

####  Sentiment Classifier Service Prerequisites
- Java 1.8 JDK
- Apache Maven

#### Document Classification Service Prerequisites
- Python 3
- Scikit-learn
- Flask
- Pandas

#### EmailRouter Program Prerequisites
- Java 1.8 JDK
- Gradle 2+

## Rebuilding Building the Tool

## Example Usage