package com.demo.chatbot.restfbchatbot;

import org.springframework.stereotype.Controller;

@Controller
public class ChatTest extends FbChat{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6775440041261733563L;
	
	public void init() {
        setGetStartedButton("hi");
       /* setGreetingText(new Payload[]{new Payload().setLocale("default").setText("JBot is a Java Framework to help" +
                " developers make Facebook, and Slack bots easily. You can see a quick demo by clicking " +
                "the \"Get Started\" button or just typing \"Hi\".")}); */
        setGreetingsText();
    }
	
   /* @Controller(events = {EventType.MESSAGE, EventType.POSTBACK}, pattern = "^(?i)(hi|hello|hey)$")
    public void onGetStarted(Event event) {
        // quick reply buttons
        Button[] quickReplies = new Button[]{
                new Button().setContentType("text").setTitle("Sure").setPayload("yes"),
                new Button().setContentType("text").setTitle("Nope").setPayload("no")
        };
        reply(event, new Message().setText("Hello, I am JBot. Would you like to see more?").setQuickReplies(quickReplies));
    } */


}
