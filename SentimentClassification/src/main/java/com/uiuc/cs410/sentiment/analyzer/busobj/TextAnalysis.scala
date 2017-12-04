package com.uiuc.cs410.sentiment.analyzer.busobj

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.mllib.linalg.Vector;

class TextAnalysis {
  var text: String = ""
	var score : Double = -1.0
	var probabilities : Vector = null
	var counts : Map[String, Double] = new HashMap();
	

	def getText(): String = {
		text
	}

	def text(text: String): TextAnalysis = {
		this.text = text
		this
	}

	def score(score :Double):TextAnalysis = {
		this.score = score
		this
	}

	def getProbabilities(): Vector = {
		probabilities
	}

	def probabilities(probabilities: Vector): TextAnalysis = {
		this.probabilities = probabilities
		this
	}

	def getCounts() : Map[String, Double] ={
		counts
	}

	def setCounts(counts :Map[String, Double]): TextAnalysis = {
		this.counts = counts
		this
	}
}