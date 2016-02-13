package net.thesilkminer.skl.interpreter.implementation.sks.components.listeners;

import net.thesilkminer.skl.interpreter.api.sks.language.ComponentArguments;
import net.thesilkminer.skl.interpreter.api.sks.language.Location;
import net.thesilkminer.skl.interpreter.api.sks.language.components.ILanguageComponent;

import java.util.Optional;

/**
 * Represents the "listener" keyword of the script.
 *
 * <p>Used to declare the listener we need to pass the script to.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class ListenerDeclaration implements ILanguageComponent {

	private String listener;

	@Override
	public String getName() {

		return "listener";
	}

	@Override
	public Optional<ComponentArguments> getArguments() {

		final ComponentArguments args = ComponentArguments.of();
		args.addArgument(ComponentArguments.INIT, null);

		return Optional.of(args.setImmutable());
	}

	@Override
	public String getSyntax() {

		return "listener <listener>";
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

		this.listener = ComponentArguments.removeApixes(
				arguments.get(ComponentArguments.INIT));

		return this.listener != null && !this.listener.isEmpty();
	}

	@Override
	public boolean parseFallback(final String line) {

		final int firstApix = line.indexOf('"');
		final int lastApix = line.lastIndexOf('"');

		if (firstApix == lastApix && firstApix == -1) {

			return false;
		}

		this.listener = ComponentArguments.removeApixes(
				line.substring(firstApix, lastApix));

		return this.listener != null && !this.listener.isEmpty();
	}

	@Override
	public boolean hasErrored() {

		return false;
	}

	@Override
	public Optional<ComponentArguments> getNeededEdits() {

		final ComponentArguments edits = ComponentArguments.of();
		edits.addArgument("listenerClass", this.listener);

		return Optional.of(edits.setImmutable());
	}

	@Override
	public String getErrorMessage() {

		return "Listener not specified correctly.";
	}

	@Override
	public boolean isLocationValid(final Location location) {

		return location.isBeforeScript() && location.noOtherListenersPresent();
	}

	@Override
	public String getInvalidLocationMessage() {

		return "Listener invocation should be before script body.";
	}
}
