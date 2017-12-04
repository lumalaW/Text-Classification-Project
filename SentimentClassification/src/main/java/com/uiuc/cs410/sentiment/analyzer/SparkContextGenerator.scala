package com.uiuc.cs410.sentiment.analyzer

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;

object SparkContextGenerator {
  
  var context: SparkContext = null
  
  def getContextInstance() : SparkContext = {
    if(context==null){
      var sparkConf = new SparkConf().setAppName("TweetNetPromoter")
        			.setMaster("local[*]")
        			.set("spark.sql.warehouse.dir", "file:///E:/UIUC/CS498/TweetNetPromoter/")
        			.set("spark.executor.memory","1g")
      context = new SparkContext(sparkConf)
    }
    context
  }
}