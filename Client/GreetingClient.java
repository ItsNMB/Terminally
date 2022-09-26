import java.net.*;
import java.io.*;
import java.util.*;

public class GreetingClient {
	static String name;
	static String prefix;
	static String color;
    static int messageLimit = 200;
	static boolean DEBUG = false;

	public static void main(String[] args) {
		String serverIP = args[0];
		int port = Integer.parseInt(args[1]);
		name = args[2];
		if (name.length() > 155)
			name = name.substring(0, 166).trim();
		color = "";
		if (args.length > 3)
			color = ConsoleColors.getColor(args[3]);

		prefix = "\n" + color + "You: " + ConsoleColors.RESET;

		try {
			// Connection
			Socket client = new Socket(serverIP, port);
			if (DEBUG) System.out.println("Just connected to " + client.getRemoteSocketAddress());
			System.out.println("Connected to Server as " + color + "[" + name + "]" + ConsoleColors.RESET);

			// Streams
			DataInputStream inFromServer = new DataInputStream(client.getInputStream());
			DataOutputStream outToServer = new DataOutputStream(client.getOutputStream());

			// Threads
			InputThread inputThread = new InputThread(inFromServer);
			OutputThread outputThread = new OutputThread(outToServer);

			inputThread.start();
			outputThread.start();

			while (outputThread.getState() != Thread.State.TERMINATED);

		} catch (IOException e) {
			if (DEBUG) e.printStackTrace();
		}
	}

	static class InputThread extends Thread {
		DataInputStream inFromServer;

		InputThread(DataInputStream i) {
			inFromServer = i;
		}

		public void run() {
			try {
				while (true) {
					String input = inFromServer.readUTF();
					if (!input.contains(name) && (input.contains("-=[") || input.contains("# "))) {
						System.out.println("\b\b\b\b\b" + input);
						System.out.print(prefix);
					}
				}
			} catch (IOException e) {
				if (DEBUG) e.printStackTrace();
			}
		}
	}

	static class OutputThread extends Thread {
		DataOutputStream outToServer;
		boolean kill;

		OutputThread(DataOutputStream o) {
			outToServer = o;
		}

