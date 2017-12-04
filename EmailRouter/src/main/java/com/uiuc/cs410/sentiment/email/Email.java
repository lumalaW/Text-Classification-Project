package com.uiuc.cs410.sentiment.email;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Email {

	private String from = "";
	private List<Address> to = new ArrayList<Address>();
	private List<Address> cc = new ArrayList<Address>();
	private List<Address> bcc = new ArrayList<Address>();
	private String subject = "";
	private String text = "";
	private String id = "";
	private String originalHTMLContent = "";
	
	public Email(MimeMessage message){
		this.parse(message);
	}
	
	public Email(){
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("From: ");
		builder.append(this.from);
		builder.append("\0To: ");
		for(Address addr : to){
			builder.append(addr.toString());
			builder.append(";");
		}
		builder.append("\0Cc: ");
		for(Address addr : this.cc){
			builder.append(addr.toString());
			builder.append(";");
		}
		builder.append("\0Bcc: ");
		for(Address addr : this.bcc){
			builder.append(addr.toString());
			builder.append(";");
		}
		builder.append("\0Subject: ");
		builder.append(this.subject);
		builder.append("\0Text: ");
		builder.append(this.text);

		return builder.toString();
	}
	
	private boolean parse(MimeMessage m){
		MimeMessageParser parser = new MimeMessageParser(m);
		try {
			this.from = parser.getFrom();
			this.to = parser.getTo();
		    this.cc = parser.getCc();
		    this.bcc = parser.getBcc();
		    this.subject = parser.getSubject();
		    this.originalHTMLContent = parser.parse().getHtmlContent();
		    this.text = getBodyText(this.originalHTMLContent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
    private String getBodyText(String htmlContent){
    	Document doc = Jsoup.parse(htmlContent);
    	Element body = doc.select("body").first();
    	Elements paragraphs = body.select("p");
    	String text = "";
    	
    	for(Element p : paragraphs){
    		if(text.length()>0)
    			text+="\0";
    		text += p.text();
    	}
    	//add any td text
    	Elements tds = body.select("td");
    	for(Element p : tds){
    		if(text.length()>0)
    			text+="\0";
    		text += p.text();
    	}
    	return text;
    }

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<Address> getTo() {
		return to;
	}

	public void setTo(List<Address> to) {
		this.to = to;
	}

	public List<Address> getCc() {
		return cc;
	}

	public void setCc(List<Address> cc) {
		this.cc = cc;
	}

	public List<Address> getBcc() {
		return bcc;
	}

	public void setBcc(List<Address> bcc) {
		this.bcc = bcc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOriginalHTMLContent() {
		return originalHTMLContent;
	}

	public void setOriginalHTMLContent(String originalHTMLContent) {
		this.originalHTMLContent = originalHTMLContent;
	}
}
