package com.uiuc.cs410.sentiment;

import twitter4j.*;
//import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchTwitter {

	String query;
	

	public SearchTwitter(String query) {
		this.query = query;
	}

	public static void main(String[] args) throws TwitterException, InterruptedException {

		SearchTwitter searchTweets = new SearchTwitter("iphone review");
		List<String> tweetsList = searchTweets.buildTweetsList();
		
		int c=0;
		for (String tweet:tweetsList){
			System.out.println(++c +":" + tweet);
		}
	}

	public List<String> buildTweetsList() throws TwitterException, InterruptedException {
		List<String> tweetsList = new ArrayList<String>();
		final int MAX_QUERIES = 5;
		final int TWEETS_PER_QUERY = 100;
		int totalTweets = 0;
		long maxID = -1;
		twitter4j.Twitter twitter = Twitter.getTwitterInstance();
		Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");
		RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");

		for (int queryNumber = 0; queryNumber < MAX_QUERIES; queryNumber++) {
			if (searchTweetsRateLimit.getRemaining() == 0)
				Thread.sleep((searchTweetsRateLimit.getSecondsUntilReset() + 2) * 1000l);
			Query q = new Query(query);
			q.setCount(TWEETS_PER_QUERY);
			// q.resultType("recent"); // Get all tweets
			q.setLang("en");
			if (maxID != -1) {
				q.setMaxId(maxID - 1);
			}
			QueryResult r = twitter.search(q);
			if (r.getTweets().size() == 0) {
				break;
			}
			for (Status s : r.getTweets()) {
				totalTweets++;
				if (maxID == -1 || s.getId() < maxID) {
					maxID = s.getId();
				}
				tweetsList.add(s.getText());
			}
			searchTweetsRateLimit = r.getRateLimitStatus();
		}

		return tweetsList;
	}

}
