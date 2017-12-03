package com.uiuc.cs410.sentiment.props;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyHandler {

	private static final String GENERAL_PROPS_FILE = "conf/general.properties";
	
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
	
	public String getProperty(String propertyName)
	{
		return null;
	}
	
	public String getMailingList(String labelClass, String sentiment, String listLevel){
		return null;
	}
	private Properties loadPropsFile(String name, String path){
		return null;
	}
}
