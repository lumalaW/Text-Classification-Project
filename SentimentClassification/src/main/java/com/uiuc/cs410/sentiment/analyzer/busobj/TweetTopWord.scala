package com.uiuc.cs410.sentiment.analyzer.busobj

class TweetTopWord(n: String, c: Int) {
  var text=n
  var weight=c
  
  def text(n: String): TweetTopWord = {
    this.text=n
    this
  }
  
  def gettext(): String = {
    this.text
  }
  
  def weight(c: Int): TweetTopWord = {
    this.weight=c
    this
  }
  
  def getweight(): Int = {
    this.weight
  }
}