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
	 * The property that loggers should use to identify
	 * if debug logging is activated.
	 *
	 * @since 0.2.1
	 */
	String DEBUG_PROPERTY = "net.thesilkminer.skl.interpreter.logging.debug";

	/**
	 * The value assumed by {@link #DEBUG_PROPERTY} when
	 * activated.
	 *
	 * @since 0.2.1
	 */
	String DEBUG_ON = "true";

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

	/**
	 * Prints a debug message, if the option is turned on.
	 *
	 * @param msg
	 *      The message to print.
	 *
	 * @implNote
	 *      The default implementation is
	 *      <blockquote><pre>
	 *          if (DEBUG_ON.equals(System.getProperty(DEBUG_PROPERTY))) this.info(msg);
	 *          else this.fine(msg);
	 *      </pre></blockquote>
	 *
	 * @since 0.2.1
	 */
	default void debug(final String msg) {
		if (DEBUG_ON.equals(System.getProperty(DEBUG_PROPERTY))) {
			this.info(msg);
		} else {
			this.fine(msg);
		}
	}
}
