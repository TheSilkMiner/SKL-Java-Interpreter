package net.thesilkminer.skl.interpreter.implementation.sks.components.language;

import net.thesilkminer.skl.interpreter.api.sks.language.ComponentArguments;
import net.thesilkminer.skl.interpreter.api.sks.language.Location;
import net.thesilkminer.skl.interpreter.api.sks.language.components.ILanguageComponent;

import java.util.Optional;

/**
 * Represents the "language" keyword of the SKS script.
 *
 * <p>This language component sets the language of the code in the script.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class LanguageDeclaration implements ILanguageComponent {

	private String language;

	@Override
	public String getName() {

		return "language";
	}

	@Override
	public Optional<ComponentArguments> getArguments() {

		final ComponentArguments arguments = ComponentArguments.of(
										ComponentArguments
											.INIT,
										null);
		return Optional.of(arguments.setImmutable());
	}

	@Override
	public String getSyntax() {

		return "language <language>";
	}

	@Override
	public boolean canApply(final ComponentArguments arguments) {

		return arguments.hasOnly(ComponentArguments.INIT);
	}

	@Override
	public boolean parse(final ComponentArguments arguments) {

		if (!this.canApply(arguments)) {

			throw new ILanguageComponent.WrongSyntaxException(this);
		}

		this.language = ComponentArguments.removeApixes(
				arguments.get(ComponentArguments.INIT));

		return this.language != null && !this.language.isEmpty();
	}

	@Override
	public boolean parseFallback(final String line) {

		final int firstApix = line.indexOf('"');
		final int lastApix = line.lastIndexOf('"');

		if (firstApix == lastApix && firstApix == -1) {

			return false;
		}

		this.language = ComponentArguments.removeApixes(
				line.substring(firstApix, lastApix));

		return this.language != null && !this.language.isEmpty();
	}

	@Override
	public boolean hasErrored() {

		return false;
	}

	@Override
	public String getErrorMessage() {

		return "Language was not specified correctly";
	}


	@Override
	public Optional<ComponentArguments> getNeededEdits() {

		ComponentArguments edits = ComponentArguments.of("actualLanguage", this.language);
		edits.addArgument("doWeKnowLanguage", "true");

		return Optional.of(edits.setImmutable());
	}

	@Override
	public boolean isLocationValid(final Location location) {

		return location.isFirst();
	}

	@Override
	public String getInvalidLocationMessage() {

		return "Language declaration must be placed first.";
	}
}
