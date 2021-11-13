package com.demo.chatbot.restfbchatbot;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

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
import com.restfb.types.send.Bubble;
import com.restfb.types.send.CallToAction;
import com.restfb.types.send.GenericTemplatePayload;
import com.restfb.types.send.Greeting;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.Message;
import com.restfb.types.send.PostbackButton;
import com.restfb.types.send.SendResponse;
import com.restfb.types.send.TemplateAttachment;
import com.restfb.types.send.WebButton;
import com.restfb.types.webhook.WebhookEntry;
import com.restfb.types.webhook.WebhookObject;
import com.restfb.types.webhook.messaging.MessageItem;
import com.restfb.types.webhook.messaging.MessagingItem;
import com.restfb.types.webhook.messaging.PostbackItem;
/**
 * Servlet implementation class FbChat
 */
@WebServlet("/webhook")
public class FbChat extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FbChat() {
        super();
        // TODO Auto-generated constructor stub
    }
  
    private String fbVerifyToken = System.getenv("BOT_VERIFY_TOKEN");
    private String pageAccessToken = System.getenv("PAGE_ACCESS_TOKEN");
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("doGet called");
		String mode = request.getParameter("hub.mode");
		String hubToken = request.getParameter("hub.verify_token");
		String hubChallenge = request.getParameter("hub.challenge");
		
		System.out.println("hubToken :"+hubToken);
		System.out.println("mode :"+mode);
		System.out.println("hubChallenge :"+hubChallenge);
		
		System.out.println("verify token check"+fbVerifyToken);
		
		if("subscribe".equals(mode) && fbVerifyToken.equalsIgnoreCase(hubToken)){
			System.out.println("hub token matched");
			response.getWriter().write(hubChallenge);
			response.getWriter().flush();
			response.getWriter().close();
			System.out.println("Challenge send back to Facebook: {}" +request.getParameter("hub.challenge"));
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
			System.out.println("webhook object list size :"+webhookObject.getEntryList().size());
			if(entry.getMessaging() != null){
				System.out.println("webhook entry detail :"+entry);
				for(MessagingItem mItem : entry.getMessaging()){
					
					String senderId = mItem.getSender().getId();
					System.out.println("senderId in doPost:"+senderId);
					System.out.println("mItem.getMessage() :"+mItem.getMessage());
					if(mItem.getMessage() != null)
						System.out.println("mItem.getMessage().getText() :"+mItem.getMessage().getText());
					System.out.println("mItem.getRecipient() :"+mItem.getRecipient());
					IdMessageRecipient recipient = new IdMessageRecipient(senderId);
					
					// Check if the event is a message or postback and
					  // pass the event to the appropriate handler function
					  if (mItem.isMessage()) {
						  handleMessage(recipient, mItem.getMessage());        
					  } else if (mItem.isPostback()) {
						  handlePostback(recipient, mItem.getPostback());
					  }
					
					/*if(mItem.getMessage() != null && mItem.getMessage().getText() != null){
						System.out.println("message Payload :"+mItem.getMessage().getText());
						System.out.println("verify token check :"+fbVerifyToken);
						sendMessage(recipient, new Message("Hi there! This is Bot. How can I help you?"));
					}*/
				}
				
			}
		}
	}
	
	public void handleMessage(IdMessageRecipient recipient, MessageItem message){
		System.out.println("Entered: handleMessage()");
		
		Message response = null;
		
		if(message.getText() != null){
			//Message simpleTextMessage = new Message("Just a simple text");
			response = new Message("You sent the message: "+message.getText() +" Now send me an attachment");
			//sendMessage(recipient, new Message("Hi there! This is Bot. How can I help you?"));
		}else if(message.getAttachments() != null){
			// Gets the URL of the message attachment
		    String attachment_url = message.getAttachments().get(0).getPayload().getUrl();
		    
		    //Below code is for simple media attachment
		    /*MediaAttachment image = new MediaAttachment(MediaAttachment.Type.IMAGE, attachment_url);
		    Message imageMessage = new Message(image); */
		    
		    GenericTemplatePayload payload = new GenericTemplatePayload();
		    //Create a bubble with a web button
			Bubble firstBubble = new Bubble("Is this the right picture?");
			firstBubble.setSubtitle("Tap a button to answer");
			WebButton webButton = new WebButton("EXAMPLE TITLE", attachment_url);
			firstBubble.addButton(webButton);
		    
			// Create a bubble with two postback buttons
			Bubble secondBubble = new Bubble("Title of second bubble");
			PostbackButton postbackButtonYes = new PostbackButton("Yes!", "yes");
			secondBubble.addButton(postbackButtonYes);
			
			PostbackButton postbackButtonNo = new PostbackButton("No!", "no");
			secondBubble.addButton(postbackButtonNo);

			payload.addBubble(firstBubble);
			payload.addBubble(secondBubble);

			TemplateAttachment templateAttachment = new TemplateAttachment(payload);
			//Message imageMessage = new Message(templateAttachment); 
			response = new Message(templateAttachment); 
		}
		
		callSendAPI(recipient, response);
	}
	
	public void handlePostback(IdMessageRecipient recipient, PostbackItem postbackItem){
		  System.out.println("Entered: handlePostback()");
		  //Get the payload for the postback
		  String payload = postbackItem.getPayload();
		  
		  Message response = null;
		  // Set the response based on the postback payload
		  if (payload == "yes") {
		    response = new Message("Thanks!");
		  } else if (payload == "no") {
		    response = new Message("Oops, try sending another image.");
		  }
		  // Send the message to acknowledge the postback
		  callSendAPI(recipient, response);
		
	}
	
	public void callSendAPI(IdMessageRecipient recipient, Message message){
		System.out.println("Entered: callSendAPI()");
		// create the latest version client
		FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.LATEST);

		SendResponse resp = pageClient.publish("me/messages", SendResponse.class,
				Parameter.with("recipient", recipient), // the id or phone recipient
				Parameter.with("message", message)); // one of the messages from above
		
	}
	
	public void sendMessage(IdMessageRecipient recipient, Message message){
		// create the latest version client
		FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.LATEST);

		SendResponse resp = pageClient.publish("me/messages", SendResponse.class,
				Parameter.with("recipient", recipient), // the id or phone recipient
				Parameter.with("message", message)); // one of the messages from above
		
	}
	
	public void setGetStartedButton(String payload){
		
		System.out.println("Entered : setGetStartedButton()");
		CallToAction getStartedPayload = new CallToAction(payload);

		// we assume there's already a FacebookClient
		FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.LATEST);
		JsonObject response = pageClient.publish("me/messenger_profile", 
		     JsonObject.class, // the returned result as JsonObject
			 Parameter.with("get_started", getStartedPayload));

	}
	
	public void setGreetingsText(){
		
		System.out.println("Entered : setGreetingsText()");
		
		Greeting defaultLocaleGreetingText = new Greeting("Hello {{user_first_name}}");
		Greeting usLocaleGreetingText= new Greeting(Locale.US, "Timeless apparel for the masses.");

		// we assume there's already a FacebookClient
		FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.LATEST);
		
		JsonObject response = pageClient.publish("me/messenger_profile", 
		     JsonObject.class, // the returned result as JsonObject
			 Parameter.with("greeting", Arrays.asList(defaultLocaleGreetingText, usLocaleGreetingText)));
	}
}
	