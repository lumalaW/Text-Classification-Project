package com.uiuc.cs410.sentiment.props;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertyHandler {

	private static final String GENERAL_PROPS_FILE = "./conf/general.properties";
	private static final String GENERAL_PROPS_KEY = "GENERAL";
	private static final String DIR_EMAIL_LISTS = "./conf/emailLists";
	
	public static final String SENTIMENT_VERY_NEG = "angry";
	public static final String SENTIMENT_NEG = "negative";
	public static final String SENTIMENT_NEUT = "neutral";
	public static final String SENTIMENT_POS = "positive";
	public static final String SENTIMENT_VERY_POS = "elated";
	
	public static final String LIST_TO="tolist";
	public static final String LIST_CC="cclist";
	public static final String LIST_BCC="bcclist";
	
	public static final String GOOGLE_MAIL_USER = "inbox.user";
	public static final String GOOGLE_MAIL_SECRET_FILE = "inbox.secret.file.path";
	
	public static final String SERVICE_SENT_CLASS_HOST = "service.sentiment.host";
	public static final String SERVICE_SENT_CLASS_PORT = "service.sentiment.port";
	public static final String SERVICE_DOC_CLASS_HOST = "service.document.host";
	public static final String SERVICE_DOC_CLASS_PORT = "service.document.port";
			
	private Map<String, Properties> propertyFiles = new HashMap<>();
	
	public PropertyHandler() throws IOException
	{
		Properties general = new Properties();
		try {
			File here = new File(".");
			System.out.println("Current directory: "+here.getAbsolutePath());
			general.load(new FileReader(GENERAL_PROPS_FILE));
			propertyFiles.put(GENERAL_PROPS_KEY, general);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
	public String getProperty(String propertyName)
	{
		Properties general = propertyFiles.get(GENERAL_PROPS_KEY);
		return general.getProperty(propertyName);
	}
	
	public List<String> getMailingList(String labelClass, String sentiment, String listLevel){
		Properties classProps = propertyFiles.get(labelClass);
		if(classProps==null){
			classProps = loadPropsFile(labelClass, DIR_EMAIL_LISTS);
			propertyFiles.put(labelClass, classProps);
		}
		List<String> emailList = new ArrayList<>();
		
		return emailList;
	}
	
	private Properties loadPropsFile(String name, String path){
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(path+"/"+name));
			return p;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	private List<String> splitEmails(String emailString){
		List<String> emails = new ArrayList<>();
		if(emailString!=null && emailString.trim().length()>0){
			String trimmed = emailString.trim();
			String[] tokens = trimmed.split(";");
			for(String token : tokens){
				System.out.println(token.trim());
				if(token.trim().length()>0)
					emails.add(token.trim());
			}
		}
		return emails;
	}
}
