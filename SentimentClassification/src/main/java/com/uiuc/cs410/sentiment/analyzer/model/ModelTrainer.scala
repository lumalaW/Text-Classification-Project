package com.uiuc.cs410.sentiment.analyzer.model

 import org.apache.spark.SparkContext
 import org.apache.spark.mllib.classification.NaiveBayesModel
 import org.apache.spark.mllib.classification.NaiveBayes
 import org.apache.spark.mllib.util.MLUtils
 import org.apache.spark.mllib.regression.LabeledPoint
 import org.apache.spark.sql.SQLContext
 import org.apache.spark.sql.Row
 import org.apache.spark.sql.Dataset
 import org.apache.spark.rdd.RDD
 
 import java.util.Enumeration;
 import java.net.URL;
 
 import com.uiuc.cs410.sentiment.analyzer.TextParser;
 
object ModelTrainer {
  
  val cache = collection.mutable.Map[String, NaiveBayesModel]()
  
  println("Model Trainer initialized")
  
  def trainCSV(csvFile: String, context: SparkContext): NaiveBayesModel = {
    
    val cachedModel = cache.get(csvFile)
    if(cachedModel != null)
    {
      println("Returning pretrained model")
      cachedModel
    }
    
    println("training model from CSV file")

     var sqlContext = SQLContext.getOrCreate(context)
     val tweetDataFrame = sqlContext.read
        .format("com.databricks.spark.csv")
        .option("delimiter", ",")
        .load(csvFile)
        .toDF("tweet_id","sentiment","airline_sentiment_confidence","negativereason","negativereason_confidence","airline",
            "airline_sentiment_gold","name","negativereason_gold","retweet_count","text","tweet_coord","tweet_created","tweet_location",
            "user_timezone")
            
     val labeledRdd = tweetDataFrame.select("sentiment","text").rdd
        .map{ //turn the tweet text into features
          case Row(sentiment: String, text:String) =>
            val cleanedTweetTokens = TextParser.cleanAndTokenizeText(text)
            val featuresVector = TextParser.generateWordCountVector(cleanedTweetTokens)
            var sentimentDouble = 0.0
            if(sentiment.equalsIgnoreCase("positive"))
                sentimentDouble=1.0
            else if(sentiment.toLowerCase().equalsIgnoreCase("negative"))
                sentimentDouble=(-1.0)
            LabeledPoint(sentimentDouble, featuresVector)
          case _ =>
            LabeledPoint(0.0, TextParser.generateWordCountVector("".split("\0")))
     }
        
    val model = NaiveBayes.train(labeledRdd, lambda=1.0, modelType="multinomial")
    cache.put(csvFile, model)
      model
  }
    
  def trainTSV(tsvFile: String, context: SparkContext): NaiveBayesModel = {
    if(cache.contains(tsvFile))
    {
      val cachedModel : NaiveBayesModel= cache.get(tsvFile).get
      if(cachedModel != null)
      {
        println("Returning pretrained model")
        return cachedModel
      }
    }
        
    println("training model from TSV file")
   
    var sqlContext = SQLContext.getOrCreate(context)
    var textDataFrame = sqlContext.read
        .format("com.databricks.spark.csv")
        .option("delimiter", "\t")
        .load(tsvFile)
        .toDF("PhraseId","SentenceId","Phrase","Sentiment")
        
      val header = textDataFrame.first()
      textDataFrame = textDataFrame.filter(row => row != header)

      val labeledRdd = textDataFrame.select("Phrase","Sentiment").rdd
        .map{ //turn the text into features
          case Row(phrase:String, sentiment: String) =>
            val cleanedTextTokens = TextParser.cleanAndTokenizeText(phrase)
            val featuresVector = TextParser.generateWordCountVector(cleanedTextTokens)
            var sentimentDouble = sentiment.toDouble
            LabeledPoint(sentimentDouble, featuresVector)
          case _ =>
            LabeledPoint(0.0, TextParser.generateWordCountVector("".split("\0")))
      }
        
      val model = NaiveBayes.train(labeledRdd, lambda=1.0, modelType="multinomial")
      cache.put(tsvFile, model)
      model
  }
  def loadModel(context: SparkContext, modelPath: String): NaiveBayesModel = {
    println("Loading model")
    var model = NaiveBayesModel.load(context, modelPath)
    model
  }
  
  def saveModel(context: SparkContext,modelPath: String, model: NaiveBayesModel): Unit = {
    println("Saving model")
    model.save(context, modelPath)
  }
  
}
