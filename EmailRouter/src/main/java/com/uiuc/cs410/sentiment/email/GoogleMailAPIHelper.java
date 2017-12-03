package com.uiuc.cs410.sentiment.email;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

public class GoogleMailAPIHelper {
	
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Gmail API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/gmail-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/gmail-java-quickstart
     */
    private static final List<String> SCOPES =
        Arrays.asList( GmailScopes.MAIL_GOOGLE_COM);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
    
    private Gmail service = null;
    private String user;
	
	public GoogleMailAPIHelper(String user, String secretPath){
		try {
			this.user = user;
			this.service = getGmailService(secretPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Email> fetchEmails() throws IOException{
		
		List<Email> emails = new ArrayList<>();
		
		ListMessagesResponse messageResponse = service.users().messages().list(this.user).execute();
        List<Message> messages = messageResponse.getMessages();
        if(messages == null || messages.isEmpty())
        {
        	System.out.println("Didn't get any messages!");
        	return emails;
        }
        for(Message m : messages){
        	String messageId = m.getId();
        	MimeMessage mimeMessage;
			try {
				mimeMessage = getMimeMessage(service, user, messageId);
				Email mail = new Email(mimeMessage);
				emails.add(mail);
//				return emails;
			} 
			catch (MessagingException e) {
				e.printStackTrace();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
        }
		return emails;
	}
	
	
    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(String secretPath) throws IOException {
        // Load client secrets.
        InputStream in =
            new FileInputStream("C:\\CS410\\project\\client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }
    
    /**
     * Build and return an authorized Gmail client service.
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public static Gmail getGmailService(String secretPath) throws IOException {
        Credential credential = authorize(secretPath);
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    private MimeMessage getMimeMessage(Gmail service, String userId, String messageId)
  	      throws IOException, MessagingException {
  	    Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute();

  	    Base64 base64Url = new Base64(true);
  	    byte[] emailBytes = base64Url.decodeBase64(message.getRaw());
  	    Properties props = new Properties();
  	    Session session = Session.getDefaultInstance(props, null);

  	    MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

  	    return email;
  	 }
    
    
    public MimeMessage createEmail(List<String> to, List<String> cc, List<String> bcc, String from,  String subject, String bodyText) throws MessagingException {
    		Properties props = new Properties();
    		Session session = Session.getDefaultInstance(props, null);

    		MimeMessage email = new MimeMessage(session);

    		email.setFrom(new InternetAddress(from));
    		for(String toAddr: to)
    			email.addRecipient(javax.mail.Message.RecipientType.TO,new InternetAddress(toAddr));
    		for(String ccAddr: cc)
    			email.addRecipient(javax.mail.Message.RecipientType.CC,new InternetAddress(ccAddr));
    		for(String bccAddr: bcc)
    			email.addRecipient(javax.mail.Message.RecipientType.BCC,new InternetAddress(bccAddr));
    		email.setSubject(subject);
    		email.setText(bodyText);
    		return email;
    }

    public Message sendMessage( MimeMessage emailContent) throws MessagingException, IOException {
    		Message message = createMessageWithEmail(emailContent);
    		message = this.service.users().messages().send(this.user, message).execute();

    		System.out.println("Message id: " + message.getId());
    		System.out.println(message.toPrettyString());
    		return message;
    }
   
    
    private Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
    
    public void markMessageAsRead(String messageId){
    	//TODO: Mark message as read
    }
}
