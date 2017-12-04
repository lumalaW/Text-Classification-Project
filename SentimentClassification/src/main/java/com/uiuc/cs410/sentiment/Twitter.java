package com.uiuc.cs410.sentiment;

import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Twitter {


	static twitter4j.Twitter getTwitterInstance() {		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("0BoInexDtUFsTz8wufgjnVfnZ")
		  .setOAuthConsumerSecret("DM5foai2ZOoVTxH1LUy21GyE32uD5sPKZFxcO4uYDt2fc5TI5L")
		  .setOAuthAccessToken("844728391836340224-UaOkOXsuX9d2iZfi3ZwiqROABrQeEoC")
		  .setOAuthAccessTokenSecret("nzJzWxVlVwu5BQis6FEs6gXuJVrfJhNswO60TYijriDiq");
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter4j.Twitter twitter = tf.getInstance();		
		return twitter;
		
	}
}
