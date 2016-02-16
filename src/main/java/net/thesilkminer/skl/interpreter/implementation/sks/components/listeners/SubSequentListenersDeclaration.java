package net.thesilkminer.skl.interpreter.implementation.sks.components.listeners;

import net.thesilkminer.skl.interpreter.api.sks.language.ComponentArguments;
import net.thesilkminer.skl.interpreter.api.sks.language.Location;
import net.thesilkminer.skl.interpreter.api.sks.language.components.ILanguageComponent;

import java.util.Optional;

/**
 * Represents the "subsequent" keyword of the script.
 *
 * <p>Used to declare the listeners we need to pass the script to.</p>
 *
 * <p>When using this declaration, all the listeners are called in
 * the specified order. The state is preserved, though. In other
 * words, every listener will execute following the previous changes
 * operated by previous listeners to the script.</p>
 *
 * <p>If one of the scripts in the chain is not present or does not
 * support the subsequent architecture, the execution will fail.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class SubSequentListenersDeclaration implements ILanguageComponent {

	private String listeners;

	@Override
	public String getName() {

		return "subSequentListeners";
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

		return "subsequent <listeners...>";
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
	public boolean parse(final ComponentArguments arguments) {

		if (!this.canApply(arguments)) {

			throw new ILanguageComponent.WrongSyntaxException(this);
		}

		final String listeners = arguments.get(
				      ComponentArguments.asVararg(
						    ComponentArguments.INIT)
		);

		String string = "SS#";
		boolean gotApix = false;

		for (int i = 0; i < listeners.length(); ++i) {

			char character = listeners.charAt(i);

			if (character == '"') {

				final boolean before = gotApix;

				gotApix = !gotApix;

				if (before) {

					string += ";SS#";
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

		return !this.listeners.isEmpty();
	}

	@Override
	public boolean parseFallback(final String line) {

		final int firstApix = line.indexOf('"');

		String string = "SS#";
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

				string += ";SS#";
			}
		}

		this.listeners = string;

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

		return "SubsequentListener invocation should be before script body.";
	}

	@Override
	public String getScriptDeclaration() {

		return "subsequent";
	}
}
