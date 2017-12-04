package com.uiuc.cs410.sentiment.analyzer.busobj

class TextSentimentResult {
  var text: String = ""
  var score: Integer = -1
  var stanfordPrediction: Double = -1.0
  var naiveBayesPrediction: Double = -1.0
  
  def getText(): String = {
		text
	}

	def text(text: String): TextSentimentResult = {
		this.text = text
		this
	}
	
	def score(score :Integer):TextSentimentResult = {
		this.score = score
		this
	}
	
	def getScore(): Integer = {
	  score
	}
	
	def stanfordPrediction(score :Double):TextSentimentResult = {
		this.stanfordPrediction = score
		this
	}
	
	def getStanfordPrediction(): Double = {
	  stanfordPrediction
	}
	
	def naiveBayesPrediction(score :Double):TextSentimentResult = {
		this.naiveBayesPrediction = score
		this
	}
	
	def getNaiveBayesPrediction(): Double = {
	  naiveBayesPrediction
	}
}