package com.coursecloud.chatbubble;

public class ChatMessage {
	public boolean left;
	public String message;
	public String user;
	public String time;
	public String porID;

	public ChatMessage(boolean left, String message,String user, String time, String porID) {
		super();
		this.left = left;
		this.message = message;
		this.user = user;
		this.time = time;
		this.porID = porID;
	}
}
