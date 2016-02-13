package net.thesilkminer.skl.interpreter.implementation.sks.components.decisionals;


import net.thesilkminer.skl.interpreter.api.sks.language.ComponentArguments;
import net.thesilkminer.skl.interpreter.api.sks.language.Location;
import net.thesilkminer.skl.interpreter.api.sks.language.components.ILanguageComponent;

import java.util.Optional;

/**
 * Represents the "endif" keyword of the script.
 *
 * <p>This can be used to make decisions and remove an entire part
 * of the script if some conditions are not met.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class EndIfDeclaration implements ILanguageComponent {

	public static final String DECLARATION;

	static {

		DECLARATION = new EndIfDeclaration().getScriptDeclaration();
	}

	@Override
	public String getName() {

		return "endif";
	}

	@Override
	public Optional<ComponentArguments> getArguments() {

		return Optional.of(ComponentArguments.empty().setImmutable());
	}

	@Override
	public String getSyntax() {

		return "endif";
	}

	@Override
	public boolean canApply(final ComponentArguments arguments) {

		return arguments.equals(ComponentArguments.empty().setImmutable());
	}

	@Override
	public boolean isLocationValid(final Location location) {

		return location.isPresent(new IfDeclaration());
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

		final ComponentArguments args = ComponentArguments.of();
		args.addArgument("shallIgnore", "false");
		args.setImmutable();

		return Optional.of(args);
	}

	@Override
	public String getErrorMessage() {

		return "If you see this, then you are stupid! Check the syntax!";
	}

	@Override
	public String getInvalidLocationMessage() {

		return "End if must be specified after an ifdef definition!";
	}
}
