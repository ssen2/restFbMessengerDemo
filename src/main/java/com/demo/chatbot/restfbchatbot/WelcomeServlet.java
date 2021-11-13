package com.demo.chatbot.restfbchatbot;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/greetings/*", loadOnStartup = 1)
public class WelcomeServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8850574637752408089L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		doGet(request, response);
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<h3>Hi there! Welcome to a simple demo chatbot app!</h3>");
	}

}
