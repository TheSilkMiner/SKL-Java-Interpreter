package net.thesilkminer.skl.interpreter.implementation.sks.components.markers;

import net.thesilkminer.skl.interpreter.api.sks.language.ComponentArguments;
import net.thesilkminer.skl.interpreter.api.sks.language.Location;
import net.thesilkminer.skl.interpreter.api.sks.language.components.ILanguageComponent;

import java.util.Optional;

/**
 * Represents the "end" keyword of the script.
 *
 * <p>Simply declares the end of the script.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class ScriptEndDeclaration implements ILanguageComponent {

	@Override
	public String getName() {

		return "end";
	}

	@Override
	public Optional<ComponentArguments> getArguments() {

		final ComponentArguments arguments = ComponentArguments.of();
		arguments.addArgument(ComponentArguments.INIT, null);

		return Optional.of(arguments.setImmutable());
	}

	@Override
	public String getSyntax() {

		return "end script";
	}

	@Override
	public boolean canApply(final ComponentArguments arguments) {

		return arguments.hasOnly(ComponentArguments.INIT)
				&& arguments.get(ComponentArguments.INIT).equals("script");
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

		return line.equals(this.getSyntax());
	}

	@Override
	public boolean hasErrored() {

		return false;
	}

	@Override
	public Optional<ComponentArguments> getNeededEdits() {

		final ComponentArguments edits = ComponentArguments.of();
		edits.addArgument("hasReachedEndOfScript", "true");

		return Optional.of(edits.setImmutable());
	}

	@Override
	public String getErrorMessage() {

		return String.format("Wrong syntax: %s", this.getSyntax());
	}

	@Override
	public boolean isLocationValid(final Location location) {

		return location.isPresent(new ScriptStartDeclaration());
	}

	@Override
	public String getInvalidLocationMessage() {

		return "End of script must be after start, you genius!";
	}
}
