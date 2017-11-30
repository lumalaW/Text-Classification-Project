package com.uiuc.cs410.sentiment.analyzer.busobj

import scala.collection.mutable.ListBuffer
import com.uiuc.cs410.sentiment.analyzer.WordCounter
import scala.collection.JavaConverters._

class TweetScore() {
  
  var score: Double = 0
  var posCount: Integer = 0
  var negCount: Integer = 0
  var neutralCount: Integer = 0
  var model: String = ""
  
  var analyzedTweets : java.util.List[TweetAnalysis] = new java.util.ArrayList[TweetAnalysis]()
  var topWords: java.util.List[TweetTopWord] = new java.util.ArrayList[TweetTopWord]()
  
  def getModel(): String = {
    this.model
  }
  
  def setModel(model: String): Unit = {
    this.model=model
  }
  
  def getScore(): Double = {
    this.score
  }
  
  def getPosCount(): Integer = {
    this.posCount
  }
  
  def getNegCount(): Integer = {
    this.negCount
  }
  
   def getNeutralCount(): Integer = {
    this.neutralCount
  }
   
  def getTweets(): java.util.List[TweetAnalysis] = {
    this.analyzedTweets
  }
  
  def getTopWords(): java.util.List[TweetTopWord] = {
    
    this.topWords
  }
  
  def addTweet(tweet: TweetAnalysis): TweetScore = {
    if(tweet.score >= 8)
      this.posCount+=1
    else if(tweet.score < 5)
      this.negCount+=1
    else
      this.neutralCount+=1
      
    this.analyzedTweets.add(tweet)
    this
    
  }
  
  def calcScore() : TweetScore = {
    val count: Integer = posCount+negCount+neutralCount
    
    val percentPos : Double = (posCount.doubleValue()/count) * 100.0
    val percentNeg : Double = (negCount.doubleValue()/count) * 100.0
    
    this.score = (percentPos - percentNeg)
    this
  }
  
  def calcWordCounts(searchTerm: String): TweetScore = {
    var words: java.util.Map[String, Integer] = WordCounter.getWordCounts()
    
    var iteration = 0
    var maxIteration = 102
    
    while(iteration < maxIteration){
      var highestVal: Int = 0
      var highestWord = ""
      for((k,v) <- words.asScala.toSet){
         if(v > highestVal){
          highestWord = k
          highestVal = v
        }
      }
      if(highestVal>0){
        if(highestWord!="rt" && highestWord!=searchTerm){
          var tweetWord = new TweetTopWord(highestWord, highestVal)
          this.topWords.add(topWords.size(), tweetWord)
        }
        words.remove(highestWord)
      }
      iteration+=1
    }    
    this
  }
  
  
}