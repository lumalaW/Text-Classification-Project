package com.uiuc.cs410.sentiment;

import java.io.IOException;
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

	private long lastEmailCheckTime = 0;
	
	private long waitTime = 0;
	
	private GoogleMailAPIHelper mailHelper = null;
	private PropertyHandler propertyHandler = null;
	private DocumentClassificationWSHelper documentClassifier = null;
	private SentimentClassifcationWSHelper sentimentClassifier = null;
	
	public static void main(String[] args){
		
		//Initialize
		EmailSentimentRouter router = new EmailSentimentRouter();
		try {
			router.init();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		List<Email> emails = router.checkForEmails();
		for(Email email: emails){
			router.processEmail(email);
		}
		router.waitForInterval(router.waitTime);
		
	}
	
	private void init() throws IOException{
		
		this.propertyHandler = new PropertyHandler();
		
		String mailUser = this.propertyHandler.getProperty(PropertyHandler.GOOGLE_MAIL_USER);
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
		
		String documentClass = classifyDocument(textBody);
		String sentiment = classifySentiment(textBody);
		
		Map<String, List<String>> addressLists = lookupTargetAddresses(documentClass, sentiment);
		try {
			if(forwardEmail(email, addressLists)){
				//TODO: Mark the email as read on Google
				mailHelper.markMessageAsRead(email.getId());
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
			MimeMessage message = mailHelper.createEmail(toList, ccList, bccList, email.getFrom(), email.getSubject(), email.getText());
			mailHelper.sendMessage(message);
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	private void waitForInterval(long waitTime){
		//TODO: Figure out Sleeping mechanism
	}
	
	private List<Email> checkForEmails(){
		try {
			List<Email> allEmails = mailHelper.fetchUnreadEmails();
//			for(Email e: allEmails){
//				System.out.println("############# NEW EMAIL #############");
//				System.out.println(e.toString());
//			}
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
		//TODO: Look up target addresses
		return null;
	}
	
	private static void logMessage(String message){
		
		System.out.println(message);
	}
}
