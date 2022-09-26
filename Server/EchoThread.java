import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.Thread;

public class EchoThread extends Thread {
	protected Socket socket;
	MessageHandler HANDLER;
	DataOutputStream outToClient;
	boolean DEBUG = false;
	private boolean kill;
	private String request;

	public EchoThread(Socket clientSocket, MessageHandler h) {
		this.socket = clientSocket;
		this.HANDLER = h;
		this.kill = false;
		this.request = "";
	}

	public void run() {
		DataInputStream inFromClient;

		try {
			inFromClient = new DataInputStream(socket.getInputStream());
			outToClient = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			if (DEBUG) e.printStackTrace();
			return;
		}

		while (true) {
			try {
				String line = inFromClient.readUTF();

				if (line.startsWith("!")) {
                    System.out.println(line);
					String command = line.substring(1);
					if (command.startsWith("online")) {
						request = "online";
						return;
					}
				} else if ((line == null) || line.contains("left the chat.")) {
					System.out.println(line + "\n\r");
					socket.close();
					return;
				} else {
					System.out.println(line + "\n\r");
					HANDLER.addMessage(line);
				}
			} catch (IOException e) {
				if (DEBUG) e.printStackTrace();
				return;
			}
		}
	}

	public void sendBack(String message) {
		try {
			if (DEBUG) System.out.print("Sending back...");
			outToClient.writeUTF(message);
			if (DEBUG) System.out.println(" Done!");
		} catch (IOException e) {
			if (DEBUG) e.printStackTrace();
		}
	}

	public boolean killServer() {
		return this.kill;
	}

	public String getRequest() {
		return this.request;
	}

	public void clearRequest() {
		this.request = "";
	}
}