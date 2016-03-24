package net.thesilkminer.skl.interpreter.implementation.sks.components.listeners;

import com.google.common.collect.Lists;

import net.thesilkminer.skl.interpreter.api.sks.language.ComponentArguments;
import net.thesilkminer.skl.interpreter.api.sks.language.Location;
import net.thesilkminer.skl.interpreter.api.sks.language.components.ILanguageComponent;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Represents the "fallbacklisteners" keyword of the script.
 *
 * <p>Used to declare the listeners we need to pass the script to.</p>
 *
 * <p>When using this declaration, all the listeners are called in
 * the specified order. The state is not preserved, though. In other
 * words, every listener will execute like it s the first listener
 * to run the script.</p>
 *
 * <p>If one of the scripts in the chain is not present, the execution
 * will skip to the next one. If the specified listeners are not for
 * the language specified, then the execution will fail anyway.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class FallBackListenersDeclaration implements ILanguageComponent {

	private static final List<String> ALL_FALL_BACK_LISTENERS = Lists.newArrayList();
	private String listeners;

	@Override
	public String getName() {

		return "fallbackListeners";
	}

	@Override
	public Optional<ComponentArguments> getArguments() {

		final ComponentArguments args = ComponentArguments.of();
		args.addArgument(ComponentArguments.INIT, null, true);
		args.setImmutable();

		return Optional.of(args);
	}

	@Override
	public String getSyntax() {

		return "fallbacklisteners <listeners...>";
	}

	@Override
	public boolean canApply(final ComponentArguments arguments) {

		return arguments.hasOnly(ComponentArguments.asVararg(ComponentArguments.INIT));
	}

	@Override
	public boolean isLocationValid(final Location location) {

		return location.isBeforeScript() && location.noOtherListenersPresent();
	}

	@Override
	public boolean parse(ComponentArguments arguments) {

		if (!this.canApply(arguments)) {

			throw new ILanguageComponent.WrongSyntaxException(this);
		}

		final String listeners = arguments.get(
				      ComponentArguments.asVararg(
						      ComponentArguments.INIT)
		);

		String string = "";
		boolean gotApix = false;

		for (int i = 0; i < listeners.length(); ++i) {

			char character = listeners.charAt(i);

			if (character == '"') {

				final boolean before = gotApix;

				gotApix = !gotApix;

				if (before) {

					string += ";";
				}

				continue;
			}

			if (gotApix) {

				string += character;
			}
		}

		if (string.endsWith(";")) {

			string = string.substring(0, string.length() - 1);
		}

		this.listeners = string;

		String[] array;

		if (string.contains(";")) {

			array = string.split(Pattern.quote(";"));
		} else {

			array = new String[] {string};
		}

		for (String listener : array) {

			if (listener.endsWith(";")) {

				listener = listener.substring(0, listener.length() - 1);
			} else if (listener.startsWith(";")) {

				listener = listener.substring(1);
			}

			ALL_FALL_BACK_LISTENERS.add(listener);
		}

		return !this.listeners.isEmpty();
	}

	@Override
	public boolean parseFallback(String line) {

		final int firstApix = line.indexOf('"');

		String string = "";
		boolean gotApix = false;

		for (int i = firstApix - 1; i < line.length(); ++i) {

			char character = listeners.charAt(i);
			boolean prevVal = gotApix;

			if (character == '"') {

				gotApix = !gotApix;
			}

			if (gotApix) {

				string += character;
			}

			if (prevVal) {

				string += ';';
			}
		}

		this.listeners = string;

		String[] array;

		if (string.contains(";")) {

			array = string.split(Pattern.quote(";"));
		} else {

			array = new String[] {string};
		}

		for (String listener : array) {

			if (listener.endsWith(";")) {

				listener = listener.substring(0, listener.length() - 1);
			} else if (listener.startsWith(";")) {

				listener = listener.substring(1);
			}

			ALL_FALL_BACK_LISTENERS.add(listener);
		}

		return !this.listeners.isEmpty();
	}

	@Override
	public boolean hasErrored() {

		return false;
	}

	@Override
	public Optional<ComponentArguments> getNeededEdits() {

		final ComponentArguments edits = ComponentArguments.of();
		edits.addArgument("listenerClass", this.listeners);
		edits.setImmutable();

		return Optional.of(edits);
	}

	@Override
	public String getErrorMessage() {

		return "Array of listener was not specified correctly.";
	}

	@Override
	public String getInvalidLocationMessage() {

		return "FallbackListener invocation should be before script body.";
	}

	@Override
	public String getScriptDeclaration() {

		return this.getName().toLowerCase(Locale.ENGLISH);
	}

	public static boolean wasFallBack(final String listener) {

		return ALL_FALL_BACK_LISTENERS.contains(listener);
	}
}
