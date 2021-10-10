package com.demo.chatbot.restfbchatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class RestfbchatbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestfbchatbotApplication.class, args);
	}

}
