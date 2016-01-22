package net.thesilkminer.skl.interpreter.sks.components.declaration;

import net.thesilkminer.skl.interpreter.sks.ComponentArguments;
import net.thesilkminer.skl.interpreter.sks.ILanguageComponent;
import net.thesilkminer.skl.interpreter.sks.Location;

import java.util.Optional;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

/**
 * Represents the "script" keyword of the SKS script.
 *
 * <p>This declares the script and its visibility.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class ScriptDeclaration implements ILanguageComponent {

	@SuppressWarnings("unused")
	private enum Visibility {

		PUBLIC("public"),
		PROTECTED("protected"),
		PACKET("packet"),
		PRIVATE("private"),
		SCRIPT("script");

		private String qualifier;

		Visibility(final String qualifier) {

			this.qualifier = qualifier;
		}

		public String getQualifier() {

			return this.qualifier;
		}

		public static Visibility fromString(@Nonnull final String qualifier) {

			Visibility visibility = null;

			for (Visibility value : Visibility.values()) {

				if (value.getQualifier().equals(qualifier)) {

					visibility = value;
					break;
				}
			}

			return visibility;
		}
	}

	private static final String VISIBILITY = "visibility";

	private String scriptName;
	private Visibility visibility;
	private boolean error;
	private boolean mustPublic;

	public ScriptDeclaration() {

		this.mustPublic = true;
	}

	@Override
	public String getName() {

		return "script";
	}

	@Override
	public Optional<ComponentArguments> getArguments() {

		final ComponentArguments args = ComponentArguments.of();
		args.addArgument(ComponentArguments.INIT, null);
		args.addArgument(VISIBILITY, null);

		return Optional.of(args.setImmutable());
	}

	@Override
	public String getSyntax() {

		return "script <scriptName> visibility <visibility>";
	}

	@Override
	public boolean canApply(ComponentArguments arguments) {

		return arguments.hasOnly(ComponentArguments.INIT, VISIBILITY);
	}

	@Override
	public boolean parse(ComponentArguments arguments) {

		if (!this.canApply(arguments)) {

			throw new ILanguageComponent.WrongSyntaxException(this);
		}

		this.scriptName = ComponentArguments.removeApixes(
				arguments.get(ComponentArguments.INIT));

		this.visibility = Visibility.fromString(arguments.get(VISIBILITY));

		return this.scriptName != null
				&& !this.scriptName.isEmpty()
				&& this.visibility != null;
	}

	@Override
	public boolean parseFallback(String line) {

		final int firstApix = line.indexOf('"');
		final int lastApix = line.lastIndexOf('"');

		if (firstApix == lastApix && firstApix == -1) {

			return false;
		}

		this.scriptName = ComponentArguments.removeApixes(
				line.substring(firstApix, lastApix));

		final String[] parts = line.split(Pattern.quote(" "));

		if (parts.length != 4 || !parts[2].equals(VISIBILITY)) {

			this.error = true;

			return false;
		}

		this.visibility = Visibility.fromString(parts[3]);

		return this.scriptName != null
				&& !this.scriptName.isEmpty()
				&& this.visibility != null;
	}

	private boolean isVisibilityAllowed(Visibility visibility) {

		if (this.mustPublic) {

			return visibility.equals(Visibility.PUBLIC);
		}

		return !visibility.equals(Visibility.PRIVATE)
				&& !visibility.equals(Visibility.PROTECTED);
	}

	@Override
	public boolean hasErrored() {

		return this.error || !this.isVisibilityAllowed(this.visibility);
	}

	@Override
	public Optional<ComponentArguments> getNeededEdits() {

		final ComponentArguments edits = ComponentArguments.of();
		edits.addArgument("scriptName", this.scriptName);
		//edits.addArgument("scriptVisibility, this.visibility);

		return Optional.of(edits.setImmutable());
	}

	@Override
	public String getErrorMessage() {

		return "The script declaration was not correct "
				+ (this.mustPublic ? "or the visibility was not public" : "");
	}

	@Override
	public boolean isLocationValid(final Location location) {

		return location.isBeforeScript();
	}

	@Override
	public String getInvalidLocationMessage() {

		return "Script declaration should be placed before script";
	}
}
