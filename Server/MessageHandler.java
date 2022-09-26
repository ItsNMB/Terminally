import java.net.*;
import java.util.*;

class MessageHandler {
	private ArrayList<String> messages;
	private int COUNTER, currentMessageCount;

	public MessageHandler() {
		messages = new ArrayList<>();
		COUNTER = 0;
	}

	public void increase() {
		COUNTER++;
	}

	public void addMessage(String message) {
		messages.add(message);
	}

	public String getMessage() {
		String result = messages.get(0);
		currentMessageCount++;
		if (currentMessageCount == COUNTER) {
			messages.remove(0);
			currentMessageCount = 0;
		}
		return result;
	}

	public int size() {
		return this.messages.size();
	}

	public String info() {
		return "Messages: "+messages.size()+
			"\nCOUNTER: "+COUNTER+
			"\ncurrentCounter: "+currentMessageCount;
	}
}