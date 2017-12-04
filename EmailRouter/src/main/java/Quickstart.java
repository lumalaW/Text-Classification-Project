import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import com.google.api.services.gmail.Gmail;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Quickstart {
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

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
//        InputStream in =
//            new FileInputStream("C:\\CS410\\project\\client_secret.json");
        //InputStream in = new FileInputStream("./client_secret.json");
    	File f = new File("./client_secret.json");
    	System.out.println("File path = "+f.getAbsolutePath());
        InputStream in =
            new FileInputStream(f);
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
    public static Gmail getGmailService() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.
        Gmail service = getGmailService();

        // Print the labels in the user's account.
        String user = "me";
        ListLabelsResponse listResponse =
            service.users().labels().list(user).execute();
        List<Label> labels = listResponse.getLabels();
        if (labels.size() == 0) {
            System.out.println("No labels found.");
        } else {
            System.out.println("Labels:");
            for (Label label : labels) {
                System.out.printf("- %s\n", label.getName());
            }
        }
        
        ListMessagesResponse messageResponse = service.users().messages().list(user).execute();
        List<Message> messages = messageResponse.getMessages();
        if(messages == null || messages.isEmpty())
        {
        	System.out.println("Didn't get any messages!");
        	return;
        }
        for(Message m : messages){
        	System.out.println(m.toString());
        	String messageId = m.getId();
        	MimeMessage mimeMessage;
			try {
				mimeMessage = getMimeMessage(service, user, messageId);
				System.out.println(mimeMessage.getSubject());
				printMessage(mimeMessage);
				return;
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    public static MimeMessage getMimeMessage(Gmail service, String userId, String messageId)
    	      throws IOException, MessagingException {
    	    Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute();

    	    Base64 base64Url = new Base64(true);
    	    byte[] emailBytes = base64Url.decodeBase64(message.getRaw());
    	    Properties props = new Properties();
    	    Session session = Session.getDefaultInstance(props, null);

    	    MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

    	    return email;
    	  }
    
    public static void printMessage(MimeMessage mimeMessage){
    	MimeMessageParser parser = new MimeMessageParser(mimeMessage);
        try {
			String from = parser.getFrom();
			System.out.println("From: "+from);
			List<Address> to = parser.getTo();
			System.out.println("To: "+to);
	        List<Address> cc = parser.getCc();
	        System.out.println("Cc: "+cc);
	        List<Address> bcc = parser.getBcc();
	        System.out.println("Bcc: "+bcc);
	        String subject = parser.getSubject();
	        System.out.println("Subject: "+subject);
	        String htmlContent = parser.parse().getHtmlContent();
	        String text = getBodyText(htmlContent);
	        System.out.println("Text: "+text);
	        System.out.println("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static String getBodyText(String htmlContent){
    	Document doc = Jsoup.parse(htmlContent);
    	Elements links = doc.select("a");
    	Element body = doc.select("body").first();
    	Elements paragraphs = body.select("p");
    	String text = "";
    	for(Element p : paragraphs){
    		if(text.length()>0)
    			text+="\0";
    		text += p.text();

    	}
    	return text;
    }

}