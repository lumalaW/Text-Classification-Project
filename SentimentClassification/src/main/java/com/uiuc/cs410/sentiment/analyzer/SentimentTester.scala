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
  
  val ANALYSIS_BREAK = " ################################# OVERALL ACCURACY ################################# "
  val ANALYSIS_BREAK_MAJORITY = " ################################# MAJORITY CLASS ACCURACY ################################# "
  
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
    
    //stanford scores
    var correctStanford = 0.0
    var tooLowStanford = 0.0
    var muchTooLowStanford = 0.0
    var tooHighStanford = 0.0
    var muchTooHighStanford = 0.0
    var stanfordMajCorrect = 0
    var stanfordMajFalsePos = 0
    var stanfordMajFalseNeg = 0
    
    //naive bayes scores
    var correctNaiveBayes = 0.0
    var tooLowNaiveBayes = 0.0
    var muchTooLowNaiveBayes = 0.0
    var tooHighNaiveBayes = 0.0
    var muchTooHighNaiveBayes = 0.0
    var naiveBayesMajCorrect = 0
    var naiveBayesMajFalsePos = 0
    var naiveBayesMajFalseNeg = 0
    
    //overall 
    var totalExamples = 0.0
    var counts = Array(0,0,0,0,0)
    var majorityClass = -1
    var majorityClasscount = 0
    
    //determine majority class
    results.asScala.toArray.foreach { result =>
      totalExamples += 1.0
      var score = result.score
      counts(score) += 1
    }
    
     for ( i <- 1 to (counts.length - 1) ) {
         if (counts(i) > majorityClasscount) {
           majorityClass = i
           majorityClasscount = counts(i);
         }
     }
   
    results.asScala.toArray.foreach { result =>
      
      if(result.score == majorityClasscount){
        
      }
      
      //Analyze StanfordCore
      //we got it right
      if(result.score.toDouble == result.stanfordPrediction){
        correctStanford+=1.0
      }
      //we got it wrong
      else {
        if(result.score.toDouble > result.stanfordPrediction){
            tooLowStanford+=1.0
            if(result.score.toDouble - result.stanfordPrediction > 1)
              muchTooLowStanford+=1.0
        }
        else{
          tooHighStanford+=1.0
          if(result.stanfordPrediction - result.score.toDouble > 1)
            muchTooHighStanford+=1.0
        }
      }
      
      if(result.score == majorityClass){
         if(result.stanfordPrediction == majorityClass)
            stanfordMajCorrect+=1
         else
            stanfordMajFalseNeg+=1
      }
      else{
        if(result.stanfordPrediction == majorityClass){
          stanfordMajFalsePos+=1
        }
      }

      
      //Analyze NB
      //we got it right
      if(result.score.toDouble == result.naiveBayesPrediction){
        correctNaiveBayes+=1.0
      }
      //we got it wrong
      else {
        if(result.score.toDouble > result.naiveBayesPrediction){
            tooLowNaiveBayes+=1.0
            if(result.score.toDouble - result.naiveBayesPrediction > 1)
              muchTooLowNaiveBayes+=1.0
        }
        else{
          tooHighNaiveBayes+=1.0
          if(result.naiveBayesPrediction - result.score.toDouble > 1)
            muchTooHighNaiveBayes+=1.0
        }
      }
      
      if(result.score == majorityClass){
         if(result.naiveBayesPrediction == majorityClass)
            naiveBayesMajCorrect+=1
         else
            naiveBayesMajFalseNeg+=1
      }
      else{
        if(result.naiveBayesPrediction == majorityClass){
          naiveBayesMajFalsePos+=1
        }
      }
    }
    
    //print results
    println("Stanford CoreNLP Exactly Accurately: "+correctStanford/totalExamples)
    println("     "+correctStanford.toInt+"/"+totalExamples.toInt)
    println("Stanford CoreNLP Scores Too Highly: "+tooHighStanford/totalExamples)
    println("     "+tooHighStanford.toInt+"/"+totalExamples.toInt)
    println("Stanford CoreNLP Scores Much Too Highly: "+muchTooHighStanford/totalExamples)
    println("     "+muchTooHighStanford.toInt+"/"+totalExamples.toInt)
    println("Stanford CoreNLP Scores Too Low: "+tooLowStanford/totalExamples)
    println("     "+tooLowStanford.toInt+"/"+totalExamples.toInt)
    println("Stanford CoreNLP Scores Much Too Low: "+muchTooLowStanford/totalExamples)
    println("     "+muchTooLowStanford.toInt+"/"+totalExamples.toInt)
    println("")
    println("NaiveBayes Scores Exactly Accurately: "+correctNaiveBayes/totalExamples)
    println("     "+correctNaiveBayes.toInt+"/"+totalExamples.toInt)
    println("NaiveBayes Scores Too Highly: "+tooHighNaiveBayes/totalExamples)
    println("     "+tooHighNaiveBayes.toInt+"/"+totalExamples.toInt)
    println("NaiveBayes Scores Much Too Highly: "+muchTooHighNaiveBayes/totalExamples)
    println("     "+muchTooHighNaiveBayes.toInt+"/"+totalExamples.toInt)
    println("NaiveBayes Scores Too Low: "+tooLowNaiveBayes/totalExamples)
    println("     "+tooLowNaiveBayes.toInt+"/"+totalExamples.toInt)
    println("NaiveBayes Scores Much Too Low: "+muchTooLowNaiveBayes/totalExamples)
    println("     "+muchTooLowNaiveBayes.toInt+"/"+totalExamples.toInt)
    println("")
    
    println(ANALYSIS_BREAK_MAJORITY)
    println("Majority Class: "+majorityClass)
    println("Majority Class Count: "+majorityClasscount)
    println("Majority Class Percentage: "+majorityClasscount/totalExamples)
    println("")
    println("Stanford Majority Predictions: "+(stanfordMajCorrect+stanfordMajFalsePos))
    println("Stanford Majority Precision: "+stanfordMajCorrect.toDouble/(stanfordMajCorrect+stanfordMajFalsePos))
    println("Stanford Majority Recall: "+stanfordMajCorrect.toDouble/(stanfordMajCorrect+stanfordMajFalseNeg))
    println("")
    println("NaiveBayes Majority Predictions: "+(naiveBayesMajCorrect+naiveBayesMajFalsePos))
    println("NaiveBayes Majority Precision: "+naiveBayesMajCorrect.toDouble/(naiveBayesMajCorrect+naiveBayesMajFalsePos))
    println("NaiveBayes Majority Recall: "+naiveBayesMajCorrect.toDouble/(naiveBayesMajCorrect+naiveBayesMajFalseNeg))
  }
}