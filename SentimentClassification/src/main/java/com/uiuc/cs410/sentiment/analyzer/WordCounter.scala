package com.uiuc.cs410.sentiment.analyzer

//import com.uiuc.cs498.netpromoter.analyzer.busobj.TweetTopWord;
//import com.uiuc.cs498.netpromoter.analyzer.busobj.TweetTopWord
//import com.uiuc.cs498.netpromoter.analyzer.busobj.TweetTopWord

object WordCounter {
  var topWords: java.util.Map[String, Integer] = new java.util.HashMap[String, Integer]()
  
  def getWordCounts(): java.util.Map[String, Integer] = {
    topWords
  }
  
  def incrementWords(tokens: Seq[String]): Unit = {
    tokens.foreach { x => {
      
      if(topWords.containsKey(x)){
        var current: Int =0
        try{
          current = topWords.get(x)
        }catch{
          case e: Exception => println(e.getMessage)
        }
        topWords.remove(x)
        topWords.put(x, new Integer(current+1))
      }
      else{
        topWords.put(x, new Integer(1))
      }
    }
    }
  }
  
  def clear(): Unit = {
    topWords.clear()
  }
}