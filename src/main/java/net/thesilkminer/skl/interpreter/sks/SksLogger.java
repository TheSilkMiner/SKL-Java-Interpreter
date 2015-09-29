package net.thesilkminer.skl.interpreter.sks;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;

/**
 * Created by TheSilkMiner on 13/09/2015.
 * Package: net.thesilkminer.skl.interpreter.sks.
 * Project: Java Interpreter.
 */
public class SksLogger {

	private static SksLogger singleton = new SksLogger();

	private Logger logger = Logger.getLogger("SKS Parser");

	public static SksLogger logger() {

		return singleton;
	}

	private void log(Level level, String msg) {

		logger.log(level, msg);
	}

	public void info(String msg) {

		this.log(Level.INFO, msg);
	}

	public void warn(String msg) {

		this.log(Level.WARNING, msg);
	}

	public void error(String msg) {

		this.log(Level.SEVERE, msg);
	}

	public void severe(String msg) {

		this.error(msg);
	}

	public void fine(String msg) {

		this.log(Level.FINE, msg);
	}

	/**
	 * Prints a stacktrace, along with a message.
	 *
	 * @param msg
	 * 		The message.
	 * @param throwable
	 * 		The exception
	 */
	public void stacktrace(@Nonnull String msg, Throwable throwable) {

		this.error(msg);
		final StackTraceElement[] elements = throwable.getStackTrace();
		for (StackTraceElement element : elements) {

			this.error(element.toString());
		}
	}

	/**
	 * Prints a stacktrace.
	 *
	 * @param throwable
	 * 		The exception
	 */
	public void stacktrace(Throwable throwable) {

		this.error(throwable.getLocalizedMessage());
		final StackTraceElement[] elements = throwable.getStackTrace();
		for (StackTraceElement element : elements) {

			this.error(element.toString());
		}
	}
}
