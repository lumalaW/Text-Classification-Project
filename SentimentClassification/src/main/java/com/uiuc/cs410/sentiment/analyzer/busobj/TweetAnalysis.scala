package com.uiuc.cs410.sentiment.analyzer.busobj;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.mllib.linalg.Vector;

class TweetAnalysis() {

	var tweetText: String = ""
	var score : Integer = -1
	var probabilities : Vector = null
	var counts : Map[String, Integer] = new HashMap();
	

	def getTweetText(): String = {
		tweetText
	}

	def tweetText(tweetText: String): TweetAnalysis = {
		this.tweetText = tweetText
		this
	}

	def score(score :Integer):TweetAnalysis = {
		this.score = score
		this
	}

	def getProbabilities(): Vector = {
		probabilities
	}

	def probabilities(probabilities: Vector): TweetAnalysis = {
		this.probabilities = probabilities
		this
	}

	def getCounts() : Map[String, Integer] ={
		counts
	}

	def setCounts(counts :Map[String, Integer]): TweetAnalysis = {
		this.counts = counts
		this
	}
}
