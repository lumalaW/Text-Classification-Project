package com.uiuc.cs410.sentiment;

import spark.Request;
import spark.Response;
import spark.Route;
 
import static spark.Spark.*;

import org.apache.spark.SparkContext;

import com.google.gson.Gson;
import com.uiuc.cs410.sentiment.analyzer.busobj.TextAnalysis;
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
        
        get("/classify", (request, response) -> {
        	
        	String text = request.queryParams("text");
        	String method = request.queryParams("method");
        	if(method==null)
        		method = STANFORDCORENLP;
        	if(text==null || text.length()==0)
        		return "Empty text!";
        	
        	Gson gson = new Gson();
        	
        	SparkContext sparkContext =SparkContextGenerator.getContextInstance();
            
        	Predictor predictor = new Predictor(sparkContext, method );
            
            TextAnalysis analysis = predictor.classifyText(text);
            return gson.toJson(analysis, TextAnalysis.class);
        });
    }
}
