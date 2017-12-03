package com.uiuc.cs410.sentiment;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.uiuc.cs410.sentiment.email.Email;
import com.uiuc.cs410.sentiment.email.GoogleMailAPIHelper;

public class EmailSentimentRouter {

	private GoogleMailAPIHelper mailHelper = new GoogleMailAPIHelper("me");
	private long lastEmailCheckTime = 0;
	
	private long waitTime = 0;
	
	public static void main(String[] args){
		
		//Initialize
		EmailSentimentRouter router = new EmailSentimentRouter();
		router.init();

		List<Email> emails = router.checkForEmails();
		for(Email email: emails){
			router.processEmail(email);
		}
		router.waitForInterval(router.waitTime);
		
	}
	
	private void init(){
		//TODO: Figure out how we're configured
	}
	
	private void processEmail(Email email){
		//TODO: Extract text from Email
		String textBody = "";
		
		String documentClass = classifyDocument(textBody);
		String sentiment = classifySentiment(textBody);
		
		Map<String, String> addressLists = lookupTargetAddresses(documentClass, sentiment);
		forwardEmail(email, addressLists);
		//TODO: Mark the email as read on Google
		
	}
	
	private void forwardEmail(Email email, Map<String, String> addressList){
		
		
	}
	
	private void waitForInterval(long waitTime){
		//TODO: Figure out Sleeping mechanism
	}
	
	private List<Email> checkForEmails(){
		try {
			List<Email> allEmails = mailHelper.fetchEmails();
			for(Email e: allEmails){
				System.out.println("############# NEW EMAIL #############");
				System.out.println(e.toString());
			}
			//TODO: filter out emails that we've already processed.
			return allEmails;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String classifyDocument(String text){
		//TODO: Classify document of Email
		return null;
	}
	
	private String classifySentiment(String text){
		//TODO: Classify Sentiment of email
		return null;
	}
	
	private Map<String, String> lookupTargetAddresses(String documentClassification, String sentimentClassificaiton){
		//TODO: Look up target addresses
		return null;
	}
}
