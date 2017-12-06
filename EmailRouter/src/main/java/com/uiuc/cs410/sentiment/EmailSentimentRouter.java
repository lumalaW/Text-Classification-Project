package com.uiuc.cs410.sentiment;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.uiuc.cs410.sentiment.email.Email;
import com.uiuc.cs410.sentiment.email.GoogleMailAPIHelper;
import com.uiuc.cs410.sentiment.props.PropertyHandler;
import com.uiuc.cs410.sentiment.ws.DocumentClassificationWSHelper;
import com.uiuc.cs410.sentiment.ws.SentimentClassifcationWSHelper;

public class EmailSentimentRouter {

	//private long waitTime = 10000;
	
	private GoogleMailAPIHelper mailHelper = null;
	private PropertyHandler propertyHandler = null;
	private DocumentClassificationWSHelper documentClassifier = null;
	private SentimentClassifcationWSHelper sentimentClassifier = null;
	
	public static void main(String[] args){
		
		//Initialize
		EmailSentimentRouter router = new EmailSentimentRouter();
		try {
			logMessage(" Initializing...");
			router.init();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		while(true){
			logMessage("Checking for Unread Emails...");
			List<Email> emails = router.checkForEmails();
			logMessage("Received "+emails.size()+" emails.");
			for(Email email: emails){
				if(email.getText().equalsIgnoreCase("ERROR")){
					logMessage("ERROR: Failed to read message with Subject "+email.getSubject()+". Skipping.");
					continue;
				}
				logMessage("Processing email "+email.getId());
				router.processEmail(email);
			}
			router.waitForInterval(router.getWaitTime()*1000);
		}
	}
	
	private void init() throws IOException{
		
		this.propertyHandler = new PropertyHandler();
		
		String mailUser = "me";
		String secretPath = this.propertyHandler.getProperty(PropertyHandler.GOOGLE_MAIL_SECRET_FILE);
		this.mailHelper = new GoogleMailAPIHelper(mailUser, secretPath);
		
		String docClassHost = this.propertyHandler.getProperty(PropertyHandler.SERVICE_DOC_CLASS_HOST);
		String docClassPort = this.propertyHandler.getProperty(PropertyHandler.SERVICE_DOC_CLASS_PORT);
		this.documentClassifier = new DocumentClassificationWSHelper(docClassHost, docClassPort);
		
		String sentClassHost = this.propertyHandler.getProperty(PropertyHandler.SERVICE_SENT_CLASS_HOST);
		String sentClassPort = this.propertyHandler.getProperty(PropertyHandler.SERVICE_SENT_CLASS_PORT);
		this.sentimentClassifier = new SentimentClassifcationWSHelper(sentClassHost, sentClassPort);
	}
	
	private boolean processEmail(Email email){
		String textBody = email.getText();
		logMessage("Text to classify: "+textBody);
		logMessage(" ");
		
		logMessage("Classifying document type...");
		String documentClass = classifyDocument(textBody);
		logMessage("Document classification: "+documentClass);
		logMessage("Classifying sentiment...");
		String sentiment = classifySentiment(textBody);
		logMessage("Sentiment classification: "+sentiment);
		
		Map<String, List<String>> addressLists = lookupTargetAddresses(documentClass, sentiment);
		try {
			logMessage("Forwarding to interested parties...");
			if(forwardEmail(email, addressLists)){
				logMessage("Marking email as read...");
				mailHelper.markMessageAsRead(email.getId());
				logMessage("Complete.");
				return true;
			}
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	private boolean forwardEmail(Email email, Map<String, List<String>> addressList) throws IOException{
		List<String> toList = addressList.get(PropertyHandler.LIST_TO);
		List<String> ccList = addressList.get(PropertyHandler.LIST_CC);
		List<String> bccList = addressList.get(PropertyHandler.LIST_BCC);
		
		try {
			MimeMessage message = mailHelper.createEmail(toList, ccList, bccList, email.getFrom(), email.getSubject(), email.getOriginalHTMLContent());
			mailHelper.sendMessage(message);
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	private void waitForInterval(long waitTime){
		logMessage("");
		logMessage("Sleeping for "+(waitTime/(1000.0)+" second"));
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logMessage(" ");
	}
	
	private List<Email> checkForEmails(){
		try {
			List<Email> allEmails = mailHelper.fetchUnreadEmails();
			return allEmails;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String classifyDocument(String text){
		String classification = documentClassifier.classify(text);
		return classification;
	}
	
	private String classifySentiment(String text){
		String classification = sentimentClassifier.classify(text);
		return classification;
	}
	
	private Map<String, List<String>> lookupTargetAddresses(String documentClassification, String sentimentClassificaiton){
		
		List<String> toList = propertyHandler.getMailingList(documentClassification, sentimentClassificaiton, PropertyHandler.LIST_TO);
		List<String> ccList = propertyHandler.getMailingList(documentClassification, sentimentClassificaiton, PropertyHandler.LIST_CC);
		List<String> bccList = propertyHandler.getMailingList(documentClassification, sentimentClassificaiton, PropertyHandler.LIST_BCC);
		
		
		Map<String, List<String>> map = new HashMap<>();
		map.put(PropertyHandler.LIST_TO, toList);
		map.put(PropertyHandler.LIST_CC, ccList);
		map.put(PropertyHandler.LIST_BCC, bccList);
		
		return map;
	}
	
	private static void logMessage(String message){
		System.out.println(message);
	}
	
	private long getWaitTime(){
		long waitTimeSeconds = 10;
		String waitTimeProperty = propertyHandler.getProperty(PropertyHandler.WAIT_TIME);
		if(waitTimeProperty!=null)
			waitTimeSeconds = Long.parseLong(waitTimeProperty);
		return waitTimeSeconds;
	}
}
