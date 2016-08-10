package net.thesilkminer.skl.interpreter.implementation.skd;

import net.thesilkminer.skl.interpreter.api.skd.logging.ISkdLogger;

import org.jetbrains.annotations.Contract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

/**
 * Logger used by the SKD environment.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class SkdLogger implements ISkdLogger {

	private class LogOutputHandler extends Handler {

		private class LogFormatter extends Formatter {

			private final DateFormat dateFormat =
					      new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

			@Override
			public String format(final LogRecord record) {
				final StringBuilder builder = new StringBuilder();

				builder.append(dateFormat.format(new Date(record.getMillis())));
				builder.append(" [");

				final String sourceClassName = record.getSourceClassName();

				builder.append(sourceClassName.substring(sourceClassName
						      .lastIndexOf('.') + 1));
				builder.append("] [");
				builder.append(record.getLevel());
				builder.append("] ");
				builder.append(this.formatMessage(record));
				builder.append("\n");


				return builder.toString();
			}
		}

		@Override
		public void publish(final LogRecord record) {
			if (this.getFormatter() == null) {
				this.setFormatter(new LogFormatter());
			}

			try {
				final String message = this.getFormatter().format(record);

				if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
					System.err.write(message.getBytes());
				} else {
					System.out.write(message.getBytes());
				}
			} catch (final Exception exception) {

				this.reportError(null, exception, ErrorManager.FORMAT_FAILURE);
			}

		}

		@Override
		public void close() throws SecurityException {}

		@Override
		public void flush() {}
	}

	private static final ISkdLogger SINGLETON = new SkdLogger();

	private final Logger logger = Logger.getLogger("SKD Parser");

	private SkdLogger() {
		this.logger.setUseParentHandlers(false);
		this.logger.addHandler(new LogOutputHandler());
	}

	/**
	 * Returns the logger singleton.
	 *
	 * @return
	 * 		The logger singleton.
	 *
	 * @since 0.2
	 */
	@Contract(pure = true)
	@Nonnull
	public static ISkdLogger get() {
		return SINGLETON;
	}

	@Override
	public void info(@Nonnull final String msg) {
		this.logger.log(Level.INFO, msg);
	}

	@Override
	public void warn(@Nonnull final String msg) {
		this.logger.log(Level.WARNING, msg);
	}

	@Override
	public void error(@Nonnull final String msg) {
		this.logger.log(Level.SEVERE, msg);
	}

	@Override
	public void fine(@Nonnull final String msg) {
		this.logger.log(Level.FINE, msg);
	}

	@Override
	public void stacktrace(@Nonnull final String msg, @Nonnull final Throwable throwable) {
		this.error(msg);
		final StackTraceElement[] elements = throwable.getStackTrace();
		for (final StackTraceElement element : elements) {

			this.error(element.toString());
		}
	}

	@Override
	public void stacktrace(@Nonnull final Throwable throwable) {
		this.error(throwable.getClass().getName());
		this.stacktrace(throwable.getLocalizedMessage(), throwable);
	}
}
