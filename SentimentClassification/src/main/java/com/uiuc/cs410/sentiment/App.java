package com.uiuc.cs410.sentiment;

import spark.Request;
import spark.Response;
import spark.Route;
 
import static spark.Spark.*;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkContext;

import com.google.gson.Gson;
import com.uiuc.cs410.sentiment.analyzer.busobj.TweetAnalysis;
import com.uiuc.cs410.sentiment.analyzer.busobj.TextAnalysis;
import com.uiuc.cs410.sentiment.analyzer.busobj.TweetScore;
import com.uiuc.cs410.sentiment.analyzer.SparkContextGenerator;
import com.uiuc.cs410.sentiment.analyzer.model.Predictor;

public class App 
{
	
	public static final String MOVIE_REVIEW_DATA= "resources/data/movie_reviews/train.tsv";
	public static final String TAB_DELIMITER = "\t";
	public static final String STANFORDCORENLP="stanford";
	
	
    public static void main(String[] args) {
      	staticFiles.location("/public");

        get("/test", (request, response) -> "Hello World");
        
        get("/score", (request, response) -> {
        	String tweetSubject = request.queryParams("tweetSubject");
        	String method = request.queryParams("method");
        	if(method==null)
        		method = STANFORDCORENLP;
        	
        	Gson gson = new Gson();
        	
        	SparkContext sparkContext =SparkContextGenerator.getContextInstance();
        	Predictor predictor = new Predictor(sparkContext, method);
        	predictor.trainModel(MOVIE_REVIEW_DATA, TAB_DELIMITER);
        	
        	SearchTwitter twitter = new SearchTwitter(tweetSubject);
        	List<String> tweets = twitter.buildTweetsList();
        	
        	if(tweets==null || tweets.size()==0)
        		return "No tweets found!";
        	
        	TweetScore score = predictor.scoreTweets(tweets);
        	score.setModel(method);
            score.calcWordCounts(tweetSubject.toLowerCase());
            score.calcScore();
        	return gson.toJson(score, TweetScore.class);
        });
        get("/classify", (request, response) -> {
        	
        	String text = request.queryParams("text");
        	String method = request.queryParams("method");
        	if(method==null)
        		method = STANFORDCORENLP;
        	if(text==null)
        		return "Empty text!";
        	
        	Gson gson = new Gson();
        	
        	SparkContext sparkContext =SparkContextGenerator.getContextInstance();
            
        	Predictor predictor = new Predictor(sparkContext, method );
        	//predictor.trainModel(MOVIE_REVIEW_DATA, TAB_DELIMITER);
            
            TextAnalysis analysis = predictor.classifyText(text);
            return gson.toJson(analysis, TextAnalysis.class);
        });
    }
}
