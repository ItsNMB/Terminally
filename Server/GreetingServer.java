import java.net.*;
import java.io.*;
import java.util.*;

public class GreetingServer extends Thread {
	private ServerSocket serverSocket;

	public GreetingServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(0);
	}

	public void run() {
		while (true) {
			try {
				// Accepting Connections
				Socket server = serverSocket.accept();

				// Send Back
				DataInputStream in = new DataInputStream(server.getInputStream());
				DataOutputStream out = new DataOutputStream(server.getOutputStream());
				out.writeUTF(in.readUTF());

				server.close();
			} catch (SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public static void main(String [] args) {
		// int port = Integer.parseInt(args[0]);
		// try {
		// 	Thread t = new GreetingServer(port);
		// 	t.start();
		// } catch (IOException e) {
		// 	e.printStackTrace();
		// }

		System.out.println("\u001b[1m Test\u001b[0m");
		System.out.println("\u001b[2m Test\u001b[0m");
		System.out.println("\u001b[3m Test\u001b[0m");
		System.out.println("\u001b[4m Test\u001b[0m");
		System.out.println("\u001b[5m Test\u001b[0m");

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int code = 16*i+j;
				System.out.print("\u001b[" + code + "m "+ code +"\u001b[0m");
			}
			System.out.println();
		}
		System.out.println();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int code = i * 16 * j;
				System.out.print("\u001b[38;5;" + code + "m "+ (16*i+j));
			}
			System.out.println();
		}
		System.out.print("\u001b[0m");
	}
}