package com.teambind.springproject.event.events;

public abstract class Event {
	String topic;
	
	public Event() {
	}
	
	public Event(String topic) {
		this.topic = topic;
	}
	
	public String getTopic() {
		return topic;
	}
}
