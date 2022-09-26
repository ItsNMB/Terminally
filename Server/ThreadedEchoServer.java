import java.net.*;
import java.io.*;
import java.util.*;

public class ThreadedEchoServer {
	static final int PORT = 1234;
	static MessageHandler HANDLER;
	static boolean DEBUG = false;
	static boolean killServer = false;;

	public static void main(String args[]) {
		ServerSocket serverSocket = null;
		Socket socket = null;
		HANDLER = new MessageHandler();

		try {
			serverSocket = new ServerSocket(PORT);
			InetAddress addr = InetAddress.getLocalHost();
			String hostname = addr.getHostName();
			System.out.println("Local Host: [" + hostname + "] @ " + addr.getHostAddress());
		} catch (IOException e) {
			if (DEBUG) e.printStackTrace();
			return;
		}

		UpdateThread updater = new UpdateThread();
		updater.start();

		while (!killServer) {
			if (updater.killServer()) {
				killServer = true;
				return;
			}

			try {
				socket = serverSocket.accept();
				System.out.println("Just connected to " + socket.getRemoteSocketAddress());
			} catch (IOException e) {
				if (DEBUG) e.printStackTrace();
				return;
			}

			updater.add(new EchoThread(socket, HANDLER));
			HANDLER.increase();
		}
	}

	static class UpdateThread extends Thread {
		private ArrayList<EchoThread> threads;
		private boolean killServer;

		UpdateThread() {
			threads = new ArrayList<>();
		}

		public void run() {
			while (true) {
				for (EchoThread t : threads) {
					if (t.killServer()) this.killServer = true;
					if (t.getRequest().equals("online")) {
						String output =
						    "# Es " + (threads.size() == 1 ? "ist [" : "sind [")
						    + threads.size() + "] online";
						t.sendBack(output);
						t.clearRequest();

                        System.out.println(output);
					}
				}

				if (HANDLER.size() > 0) {
					if (DEBUG) System.out.println("Handler has a Message! \n" + HANDLER.info());
					for (EchoThread t : threads) {
						if (DEBUG) System.out.println("Seding to " + t.getName());
						t.sendBack(HANDLER.getMessage());
					}
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					if (DEBUG) e.printStackTrace();
				}
			}
		}

		public void add(EchoThread echoThread) {
			threads.add(echoThread);
			threads.get(threads.size() - 1).start();
		}

		public boolean killServer() {
			return this.killServer;
		}
	}
}