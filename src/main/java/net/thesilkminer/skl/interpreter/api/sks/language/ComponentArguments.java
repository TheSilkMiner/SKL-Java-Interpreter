package net.thesilkminer.skl.interpreter.api.sks.language;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;

/**
 * Represents the arguments sent to every language component.
 *
 * <p>It serves as a wrapper which allows better exchange of arguments.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
@SuppressWarnings("unused")
public final class ComponentArguments {

	/**
	 * Represents the key used to identify the main argument.
	 *
	 * @since 0.2
	 */
	public static final String INIT = "<init>";
	private static final String VARARG = "@VARARG@ ";
	private static final ComponentArguments empty = ComponentArguments.of().setImmutable();

	private final Map<String, String> arguments = Maps.newLinkedHashMap();
	private boolean isInvalid;
	private boolean immutable;

	private ComponentArguments(final ComponentArguments... otherArguments) {

		if (otherArguments.length == 0) {

			return;
		}

		for (ComponentArguments argument : otherArguments) {

			this.arguments.putAll(argument.getArguments());
		}
	}

	/**
	 * Gets a new instance with the previous arguments copied.
	 *
	 * @param otherArguments
	 * 		The arguments to copy.
	 * @return
	 * 		A new instance.
	 *
	 * @since 0.2
	 */
	public static ComponentArguments of(final ComponentArguments... otherArguments) {

		return new ComponentArguments(otherArguments);
	}

	/**
	 * Gets a new instance with the specified pair as an argument.
	 *
	 * @param key
	 * 		The key.
	 * @param value
	 * 		The value.
	 * @return
	 * 		A new instance with the argument added.
	 *
	 * @since 0.2
	 */
	public static ComponentArguments of(final String key, final String value) {

		return new ComponentArguments().addArgument(key, value);
	}

	/**
	 * Gets a new instance with the specified map of arguments added.
	 *
	 * @param arguments
	 * 		The arguments to add.
	 * @return
	 * 		A new instance.
	 *
	 * @since 0.2
	 */
	public static ComponentArguments of(final Map<String, String> arguments) {

		ComponentArguments componentArguments = new ComponentArguments();
		componentArguments.getArguments().putAll(arguments);

		return componentArguments;
	}

	/**
	 * Gets an immutable empty argument set.
	 *
	 * @return
	 * 		An immutable empty argument set.
	 *
	 * @since 0.2
	 */
	public static ComponentArguments empty() {

		return empty;
	}

	/**
	 * Removes the quotes from a specified string.
	 *
	 * <p>This method removes <b>ONLY</b> leading and
	 * trailing qutoes.</p>
	 *
	 * @param string
	 *		The string with the quotes that need to be removed.
	 * @return
	 * 		The "un-quoted" string.
	 *
	 * @since 0.2
	 */
	public static String removeApixes(String string) {

		if (string.startsWith("\"")) {

			string = string.substring(1);
		}

		if (string.endsWith("\"")) {

			string = string.substring(0, string.length() - 1);
		}

		return string;
	}

	/**
	 * Returns the specified key as a vararg-marked key.
	 *
	 * @param key
	 * 		The key.
	 * @return
	 * 		The key marked as a vararg-key.
	 *
	 * @since 0.2
	 */
	public static String asVararg(final String key) {

		return key.startsWith(VARARG) ? key : VARARG + key;
	}

	/**
	 * Sets this instance as immutable.
	 *
	 * @return
	 * 		{@code this} for convenience.
	 *
	 * @since 0.2
	 */
	public ComponentArguments setImmutable() {

		this.immutable = true;
		return this;
	}

	/**
	 * Adds an argument to this component.
	 *
	 * @param key
	 * 		The argument's key.
	 * @param value
	 * 		The argument's value.
	 * @return
	 * 		{@code this} for convenience.
	 *
	 * @since 0.2
	 */
	public ComponentArguments addArgument(final String key, final String value) {

		return this.addArgument(key, value, false);
	}

	/**
	 * Adds an argument to this component.
	 *
	 * @param key
	 * 		The argument's key.
	 * @param value
	 * 		The argument's value.
	 * @param varArg
	 * 		If this is a vararg argument.
	 * @return
	 * 		{@code this} for convenience.
	 *
	 * @since 0.2
	 */
	public ComponentArguments addArgument(final String key,
									final String value,
									final boolean varArg) {

		final String newKey = varArg ? VARARG + key : key;

		Preconditions.checkArgument(!this.getArguments().containsKey(newKey),
									 "Key is already in use.");
		Preconditions.checkState(!this.isInvalid,
									 "The state is invalid");
		Preconditions.checkState(!this.immutable, "The component is immutable");

		this.arguments.put(newKey, value);

		return this;
	}

	/**
	 * Gets the arguments.
	 *
	 * @return
	 * 		The arguments.
	 *
	 * @since 0.2
	 */
	public Map<String, String> getArguments() {

		return this.arguments;
	}

	/**
	 * Invalidates the current instance.
	 *
	 * <p>This has no practical use, yet.</p>
	 *
	 * @since 0.2
	 */
	public void invalidate() {

		this.isInvalid = true;
	}

	/**
	 * Returns if this argument list has the specified argument.
	 *
	 * @param key
	 * 		The argument's key.
	 * @return
	 * 		If the argument is available.
	 *
	 * @since 0.2
	 */
	@SuppressWarnings("WeakerAccess") //API Method
	public boolean has(final String key) {

		return this.getArguments().containsKey(key);
	}

	/**
	 * Returns if this argument has only the specified arguments.
	 *
	 * @param keys
	 * 		The keys of the arguments.
	 * @return
	 * 		If this instance has only the specified arguments.
	 *
	 * @since 0.2
	 */
	public boolean hasOnly(final String... keys) {

		// Performance edit
		if (!(this.getArguments().size() == keys.length)) {

			return false;
		}

		for (final String key : keys) {

			if (!this.has(key)) {

				return false;
			}
		}

		return true;
	}

	/**
	 * Gets the value of the specified key.
	 *
	 * @param key
	 * 		The key.
	 * @return
	 * 		The corresponding value.
	 *
	 * @since 0.2
	 */
	public String get(final String key) {

		return this.getArguments().get(key);
	}

	/**
	 * Pairs or adds a specific pair of key and value.
	 *
	 * @param key
	 * 		The key.
	 * @param val
	 * 		The value.
	 * @return
	 * 		{@code this} for convenience.
	 *
	 * @since 0.2
	 */
	public ComponentArguments pairOrAdd(final String key,
										final String val) {

		return this.pairOrAdd(key, val, false);
	}

	/**
	 * Pairs or adds a specific pair of key and value.
	 *
	 * @param key
	 * 		The key.
	 * @param val
	 * 		The value.
	 * @param va
	 * 		If the specified argument is a vararg.
	 * @return
	 * 		{@code this} for convenience.
	 *
	 * @since 0.2
	 */
	@SuppressWarnings({"WeakerAccess", "SameParameterValue"}) // API Method
	public ComponentArguments pairOrAdd(final String key,
										final String val,
										final boolean va) {

		try {

			this.pairValue(key, val, va);
		} catch (final RuntimeException e) {

			this.addArgument(key, val, va);
		}
		return this;
	}

	/**
	 * Pairs the specified pair of key and value.
	 *
	 * @param key
	 * 		The key.
	 * @param val
	 * 		The value.
	 * @return
	 * 		{@code this} for convenience
	 * @throws RuntimeException
	 * 		If the key is not present in the list of arguments.
	 *
	 * @since 0.2
	 */
	@SuppressWarnings("UnusedReturnValue")
	public ComponentArguments pairValue(final String key,
										final String val) {

		return this.pairValue(key, val, false);
	}

	/**
	 * Pairs the specified pair of key and value.
	 *
	 * @param key
	 * 		The key.
	 * @param value
	 * 		The value.
	 * @param var
	 * 		If the argument is a vararg-argument.
	 * @return
	 * 		{@code this} for convenience
	 * @throws RuntimeException
	 * 		If the key is not present in the list of arguments.
	 *
	 * @since 0.2
	 */
	@SuppressWarnings({"UnusedReturnValue", "SameParameterValue", "WeakerAccess"})
	//API Method
	public ComponentArguments pairValue(final String key,
										final String value,
										final boolean var) {

		final String newKey = var ? VARARG + key : key;

		if (!this.getArguments().containsKey(newKey)) {

			throw new RuntimeException(String.format(
					"Value %s was not present", newKey));
		}

		Preconditions.checkState(!this.immutable, "The component is immutable");

		this.getArguments().remove(key);
		this.getArguments().put(key, value);

		return this;
	}

	/**
	 * Adds all the arguments contained in the map.
	 *
	 * @param map
	 * 		The arguments list.
	 * @return
	 * 		{@code this} for convenience.
	 *
	 * @since 0.2
	 */
	public ComponentArguments addAll(final Map<String, String> map) {

		this.getArguments().putAll(map);

		return this;
	}

	/**
	 * Gets if the specified key represents a vararg argument.
	 *
	 * @param key
	 * 		The key.
	 * @return
	 * 		If it represents a vararg argument.
	 *
	 * @since 0.2
	 */
	public boolean isVarArg(final String key) {

		return this.getArguments().containsKey(VARARG + key);
	}

	@Override
	public String toString() {

		return this.arguments.toString();
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null || this.getClass() != obj.getClass()) {

			return false;
		}

		ComponentArguments that = (ComponentArguments) obj;

		return Objects.equals(this.getArguments(), that.getArguments());
	}

	@Override
	public int hashCode() {

		return Objects.hash(this.getArguments());
	}
}
