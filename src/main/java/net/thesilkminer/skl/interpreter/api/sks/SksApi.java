package net.thesilkminer.skl.interpreter.api.sks;

import net.thesilkminer.skl.interpreter.api.sks.holder.IScriptHolder;
import net.thesilkminer.skl.interpreter.api.sks.listener.IScriptListener;
import net.thesilkminer.skl.interpreter.api.sks.logging.ISksLogger;
import net.thesilkminer.skl.interpreter.api.sks.parser.ISksParser;

import java.io.File;
import java.lang.reflect.Method;
import javax.annotation.Nonnull;

/**
 * Represents the access point of the SKS Api.
 *
 * <p>Every call to the API must be performed from here.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class SksApi {

	private static final SksApi SINGLETON = new SksApi();

	private ISksLogger loggerCache;

	private SksApi() { }

	/**
	 * Gets the singleton instance of the API.
	 *
	 * @return
	 * 		The singleton instance of the API.
	 *
	 * @since 0.2
	 */
	public static SksApi get() {

		return SINGLETON;
	}

	/**
	 * Gets the logger instance of the current implementation.
	 *
	 * @return
	 * 		The logger instance of the current implementation.
	 */
	public ISksLogger getLogger() {

		if (loggerCache != null) {

			return loggerCache;
		}

		try {

			Class<?> loggerClass = Class.forName("net.thesilkminer.skl."
					      + "interpreter.implementation.sks.SksLogger");

			Method logger = loggerClass.getDeclaredMethod("logger");

			ISksLogger result = (ISksLogger) logger.invoke(null);
			loggerCache = result;
			return result;
		} catch (final ReflectiveOperationException e) {

			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the parser instance of the current implementation.
	 *
	 * @param holder
	 * 		The IScriptHolder you need to create the parser for.
	 * @return
	 * 		The parser instance of the current implementation.
	 */
	public ISksParser getParser(@Nonnull final IScriptHolder holder) {

		try {

			Class<?> parserClass = Class.forName("net.thesilkminer.skl."
					       + "interpreter.implementation.sks.SksParser");

			Method of = parserClass.getDeclaredMethod("of", IScriptHolder.class);

			return (ISksParser) of.invoke(null, holder);
		} catch (final ReflectiveOperationException e) {

			throw new RuntimeException(e);
		}
	}

	/**
	 * Registers a listener for the specified language.
	 *
	 * @param listener
	 * 		The listener to register.
	 * @return
	 * 		If the registration was successful.
	 */
	public Boolean registerListener(@Nonnull final IScriptListener listener) {

		try {

			Class<?> parserClass = Class.forName("net.thesilkminer.skl."
					      + "interpreter.implementation.sks.SksParser");

			Method listenerM = parserClass.getMethod("listener", IScriptListener.class);

			return (Boolean) listenerM.invoke(null, listener);
		} catch (final ReflectiveOperationException e) {

			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets a new script holder of the current implementation.
	 *
	 * @param file
	 * 		The file you need to create the holder for.
	 * @return
	 * 		The holder instance of the current implementation.
	 */
	public IScriptHolder getDefaultScriptHolderForFile(final File file) {

		try {

			Class<?> sksFileClass = Class.forName("net.thesilkminer.skl."
					      + "interpreter.implementation.sks.ScriptFile");

			Method of = sksFileClass.getMethod("of", File.class);

			return (IScriptHolder) of.invoke(null, file);
		} catch (final ReflectiveOperationException e) {

			throw new RuntimeException(e);
		}
	}
}
