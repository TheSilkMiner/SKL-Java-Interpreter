package net.thesilkminer.skl.interpreter.sks;

import net.thesilkminer.skl.interpreter.sks.components.markers.ScriptEndDeclaration;
import net.thesilkminer.skl.interpreter.sks.components.markers.ScriptStartDeclaration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the location of a particular piece of code.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class Location {

	private List<ILanguageComponent> args;
	private boolean hadScriptLines;

	private Location(final boolean hadScriptLines) {

		// Not in coding style, but needed.
		this.args = new ArrayList<ILanguageComponent>() {

			@Override
			public boolean contains(final Object object) {

				if (object == null) {

					return super.contains(null);
				}

				for (ILanguageComponent component : this) {

					if (object.getClass().isAssignableFrom(
							       component.getClass())) {

						return true;
					}
				}

				return false;
			}
		};
		this.hadScriptLines = hadScriptLines;
	}

	private Location(final boolean hadScriptLines,
					    @Nonnull
					    final ILanguageComponent... previousArguments) {

		this(hadScriptLines);
		this.args.addAll(Arrays.asList(previousArguments));
	}

	public static Location from(final boolean scriptLinesBefore,
								 @Nullable
								 final ILanguageComponent... prev) {

		if (prev != null) {

			return new Location(scriptLinesBefore, prev);
		}

		return new Location(scriptLinesBefore);
	}

	public static Location from(final boolean scriptLinesBefore,
								 @Nullable
								 final List<ILanguageComponent>
										 prev) {

		if (prev != null) {

			return from(scriptLinesBefore,
					prev.toArray(new ILanguageComponent[prev.size()]));
		}

		return from(scriptLinesBefore);
	}

	public List<ILanguageComponent> getPreviousArguments() {

		return args;
	}

	public boolean wereThereScriptLinesBefore() {

		return hadScriptLines;
	}

	public boolean isPresent(final ILanguageComponent languageComponent) {

		return this.getPreviousArguments().contains(languageComponent);
	}

	public boolean isNotPresent(final ILanguageComponent component) {

		return !this.isPresent(component);
	}

	public boolean isFirst() {

		return this.getPreviousArguments() == null || this.getPreviousArguments().isEmpty();
	}

	public boolean isBeforeScript() {

		return this.isNotPresent(new ScriptStartDeclaration())
				&& this.isNotPresent(new ScriptEndDeclaration());
	}

	public boolean noOtherListenersPresent() {

		if (System.getProperty(
				      "skl.sks.debugAllowMultipleListenerRegistration",
				      "false").equals("true")) {

			return true;
		}

		boolean flag = true;

		for (ILanguageComponent comp : this.getPreviousArguments()) {

			String compClass = comp.getClass().getName();
			System.out.println(compClass);

			if (compClass.startsWith(
					      "net.thesilkminer.skl.interpreter."
							    + "sks.components.listeners.")) {

				flag = false;
			}
		}

		return flag;
	}
}
