package com.uiuc.cs410.sentiment.analyzer

import com.uiuc.cs410.sentiment.analyzer.model.Predictor
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object TweetAnalyzer {
  
  //val AirlineData = "E:/UIUC/CS498/Project/Data/twitter-airline-sentiment/Tweets.csv"
  val MovieReviewData = "resources/data/movie_reviews/train.tsv"
  
  val TabDelimiter = "\t"
  val CommaDelimiter = ","
  
  def main(args: Array[String]): Unit = {
    
    val sparkConf = new SparkConf().setAppName("TweetAnalyzer").setMaster("local[*]").set("spark.executor.memory","1g");
    var sparkContext = new SparkContext(sparkConf)
    
    var predictor = new Predictor(sparkContext,"stanford")
    predictor.trainModel(MovieReviewData, TabDelimiter);
            
    var result = predictor.classifySentiment("great amazing fantastic")
//    println("Score: "+result._1)
    result = predictor.classifySentiment("hate terrible sucks")
//    println("Score: "+result._1)
    result = predictor.classifySentiment("meh whatever done")
//    println("Score: "+result._1)
    
    println("Normal exit")
  }
}