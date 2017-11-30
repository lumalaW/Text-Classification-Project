package com.uiuc.cs410.sentiment;

import java.io.IOException;
import java.util.List;

import com.uiuc.cs410.sentiment.email.Email;
import com.uiuc.cs410.sentiment.email.GoogleMailAPIHelper;

public class EmailSentimentRouter {

	public static void main(String[] args){
		GoogleMailAPIHelper mailHelper = new GoogleMailAPIHelper("me");
		try {
			List<Email> allEmails = mailHelper.fetchEmails();
			for(Email e: allEmails){
				System.out.println("############# NEW EMAIL #############");
				System.out.println(e.toString());
			}
			System.out.println("Normal exit.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
