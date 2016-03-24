package net.thesilkminer.skl.interpreter.implementation.sks.listeners.custom.register.listeners;

import net.thesilkminer.skl.interpreter.api.sks.SksApi;
import net.thesilkminer.skl.interpreter.api.sks.holder.IScriptHolder;
import net.thesilkminer.skl.interpreter.api.sks.language.ComponentArguments;
import net.thesilkminer.skl.interpreter.api.sks.language.IllegalScriptException;
import net.thesilkminer.skl.interpreter.api.sks.language.Location;
import net.thesilkminer.skl.interpreter.api.sks.language.components.ILanguageComponent;
import net.thesilkminer.skl.interpreter.api.sks.listener.IScriptListener;
import net.thesilkminer.skl.interpreter.api.sks.listener.Result;
import net.thesilkminer.skl.interpreter.api.sks.parser.ISksParser;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Represents the listener for the "listenerReg" language.
 *
 * <p><strong>USAGE:</strong></p>
 *
 * <p>This custom language can be used to define a set of custom
 * listeners.</p>
 *
 * <p><strong>SYNTAX:</strong></p>
 *
 * <p>Simply write the full name of the listeners class in the
 * script body.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class ListenerRegisterListener implements IScriptListener {

	/**
	 * Why not? Let's use this instead of a custom exception.
	 */
	private class Dummy implements ILanguageComponent {

		@Override
		public String getName() {

			return null;
		}

		@Override
		public Optional<ComponentArguments> getArguments() {

			return null;
		}

		@Override
		public String getSyntax() {

			return "Existent listener class expected";
		}

		@Override
		public boolean canApply(final ComponentArguments arguments) {

			return false;
		}

		@Override
		public boolean isLocationValid(final Location location) {

			return false;
		}

		@Override
		public boolean parse(final ComponentArguments arguments) {

			return false;
		}

		@Override
		public boolean parseFallback(final String line) {

			return false;
		}

		@Override
		public boolean hasErrored() {

			return false;
		}

		@Override
		public Optional<ComponentArguments> getNeededEdits() {

			return null;
		}

		@Override
		public String getErrorMessage() {

			return null;
		}

		@Override
		public String getInvalidLocationMessage() {

			return null;
		}
	}

	private static boolean staticRegOk;

	@Override
	public String listenerFor() {

		return "listener-registration";
	}

	@Override
	public boolean needsInit() {

		return true;
	}

	@Override
	public boolean hasAlreadyInit() {

		return staticRegOk;
	}

	@Override
	public void init(final ISksParser parser, final IScriptHolder scriptFile) {

		staticRegOk = true;
	}

	@Override
	public void runScript(final List<String> lines) {

		lines.stream().forEach(this::registerListener);
	}

	private void registerListener(final String listener) {

		try {

			Class<?> listenerClass = Class.forName(listener);
			Object newInstance = listenerClass.newInstance();

			if (!(newInstance instanceof IScriptListener)) {

				// Why not?
				throw new ReflectiveOperationException("Invalid listener class\n"
						+ "It must extend IScriptListener.");
			}

			IScriptListener instance = (IScriptListener) newInstance;
			staticRegOk = SksApi.get().registerListener(instance);
		} catch (final ReflectiveOperationException e) {

			SksApi.get().getLogger().stacktrace("Script error!", e);

			try {

				// Not exactly its purpose, but who cares, right?
				throw new ILanguageComponent.WrongSyntaxException(new Dummy());
			} catch (final ILanguageComponent.WrongSyntaxException ex) {

				throw new IllegalScriptException(ex);
			}
		}
	}

	@Override
	public Result result() {

		if (staticRegOk) {

			return Result.SUCCESSFUL;
		}

		return Result.WARNING;
	}

	@Override
	public Optional<List<String>> toLog() {

		return Optional.empty();
	}

	/**
	 * Reads the specified file and automatically adds all the
	 * various listeners it finds in it.
	 *
	 * <p>The script file's extension <strong>MUST BE</strong>
	 * {@code .sks}</p>
	 *
	 * @param script
	 * 		The script file.
	 * @return
	 * 		If the operation was successful.
	 */
	@SuppressWarnings("unused") //API method
	public static boolean addListeners(final File script) {

		final IScriptHolder holder = SksApi.get().getDefaultScriptHolderForFile(script);
		final ISksParser parser = SksApi.get().getParser(holder);

		if (!parser.init() && !parser.errored()) {

			parser.initParser(false);
		}

		parser.parse();

		return staticRegOk;
	}
}
