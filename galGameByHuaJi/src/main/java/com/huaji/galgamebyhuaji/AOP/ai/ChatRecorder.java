package com.huaji.galgamebyhuaji.AOP.ai;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

public class ChatRecorder implements ChatMemory {
	@Override
	public void add (String conversationId, List<Message> messages) {
	
	}
	
	@Override
	public List<Message> get (String conversationId) {
		return List.of();
	}
	
	@Override
	public void clear (String conversationId) {
	
	}
}