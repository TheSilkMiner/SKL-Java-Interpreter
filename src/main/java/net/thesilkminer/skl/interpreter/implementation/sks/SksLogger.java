package net.thesilkminer.skl.interpreter.implementation.sks;

import net.thesilkminer.skl.interpreter.api.sks.logging.ISksLogger;

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
 * Logger class used by the SKS parser.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public class SksLogger implements ISksLogger {

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

	private static SksLogger singleton = new SksLogger();

	private Logger logger = Logger.getLogger("SKS Parser");

	private SksLogger() {

		this.logger.setUseParentHandlers(false);
		this.logger.addHandler(new LogOutputHandler());
	}

	/**
	 * Returns the logger singleton.
	 *
	 * @return
	 * 		The logger singleton.
	 *
	 * @since 0.1
	 */
	public static SksLogger logger() {

		return singleton;
	}

	private void log(Level level, String msg) {

		logger.log(level, msg);
	}

	@Override
	public void info(String msg) {

		this.log(Level.INFO, msg);
	}

	@Override
	public void warn(String msg) {

		this.log(Level.WARNING, msg);
	}

	@Override
	public void error(String msg) {

		this.log(Level.SEVERE, msg);
	}

	@Override
	public void severe(String msg) {

		this.error(msg);
	}

	@Override
	public void fine(String msg) {

		this.log(Level.FINE, msg);
	}

	@Override
	public void stacktrace(@Nonnull String msg, Throwable throwable) {

		this.error(msg);
		final StackTraceElement[] elements = throwable.getStackTrace();
		for (final StackTraceElement element : elements) {

			this.error(element.toString());
		}
	}

	@Override
	public void stacktrace(Throwable throwable) {

		this.error(throwable.getLocalizedMessage());
		final StackTraceElement[] elements = throwable.getStackTrace();
		for (final StackTraceElement element : elements) {

			this.error(element.toString());
		}
	}
}
