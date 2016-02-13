package net.thesilkminer.skl.interpreter.implementation.sks.components.listeners;

import net.thesilkminer.skl.interpreter.api.sks.language.ComponentArguments;
import net.thesilkminer.skl.interpreter.api.sks.language.Location;
import net.thesilkminer.skl.interpreter.api.sks.language.components.ILanguageComponent;

import java.util.Optional;

/**
 * Represents the "nolistener" keyword of the script.
 *
 * <p>Used to avoid every listener processing. Useful with sub-scripts
 * (yet to be implemented).</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class NoListenerDeclaration implements ILanguageComponent {

	@Override
	public String getName() {

		return "noListeners";
	}

	@Override
	public Optional<ComponentArguments> getArguments() {

		return Optional.of(ComponentArguments.empty());
	}

	@Override
	public String getSyntax() {

		return this.getScriptDeclaration();
	}

	@Override
	public boolean canApply(final ComponentArguments arguments) {

		return arguments.equals(ComponentArguments.empty());
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

		return true;
	}

	@Override
	public boolean parseFallback(final String line) {

		return line.length() != this.getScriptDeclaration().length();
	}

	@Override
	public boolean hasErrored() {

		return false;
	}

	@Override
	public Optional<ComponentArguments> getNeededEdits() {

		final ComponentArguments edits = ComponentArguments.of();
		edits.addArgument("listenerClass", null);
		edits.setImmutable();

		return Optional.of(edits);
	}

	@Override
	public String getErrorMessage() {

		return "How did this happen?";
	}

	@Override
	public String getInvalidLocationMessage() {

		return "Declaration must be before script.";
	}

	@Override
	public String getScriptDeclaration() {

		return "nolistener";
	}
}
