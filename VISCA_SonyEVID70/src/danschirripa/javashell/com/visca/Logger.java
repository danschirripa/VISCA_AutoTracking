package danschirripa.javashell.com.visca;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Resource class which provides writing to the default Output, or a redirected
 * OutputStream. Loggers should be obtained through the LogManager.
 * 
 * @author dan
 * 
 */
public class Logger implements Serializable {
	final private transient String name;
	final private transient int id;
	private transient PrintStream out = System.out;
	private transient PrintStream debug = System.out;

	/**
	 * Create a Logger with this numerical ID and Display Name
	 * 
	 * @param name Display Name
	 * @param id   Numerical ID
	 */
	public Logger(String name, int id) {
		this.id = id;
		this.name = name + ":" + id;
	}

	/**
	 * Create a Logger with this numerical ID, the ID will be used as the Display
	 * Name
	 * 
	 * @param id Numerical ID
	 */
	public Logger(int id) {
		this.name = "ID:" + id;
		this.id = id;
	}

	/**
	 * Set the PrintStream for which debug messages should be logged to, if set to
	 * null use same as default logs
	 * 
	 * @param out PrintStream for which debug messages should be written
	 */
	public void setDebugStream(PrintStream out) {
		if (out == null) {
			this.debug = this.out;
		}
		this.debug = out;
	}

	/**
	 * Write a String out to the current OutputStream. This String will be checked
	 * for variables within the String unless otherwise specified.
	 * 
	 * @param s String to write.
	 */
	public void log(String s) {
		String[] sA = { s };
		actual(sA);
	}

	/**
	 * Write an array of Strings out to the current OutputStream. This Array will be
	 * checked for variables within itself unless otherwise specified.
	 * 
	 * @param s Array Of String to be written.
	 */
	public void log(String[] s) {
		actual(s);
	}

	private void actual(String[] s) {
		print("[" + name + "]");
		for (int i = 0; i < s.length; i++) {
			print(s[i] + " ");
		}
		endln();
	}

	/**
	 * End the current line.
	 */
	public synchronized void endln() {
		out.print("\n");
		return;
	}

	/**
	 * Write out to this Stream temporarily. Only one line will be written here
	 * 
	 * @param s   String to be written.
	 * @param out PrintStream to write out to.
	 */
	public synchronized void log(String s, PrintStream out) {
		out.print("[" + name + "] " + s + "\n");
	}

	/**
	 * Print this String without ending the line, and without any Display Name.
	 * 
	 * @param s String to be written.
	 */
	public synchronized void print(String s) {
		synchronized (out) {
			out.print(s);
			out.flush();
		}
		return;
	}

	/**
	 * Write an Error, this goes to System.err.
	 * 
	 * @param s String to be written.
	 */
	public void err(String s) {
		System.err.print("[" + name + "] " + s + "\n");
	}

	public void debug(String s, int level) {
		debug(s, debug);
	}

	public synchronized void debug(String s, int level, PrintStream out) {
		// TODO Add if statements to clarify debug verbosity
		synchronized (out) {
			debug(s, out);
			out.flush();
		}
	}

	/**
	 * Debug stream, only prints if "debug" is marked true in launch configuration
	 * 
	 * @param s String to be written
	 */
	public void debug(String s) {
		debug(s, debug);
	}

	/**
	 * Debug stream, only prints if "debug" is marked true in launch configuration
	 * 
	 * @param s   String to be written
	 * @param out Stream to write to
	 */
	public synchronized void debug(String s, PrintStream out) {
		log("[DEBUG] " + s + " @:" + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + ":"
				+ LocalDateTime.now().getSecond(), out);
	}

	/**
	 * Obtain the Display Name of this Logger.
	 * 
	 * @return The Logger's Display Name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Obtain the numerical ID of this Logger.
	 * 
	 * @return The Numerical ID of this Logger.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Clear the console
	 * 
	 */
	public void clear() {
		try {
			int col = Integer.parseInt(System.getenv("COLUMNS"));
			for (int i = 0; i < col; i++) {
				out.println();
			}
		} catch (Exception e) {
			final int col = 80;
			for (int i = 0; i < col; i++) {
				out.println();
			}
		}
	}

	/**
	 * Obtain the currently used OutputStream to write to.
	 * 
	 * @return The current OutputStream.
	 */
	public OutputStream getOutputStream() {
		return out;
	}

	/**
	 * Change the current OutputStream.
	 * 
	 * @param out The OutputStream to be written to.
	 */
	public void setOutputStream(OutputStream out) {
		this.out = new PrintStream(out);
		debug = this.out;
	}
}