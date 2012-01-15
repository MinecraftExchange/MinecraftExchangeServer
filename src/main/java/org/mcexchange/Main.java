package org.mcexchange;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Set up output.
		Logger log = Logger.getLogger("exchange");
		log.setUseParentHandlers(false);
		Handler c = new ConsoleHandler(); //why its called "c"? You have as much of an idea as I do.
		c.setFormatter(new DateOutputFormatter(new SimpleDateFormat("HH:mm:ss")));
		log.addHandler(c);
		System.setOut(new PrintStream(new LoggerOutputStream(log, Level.INFO), true));
		System.setErr(new PrintStream(new LoggerOutputStream(log, Level.SEVERE), true));
		log.info("Starting minecraft exchange server version 0.2");
		//Create server.
		ExchangeServer es = ExchangeServer.getInstance();
		log.info("Loading properties.");
		es.loadProperties(args);
		log.info("Loading plugins.");
		es.loadPlugins();
		Thread t = new Thread(es);
		t.start();
		//Listen for input.
		Scanner scan = new Scanner(System.in);
		while(true) {
			String s = scan.nextLine();
			if(s.equalsIgnoreCase("stop")) {//better option here one of these days. ;)
				shutdown(t, es);
			} else {
				es.broadcastMessage(s);
			}
		}
	}
	
	/**
	 * Ends all threads and exits the program safely.
	 * @param t
	 * @param es
	 */
	public static void shutdown(Thread t, ExchangeServer es) {
		System.out.println("Disconnecting from clients.");
		for(Thread thread : es.getThreads()) {
			thread.interrupt();
		}
		System.out.println("Shutting down server.");
		t.interrupt();
		System.exit(0);
	}
	
	/**
	 * Used to format console output.
	 */
	private static class DateOutputFormatter extends Formatter {
		private final SimpleDateFormat date;

		public DateOutputFormatter(SimpleDateFormat date) {
			this.date = date;
		}

		@Override
		public String format(LogRecord record) {
			StringBuilder builder = new StringBuilder();

			builder.append(date.format(record.getMillis()));
			builder.append(" [");
			builder.append(record.getLevel().getLocalizedName().toUpperCase());
			builder.append("] ");
			builder.append(formatMessage(record));
			builder.append('\n');

			if (record.getThrown() != null) {
				StringWriter writer = new StringWriter();
				record.getThrown().printStackTrace(new PrintWriter(writer));
				builder.append(writer.toString());
			}

			return builder.toString();
		}
	}
}
