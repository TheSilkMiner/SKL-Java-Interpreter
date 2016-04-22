package net.thesilkminer.skl.interpreter.api.skd.logging;

import javax.annotation.Nonnull;

/**
 * Represents the logger of the database.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public interface ISkdLogger {

	/**
	 * Logs an info message.
	 *
	 * @param msg
	 * 		The message to log.
	 *
	 * @since 0.2
	 */
	void info(final String msg);

	/**
	 * Logs a warning message.
	 *
	 * @param msg
	 * 		The message to log.
	 *
	 * @since 0.2
	 */
	void warn(final String msg);

	/**
	 * Logs an error message.
	 *
	 * @param msg
	 * 		The message to log.
	 *
	 * @since 0.2
	 */
	void error(final String msg);

	/**
	 * Logs an error message.
	 *
	 * @param msg
	 * 		The message to log.
	 *
	 * @since 0.2
	 */
	default void severe(final String msg) {

		this.error(msg);
	}

	/**
	 * Logs a fine message.
	 *
	 * @param msg
	 * 		The message to log.
	 *
	 * @since 0.2
	 */
	void fine(final String msg);

	/**
	 * Prints a stacktrace, along with a message.
	 *
	 * @param msg
	 * 		The message.
	 * @param throwable
	 * 		The exception.
	 *
	 * @since 0.2
	 */
	void stacktrace(@Nonnull final String msg, final Throwable throwable);

	/**
	 * Prints a stacktrace.
	 *
	 * @param throwable
	 * 		The exception.
	 *
	 * @since 0.2
	 */
	void stacktrace(final Throwable throwable);
}
