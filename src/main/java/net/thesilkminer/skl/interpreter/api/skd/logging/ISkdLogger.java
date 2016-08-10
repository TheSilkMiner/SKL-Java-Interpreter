package net.thesilkminer.skl.interpreter.api.skd.logging;

import org.jetbrains.annotations.NonNls;

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
	@NonNls	String DEBUG_PROPERTY = "net.thesilkminer.skl.interpreter.logging.debug";

	/**
	 * The value assumed by {@link #DEBUG_PROPERTY} when
	 * activated.
	 *
	 * @since 0.2.1
	 */
	@NonNls String DEBUG_ON = "true";

	/**
	 * Logs an info message.
	 *
	 * @param msg
	 * 		The message to log.
	 *
	 * @since 0.2
	 */
	void info(@Nonnull final String msg);

	/**
	 * Logs a warning message.
	 *
	 * @param msg
	 * 		The message to log.
	 *
	 * @since 0.2
	 */
	void warn(@Nonnull final String msg);

	/**
	 * Logs an error message.
	 *
	 * @param msg
	 * 		The message to log.
	 *
	 * @since 0.2
	 */
	void error(@Nonnull final String msg);

	/**
	 * Logs an error message.
	 *
	 * @param msg
	 * 		The message to log.
	 *
	 * @since 0.2
	 */
	default void severe(@Nonnull final String msg) {
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
	void fine(@Nonnull final String msg);

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
	void stacktrace(@Nonnull final String msg, @Nonnull final Throwable throwable);

	/**
	 * Prints a stacktrace.
	 *
	 * @param throwable
	 * 		The exception.
	 *
	 * @since 0.2
	 */
	void stacktrace(@Nonnull final Throwable throwable);

	/**
	 * Prints a debug message, if the option is turned on.
	 *
	 * @param msg
	 *      The message to print.
	 *
	 * @implNote
	 *      The default implementation is
	 *      <pre>
	 *          if (DEBUG_ON.equals(System.getProperty(DEBUG_PROPERTY))) this.info(msg);<br />
	 *          else this.fine(msg);
	 *      </pre>
	 *
	 * @since 0.2.1
	 */
	default void debug(@Nonnull final String msg) {
		if (DEBUG_ON.equals(System.getProperty(DEBUG_PROPERTY))) {
			this.info(msg);
		} else {
			this.fine(msg);
		}
	}
}
