package net.thesilkminer.skl.interpreter.api.sks.language;

import net.thesilkminer.skl.interpreter.api.sks.SksApi;
import net.thesilkminer.skl.interpreter.api.sks.language.components.ILanguageComponent;

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

	private final List<ILanguageComponent> args;
	private final boolean hadScriptLines;

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

	/**
	 * Generates a new location instance with the
	 * specified previous language components.
	 *
	 * @param scriptLinesBefore
	 * 		Holds if there were some script lines before this location.
	 * @param prev
	 * 		The previous arguments encountered.
	 * @return
	 * 		A new location instance.
	 *
	 * @since 0.2
	 */
	@SuppressWarnings("WeakerAccess") //API Method
	public static Location from(final boolean scriptLinesBefore,
								 @Nullable
								 final ILanguageComponent... prev) {

		if (prev != null) {

			return new Location(scriptLinesBefore, prev);
		}

		return new Location(scriptLinesBefore);
	}

	/**
	 * Generates a new location instance with the
	 * specified list of previous language components.
	 *
	 * @param scriptLinesBefore
	 * 		Holds if there were some script lines before this location.
	 * @param prev
	 * 		The list of previous arguments encountered.
	 * @return
	 * 		A new location instance.
	 *
	 * @since 0.2
	 */
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

	/**
	 * Gets the previous arguments as a list.
	 *
	 * @return
	 * 		The previous arguments as a list.
	 *
	 * @since 0.2
	 */
	@SuppressWarnings("WeakerAccess") //API Method
	public List<ILanguageComponent> getPreviousArguments() {

		return args;
	}

	/**
	 * Gets if there were some script lines before this location.
	 *
	 * @return
	 * 		If there were some script lines before this location.
	 *
	 * @since 0.2
	 */
	@SuppressWarnings("unused")
	public boolean wereThereScriptLinesBefore() {

		return hadScriptLines;
	}

	/**
	 * Returns if the specified language component is present in the
	 * list of previous language components.
	 *
	 * @param languageComponent
	 * 		The language component to check.
	 * @return
	 * 		If it was present.
	 *
	 * @since 0.2
	 */
	public boolean isPresent(final ILanguageComponent languageComponent) {

		return this.getPreviousArguments().contains(languageComponent);
	}

	/**
	 * Returns if the specified language component is not present in the
	 * list of previous language components.
	 *
	 * @param component
	 * 		The language component to check.
	 * @return
	 * 		If it wasn't present.
	 *
	 * @since 0.2
	 */
	@SuppressWarnings("WeakerAccess") //API Method
	public boolean isNotPresent(final ILanguageComponent component) {

		return !this.isPresent(component);
	}

	/**
	 * Gets if this is the first argument of a collection.
	 *
	 * @return
	 * 		If this is the first argument of a collection.
	 *
	 * @since 0.2
	 */
	public boolean isFirst() {

		return this.getPreviousArguments() == null || this.getPreviousArguments().isEmpty();
	}

	/**
	 * Gets if the location is before the declaration of a script.
	 *
	 * <p>It is a simple convenience method, to avoid having to
	 * check {@link #wereThereScriptLinesBefore()}.</p>
	 *
	 * @return
	 * 		If the script declaration hasn't been performed yet.
	 *
	 * @since 0.2
	 */
	public boolean isBeforeScript() {

		try {

			Class<?> startDecClazz = Class.forName(
					      "net.thesilkminer.skl.interpreter.implementation."
				          + "sks.components.markers.ScriptStartDeclaration"
			);
			Class<?> endDecClazz = Class.forName(
					      "net.thesilkminer.skl.interpreter.implementation."
				          + "sks.components.markers.ScriptEndDeclaration"
			);

			Object startObj = startDecClazz.newInstance();
			Object endObj = endDecClazz.newInstance();

			ILanguageComponent start = ILanguageComponent.class.cast(startObj);
			ILanguageComponent end = ILanguageComponent.class.cast(endObj);

			return this.isNotPresent(start) && this.isNotPresent(end);
		} catch (final ReflectiveOperationException ex) {
			SksApi.get().getLogger().info("Error while calling location method");
			SksApi.get().getLogger().stacktrace(ex);
			return false;
		}
	}

	/**
	 * Gets if this is the first listener declaration.
	 *
	 * @return
	 * 		If this is the first listener declaration.
	 *
	 * @since 0.2
	 */
	public boolean noOtherListenersPresent() {

		if (System.getProperty(
				      "skl.sks.debugAllowMultipleListenerRegistration",
				      "false").equals("true")) {

			return true;
		}

		boolean flag = true;

		for (ILanguageComponent comp : this.getPreviousArguments()) {

			String compClass = comp.getClass().getName();

			if (compClass.startsWith(
					      "net.thesilkminer.skl.interpreter."
							    + "sks.components.listeners.")) {

				flag = false;
			}
		}

		return flag;
	}
}
