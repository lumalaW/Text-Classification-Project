package com.uiuc.cs410.sentiment.analyzer.model

import java.util.Properties
import scala.collection.JavaConverters._
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.util.CoreMap
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import com.uiuc.cs410.sentiment.analyzer.TweetParser;
import java.util.HashMap;
import java.util.Map;

object StanfordNLPScorer {
  
  val stanfordPipeline: StanfordCoreNLP = {
    val properties = new Properties()
    properties.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment")
    new StanfordCoreNLP(properties);
  }
  
  def scoreTweet(tweetText: String): Int = {
    val cleanedTweet = TweetParser.cleanTweet(tweetText, true)
    val document: edu.stanford.nlp.pipeline.Annotation = new edu.stanford.nlp.pipeline.Annotation(cleanedTweet)
    stanfordPipeline.annotate(document)   
    var sentiment:Double = 0.0
    var counter:Int = 0
    var sentences: scala.collection.immutable.List[CoreMap] = (document.get(classOf[CoreAnnotations.SentencesAnnotation])).asScala.toList
    for (sentence <- sentences) {
      val tree = sentence.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])
      sentiment += RNNCoreAnnotations.getPredictedClass(tree)
      counter+=1
    }
   convertSentimentToScore(sentiment/counter)
   }
  
  def scoreText(text: String): (Double, HashMap[String, Double]) ={
    var map : HashMap[String, Double] = new HashMap()
    val cleanedTweet = text//TweetParser.cleanTweet(text, true)
    val document: edu.stanford.nlp.pipeline.Annotation = new edu.stanford.nlp.pipeline.Annotation(cleanedTweet)
    stanfordPipeline.annotate(document)   
    var overall_sentiment:Double = 0.0
    var sentence_count:Int = 0
    var sentences: scala.collection.immutable.List[CoreMap] = (document.get(classOf[CoreAnnotations.SentencesAnnotation])).asScala.toList
    for (sentence <- sentences) {
      val tree = sentence.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])
      val sentiment = RNNCoreAnnotations.getPredictedClass(tree)
      overall_sentiment += sentiment
      sentence_count+=1
      map.put(sentence.toString(), sentiment)
    }
    
    (overall_sentiment/sentence_count, map)
  }
  def convertSentimentToScore(sentiment: Double): Int = {
    if(sentiment > 2.0)
      return 10
    if(sentiment == 2.0)
      return 5
    return 1
  }
}