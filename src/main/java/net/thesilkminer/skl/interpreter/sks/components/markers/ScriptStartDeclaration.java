package net.thesilkminer.skl.interpreter.sks.components.markers;

import net.thesilkminer.skl.interpreter.sks.ComponentArguments;
import net.thesilkminer.skl.interpreter.sks.ILanguageComponent;
import net.thesilkminer.skl.interpreter.sks.Location;
import net.thesilkminer.skl.interpreter.sks.components.declaration.ScriptDeclaration;
import net.thesilkminer.skl.interpreter.sks.components.language.LanguageDeclaration;

import java.util.Optional;

/**
 * Represents the "start" keyword of the script.
 *
 * <p>Simply declares the start of the script.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class ScriptStartDeclaration implements ILanguageComponent {

	@Override
	public String getName() {

		return "start";
	}

	@Override
	public Optional<ComponentArguments> getArguments() {

		final ComponentArguments arguments = ComponentArguments.of();
		arguments.addArgument(ComponentArguments.INIT, null);

		return Optional.of(arguments.setImmutable());
	}

	@Override
	public String getSyntax() {

		return "start script";
	}

	@Override
	public boolean canApply(final ComponentArguments arguments) {

		return arguments.hasOnly(ComponentArguments.INIT)
				&& arguments.get(ComponentArguments.INIT).equals("script");
	}

	@Override
	public boolean parse(ComponentArguments arguments) {

		if (!this.canApply(arguments)) {

			throw new ILanguageComponent.WrongSyntaxException(this);
		}

		return true;
	}

	@Override
	public boolean parseFallback(String line) {

		return line.equals(this.getSyntax());
	}

	@Override
	public boolean hasErrored() {

		return false;
	}

	@Override
	public Optional<ComponentArguments> getNeededEdits() {

		ComponentArguments edits = ComponentArguments.of();
		edits.addArgument("hasReachedStart", "true");

		return Optional.of(edits);
	}

	@Override
	public String getErrorMessage() {

		return String.format("Wrong syntax: %s", this.getSyntax());
	}

	@Override
	public boolean isLocationValid(final Location location) {

		return location.isPresent(new LanguageDeclaration())
				&& location.isPresent(new ScriptDeclaration());
	}

	@Override
	public String getInvalidLocationMessage() {

		return "Script start must be after script declaration and language";
	}
}
