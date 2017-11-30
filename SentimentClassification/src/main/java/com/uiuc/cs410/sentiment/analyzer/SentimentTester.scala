package com.uiuc.cs410.sentiment.analyzer

import com.uiuc.cs410.sentiment.analyzer.model.Predictor
import com.uiuc.cs410.sentiment.analyzer.busobj.TextSentimentResult
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import scala.collection.JavaConverters._
import scala.io.Source
import com.uiuc.cs410.sentiment.analyzer.busobj.TextSentimentResult
import com.uiuc.cs410.sentiment.analyzer.busobj.TextSentimentResult



object SentimentTester {
  val MovieReviewData = "resources/data/movie_reviews/train.tsv"
  
  val TabDelimiter = "\t"
  val CommaDelimiter = ","
  
  val ANALYSIS_BREAK = " ################################# RESULTS ################################# "
  
  def main(args: Array[String]): Unit = {
    
    System.setProperty("hadoop.home.dir", "C:/CS410/FinalProj/Text-Classification-Project/SentimentClassification/resources" )
    val sparkConf = new SparkConf().setAppName("TweetAnalyzer").setMaster("local[*]").set("spark.executor.memory","1g");
    var sparkContext = new SparkContext(sparkConf)
    
    var stanfordCorePredictor = new Predictor(sparkContext,"stanford")
    stanfordCorePredictor.trainModel(MovieReviewData, TabDelimiter);
    
    var naiveBayesPredictor = new Predictor(sparkContext, "naiveBayes")
    naiveBayesPredictor.trainModel(MovieReviewData, TabDelimiter)
            
    var unscoredExamples = loadExamples(MovieReviewData).asScala.toSet
    var scoredExamples: java.util.List[TextSentimentResult] = new java.util.ArrayList[TextSentimentResult]()
    
    var count = 0
    
    unscoredExamples.foreach { example =>
      println("Example "+count)
      println("True score = "+example.score)
      var tuple = scoreBoth(example.text, stanfordCorePredictor, naiveBayesPredictor)
      println("True score = "+example.score)
      var scored = new TextSentimentResult()
        .text(example.text)
        .score(example.score)
        .stanfordPrediction(tuple._1)
        .naiveBayesPrediction(tuple._2)
       scoredExamples.add(scored)
       println("")
       count +=1
    }
    analyzeResults(scoredExamples)
    
//    scoreBoth("great amazing fantastic", stanfordCorePredictor, naiveBayesPredictor)
//    scoreBoth("meh whatever done", stanfordCorePredictor, naiveBayesPredictor)
    
    println("Normal exit")
  }
  
  def scoreBoth(text: String, stanfordCorePredictor: Predictor, naiveBayesPredictor: Predictor): (Double, Double) ={
    println("Classifying text: "+text)
    var stanford_result = stanfordCorePredictor.classifyText(text)
    var nb_result = naiveBayesPredictor.classifyText(text)
    (stanford_result.score, nb_result.score)
  }
  
  def loadExamples(data: String): java.util.List[TextSentimentResult] = {
    var list : java.util.List[TextSentimentResult] = new java.util.ArrayList[TextSentimentResult]()
    
    val lines = Source.fromFile(data).getLines.toArray
    lines.slice(1,lines.size).foreach { line => 
      var item = new TextSentimentResult()
      var tokens =line.split(TabDelimiter)
      item.text(tokens(2))
      item.score(tokens(3).trim().toInt)
      list.add(item)
    }
    list
  }
    
  def analyzeResults(results: java.util.List[TextSentimentResult]): Unit = {
    
    println(ANALYSIS_BREAK)
    
    var correctStanford = 0.0
    var tooLowStanford = 0.0
    var tooHighStanford = 0.0
    
    var correctNaiveBayes = 0.0
    var tooLowNaiveBayes = 0.0
    var tooHighNaiveBayes = 0.0
    
    var totalExamples = 0.0
    
    results.asScala.toArray.foreach { result =>
      totalExamples += 1.0
      if(result.score.toDouble == result.stanfordPrediction)
        correctStanford+=1.0
      else if(result.score.toDouble > result.stanfordPrediction)
        tooLowStanford+=1.0
      else
        tooHighStanford+=1.0
      if(result.score.toDouble == result.naiveBayesPrediction)
        correctNaiveBayes+=1.0
      else if(result.score.toDouble > result.stanfordPrediction)
        tooLowNaiveBayes+=1.0
      else
        tooHighNaiveBayes+=1.0
    }
    
    println("Stanford CoreNLP Accuracy: "+correctStanford/totalExamples)
    println("     "+correctStanford.toInt+"/"+totalExamples.toInt)
    println("Stanford CoreNLP Scores Too Highly: "+tooHighStanford/totalExamples)
    println("     "+tooHighStanford.toInt+"/"+totalExamples.toInt)
    println("Stanford CoreNLP Scores Too Low: "+tooLowStanford/totalExamples)
    println("     "+tooLowStanford.toInt+"/"+totalExamples.toInt)
    println("")
    println("NaiveBayes Accuracy: "+correctNaiveBayes/totalExamples)
    println("     "+correctNaiveBayes.toInt+"/"+totalExamples.toInt)
    println("NaiveBayes Scores Too Highly: "+tooHighNaiveBayes/totalExamples)
    println("     "+tooHighNaiveBayes.toInt+"/"+totalExamples.toInt)
    println("NaiveBayes Scores Too Low: "+tooLowNaiveBayes/totalExamples)
    println("     "+tooLowNaiveBayes.toInt+"/"+totalExamples.toInt)
    println("")
    
  }
}