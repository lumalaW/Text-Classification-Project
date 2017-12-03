package com.uiuc.cs410.sentiment.ws;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class DocumentClassificationWSHelper {
	
	private static final String CLASSIFY_ROUTE="classify";
	private static final String CLASSIFY_PARAM="text";

	private HttpHost i_targetHost = null;
	private CloseableHttpClient i_httpclient = null;
	private HttpClientContext i_localContext = null;
	private CredentialsProvider i_credsProvider = null;
	
	private String host = "";
	private String port = "";
	private String user = "";
	private String pass = "";
	private String address ="";

	public DocumentClassificationWSHelper(String host, String port){
		this.host=host;
		this.port=port;
		this.address = this.buildClassifyURL(host, port);
	}
	
	public String classify(String text){
		String path = this.address + CLASSIFY_ROUTE;
		path+="?"+CLASSIFY_PARAM+"=";
		path+=text;
		
		HttpGet get = new HttpGet(path);
		HttpResponse response = this.execute(get);
		
		String classification = this.extractClass(response);
		return classification;
	}
	
	private String extractClass(HttpResponse response){
		//TODO: Figure out how to get classification from Response
		return null;
	}
	
	private String buildClassifyURL(String host, String port){
		return "http://"+host+":"+port;
	}
	
	private HttpResponse execute(HttpRequestBase request)
	{
		try{

			this.i_targetHost = new HttpHost(this.host, Integer.parseInt(this.port), "http");
		    this.i_credsProvider = new BasicCredentialsProvider();

		    this.i_credsProvider.setCredentials(
		                new AuthScope(this.i_targetHost.getHostName(), this.i_targetHost.getPort()),
		                new UsernamePasswordCredentials(this.user, this.pass));
		    
		    this.i_httpclient = HttpClients.custom()
	                .setDefaultCredentialsProvider(this.i_credsProvider).build();

		    // Create AuthCache instance
		    AuthCache authCache = new BasicAuthCache();
		    BasicScheme basicAuth = new BasicScheme();
		    authCache.put(this.i_targetHost, basicAuth);

		    // Add AuthCache to the execution context
		    this.i_localContext = HttpClientContext.create();
		    this.i_localContext.setAuthCache(authCache);
        
		    HttpResponse response = this.i_httpclient.execute(this.i_targetHost, request, this.i_localContext);
		    return response;
	    }
	    catch(UnsupportedEncodingException e)
	    {
	    	System.out.println("UnsupportedEncodingException caught while trying to execute request "+request.toString());
			for(StackTraceElement s : e.getStackTrace())
			{
				System.out.println(s.toString());
			}
	    }
	    catch(ClientProtocolException cpe)
	    {
	    	System.out.println("ClientProtocolException caught while trying to execute request "+request.toString());
			for(StackTraceElement s : cpe.getStackTrace())
			{
				System.out.println(s.toString());
			}
	    }
	    catch(IOException ioe)
	    {
	    	System.out.println("IOException caught while trying to execute request "+request.toString());
			for(StackTraceElement s : ioe.getStackTrace())
			{
				System.out.println(s.toString());
			}
	    }
		catch(IllegalArgumentException iae)
		{
			System.out.println("IllegalArgumentException caught while trying to execute request "+request.toString());
			for(StackTraceElement s : iae.getStackTrace())
			{
				System.out.println(s.toString());
			}
		}
		return null;
	}
}
