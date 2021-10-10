package com.demo.chatbot.restfbchatbot;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.FacebookClient;
import com.restfb.JsonMapper;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonObject;
import com.restfb.types.send.CallToAction;
import com.restfb.types.send.Greeting;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.Message;
import com.restfb.types.send.SendResponse;
import com.restfb.types.webhook.WebhookEntry;
import com.restfb.types.webhook.WebhookObject;
import com.restfb.types.webhook.messaging.MessagingItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
/**
 * Servlet implementation class FbChat
 */
@WebServlet("/Webhook")
public class FbChat extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	//FB Access Token
	private String pageAccessToken = "EAAFwL7TqfoIBAAuZAZAZC8ZCnxqtTGMAxOLq1alJaiwM8tV0wSr7u5zooCCiiSsp7jcCekcuJydpzkahCDemguvglAydk9R7JPbIHLAjFhPEVRj43BN3hms6hNJIlJDXabOqRrJJx4uTentKo1hdlVgiCd92xIFDI10UQilknHc6tFzx65Vi";
	
	//URL verification
	private String verifyToken = "AnyString";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FbChat() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String hubToken = request.getParameter("hub.verify_token");
		String hubChallenge = request.getParameter("hub.challenge");
		
		if(verifyToken.equalsIgnoreCase(hubToken)){
			response.getWriter().write(hubChallenge);
			response.getWriter().flush();
			response.getWriter().close();
		}else{
			response.getWriter().write("incorrect verify token");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		BufferedReader br = request.getReader();
		String line = "";
		while((line = br.readLine()) != null){
			sb.append(line);
			
		}
		//convert java String to java Object
		JsonMapper mapper = new DefaultJsonMapper();
		WebhookObject webhookObject = mapper.toJavaObject(sb.toString(), WebhookObject.class);
		
		for(WebhookEntry entry : webhookObject.getEntryList()){
			if(entry.getMessaging() != null){
				for(MessagingItem mItem : entry.getMessaging()){
					
					String senderId = mItem.getSender().getId();
					IdMessageRecipient recipient = new IdMessageRecipient(senderId);
					
					if(mItem.getMessage() != null && mItem.getMessage().getText() != null){
						sendMessage(recipient, new Message("Hi"));
					}
				}
				
			}
		}
	}
	
	void sendMessage(IdMessageRecipient recipient, Message message){
		// create a version 2.6 client
		FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.VERSION_9_0);

		SendResponse resp = pageClient.publish("me/messages", SendResponse.class,
				Parameter.with("recipient", recipient), // the id or phone recipient
				Parameter.with("message", message)); // one of the messages from above
		
	}
	
	public void setGetStartedButton(String payload){
		
		System.out.println("Entered : setGetStartedButton()");
		
		CallToAction getStartedPayload = new CallToAction(payload);

		// we assume there's already a FacebookClient
		FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.VERSION_9_0);
		JsonObject response = pageClient.publish("me/messenger_profile", 
		     JsonObject.class, // the returned result as JsonObject
			 Parameter.with("get_started", getStartedPayload));

	}
	
	public void setGreetingsText(){
		
		System.out.println("Entered : setGreetingsText()");
		
		Greeting defaultLocaleGreetingText = new Greeting("Hello {{user_first_name}}");
		Greeting usLocaleGreetingText= new Greeting(Locale.US, "Timeless apparel for the masses.");

		// we assume there's already a FacebookClient
		FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.VERSION_9_0);
		
		JsonObject response = pageClient.publish("me/messenger_profile", 
		     JsonObject.class, // the returned result as JsonObject
			 Parameter.with("greeting", Arrays.asList(defaultLocaleGreetingText, usLocaleGreetingText)));
	}
}
	