		public void run() {
			while (!kill) {
				Scanner scanner = new Scanner(System.in);
				System.out.print(prefix);
				String message = scanner.nextLine();

                if (message.length() > messageLimit)
                    System.out.println("haha lol vergiss es");

				if (message.startsWith("!")) {
					String command = message.substring(1);
					String output = "";

					if (command.equals("quit")) {
						output = String.format("%s[ %s ]%s left the chat.",
						                       color, name, ConsoleColors.RESET);
						kill = true;
					} else if (command.equals("KILLTHESERVER")) {
						output = "!killtheserver";
						kill = true;
					} else if (command.equals("online")) {
						output = "!online " + name;
					} else if (command.startsWith("setLimit")) {
                        messageLimit = Integer.parseInt(command.split(" ")[1]);
                    } else if (command.equals("enableDebug")) {
                        System.out.println("DEBUG enabled");
                        DEBUG = true;
                    } else if (command.equals("disableDebug")) {
                        System.out.println("DEBUG disabled");
                        DEBUG = false;
                    }

					if (!output.equals("")) {
						try {
							outToServer.writeUTF(output);
						} catch (IOException e) {
							if (DEBUG) e.printStackTrace();
						}
					}
				} else if (!message.equals("") && message.length() <= messageLimit) {
					Calendar calendar = Calendar.getInstance();
					String output = String.format("%s-=[ %s ]=-%s   %s%02d:%02d%s\n> %s",
					                              color, name, ConsoleColors.RESET,
					                              ConsoleColors.BLACK_BOLD, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), ConsoleColors.RESET,
					                              ConsoleColors.ITALIC + message.trim() + ConsoleColors.RESET);

					try {
						outToServer.writeUTF(output);
					} catch (IOException e) {
						if (DEBUG) e.printStackTrace();
					}
				}


				if (kill) System.exit(0);
			}
		}
	}

	static class ConsoleColors {
		public static String getColor(String s) {
			String result = "";

			if (s.equalsIgnoreCase("BLACK")) result = BLACK;
			if (s.equalsIgnoreCase("RED")) result = RED;
			if (s.equalsIgnoreCase("GREEN")) result = GREEN;
			if (s.equalsIgnoreCase("YELLOW")) result = YELLOW;
			if (s.equalsIgnoreCase("BLUE")) result = BLUE;
			if (s.equalsIgnoreCase("PURPLE")) result = PURPLE;
			if (s.equalsIgnoreCase("CYAN")) result = CYAN;
			if (s.equalsIgnoreCase("WHITE")) result = WHITE;

			if (s.equalsIgnoreCase("BLACK_BOLD")) result = BLACK_BOLD;
			if (s.equalsIgnoreCase("RED_BOLD")) result = RED_BOLD;
			if (s.equalsIgnoreCase("GREEN_BOLD")) result = GREEN_BOLD;
			if (s.equalsIgnoreCase("YELLOW_BOLD")) result = YELLOW_BOLD;
			if (s.equalsIgnoreCase("BLUE_BOLD")) result = BLUE_BOLD;
			if (s.equalsIgnoreCase("PURPLE_BOLD")) result = PURPLE_BOLD;
			if (s.equalsIgnoreCase("CYAN_BOLD")) result = CYAN_BOLD;
			if (s.equalsIgnoreCase("WHITE_BOLD")) result = WHITE_BOLD;

			return result;
		}

		// Reset
		public static final String RESET = "\u001B[0m";    // Text Reset
		public static final String ITALIC = "\u001B[3m";    // Text Reset

		// Regular Colors
		public static final String BLACK = "\u001B[0;30m";    // BLACK
		public static final String RED = "\u001B[0;31m";        // RED
		public static final String GREEN = "\u001B[0;32m";    // GREEN
		public static final String YELLOW = "\u001B[0;33m";    // YELLOW
		public static final String BLUE = "\u001B[0;34m";     // BLUE
		public static final String PURPLE = "\u001B[0;35m";    // PURPLE
		public static final String CYAN = "\u001B[0;36m";     // CYAN
		public static final String WHITE = "\u001B[0;37m";    // WHITE

		// Bold
		public static final String BLACK_BOLD = "\u001B[1;30m";    // BLACK
		public static final String RED_BOLD = "\u001B[1;31m";     // RED
		public static final String GREEN_BOLD = "\u001B[1;32m";    // GREEN
		public static final String YELLOW_BOLD = "\u001B[1;33m"; // YELLOW
		public static final String BLUE_BOLD = "\u001B[1;34m";    // BLUE
		public static final String PURPLE_BOLD = "\u001B[1;35m"; // PURPLE
		public static final String CYAN_BOLD = "\u001B[1;36m";    // CYAN
		public static final String WHITE_BOLD = "\u001B[1;37m";    // WHITE

		// Underline
		public static final String BLACK_UNDERLINED = "\u001B[4;30m";    // BLACK
		public static final String RED_UNDERLINED = "\u001B[4;31m";     // RED
		public static final String GREEN_UNDERLINED = "\u001B[4;32m";    // GREEN
		public static final String YELLOW_UNDERLINED = "\u001B[4;33m"; // YELLOW
		public static final String BLUE_UNDERLINED = "\u001B[4;34m";    // BLUE
		public static final String PURPLE_UNDERLINED = "\u001B[4;35m"; // PURPLE
		public static final String CYAN_UNDERLINED = "\u001B[4;36m";    // CYAN
		public static final String WHITE_UNDERLINED = "\u001B[4;37m";    // WHITE

		// Background
		public static final String BLACK_BACKGROUND = "\u001B[40m";    // BLACK
		public static final String RED_BACKGROUND = "\u001B[41m";     // RED
		public static final String GREEN_BACKGROUND = "\u001B[42m";    // GREEN
		public static final String YELLOW_BACKGROUND = "\u001B[43m"; // YELLOW
		public static final String BLUE_BACKGROUND = "\u001B[44m";    // BLUE
		public static final String PURPLE_BACKGROUND = "\u001B[45m"; // PURPLE
		public static final String CYAN_BACKGROUND = "\u001B[46m";    // CYAN
		public static final String WHITE_BACKGROUND = "\u001B[47m";    // WHITE

		// High Intensity
		public static final String BLACK_BRIGHT = "\u001B[0;90m";    // BLACK
		public static final String RED_BRIGHT = "\u001B[0;91m";     // RED
		public static final String GREEN_BRIGHT = "\u001B[0;92m";    // GREEN
		public static final String YELLOW_BRIGHT = "\u001B[0;93m"; // YELLOW
		public static final String BLUE_BRIGHT = "\u001B[0;94m";    // BLUE
		public static final String PURPLE_BRIGHT = "\u001B[0;95m"; // PURPLE
		public static final String CYAN_BRIGHT = "\u001B[0;96m";    // CYAN
		public static final String WHITE_BRIGHT = "\u001B[0;97m";    // WHITE

		// Bold High Intensity
		public static final String BLACK_BOLD_BRIGHT = "\u001B[1;90m"; // BLACK
		public static final String RED_BOLD_BRIGHT = "\u001B[1;91m";    // RED
		public static final String GREEN_BOLD_BRIGHT = "\u001B[1;92m"; // GREEN
		public static final String YELLOW_BOLD_BRIGHT = "\u001B[1;93m";// YELLOW
		public static final String BLUE_BOLD_BRIGHT = "\u001B[1;94m";    // BLUE
		public static final String PURPLE_BOLD_BRIGHT = "\u001B[1;95m";// PURPLE
		public static final String CYAN_BOLD_BRIGHT = "\u001B[1;96m";    // CYAN
		public static final String WHITE_BOLD_BRIGHT = "\u001B[1;97m"; // WHITE

		// High Intensity backgrounds
		public static final String BLACK_BACKGROUND_BRIGHT = "\u001B[0;100m";// BLACK
		public static final String RED_BACKGROUND_BRIGHT = "\u001B[0;101m";// RED
		public static final String GREEN_BACKGROUND_BRIGHT = "\u001B[0;102m";// GREEN
		public static final String YELLOW_BACKGROUND_BRIGHT = "\u001B[0;103m";// YELLOW
		public static final String BLUE_BACKGROUND_BRIGHT = "\u001B[0;104m";// BLUE
		public static final String PURPLE_BACKGROUND_BRIGHT = "\u001B[0;105m"; // PURPLE
		public static final String CYAN_BACKGROUND_BRIGHT = "\u001B[0;106m";    // CYAN
		public static final String WHITE_BACKGROUND_BRIGHT = "\u001B[0;107m";    // WHITE
	}
}