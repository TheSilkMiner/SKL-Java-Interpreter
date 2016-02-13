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
// TODO Javadoc
public final class ComponentArguments {

	public static final String INIT = "<init>";
	private static final String VARARG = "@VARARG@ ";
	private static final ComponentArguments empty = ComponentArguments.of().setImmutable();

	private Map<String, String> arguments = Maps.newLinkedHashMap();
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

	public static ComponentArguments of(final ComponentArguments... otherArguments) {

		return new ComponentArguments(otherArguments);
	}

	public static ComponentArguments of(final String key, final String value) {

		return new ComponentArguments().addArgument(key, value);
	}

	public static ComponentArguments of(final Map<String, String> arguments) {

		ComponentArguments componentArguments = new ComponentArguments();
		componentArguments.getArguments().putAll(arguments);

		return componentArguments;
	}

	public static ComponentArguments empty() {

		return empty;
	}

	public static String removeApixes(String string) {

		if (string.startsWith("\"")) {

			string = string.substring(1);
		}

		if (string.endsWith("\"")) {

			string = string.substring(0, string.length() - 1);
		}

		return string;
	}

	public static String asVararg(final String key) {

		return key.startsWith(VARARG) ? key : VARARG + key;
	}

	public ComponentArguments setImmutable() {

		this.immutable = true;
		return this;
	}

	public ComponentArguments addArgument(final String key, final String value) {

		return this.addArgument(key, value, false);
	}

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

	public Map<String, String> getArguments() {

		return this.arguments;
	}

	public void invalidate() {

		this.isInvalid = true;
	}

	public boolean has(String key) {

		return this.getArguments().containsKey(key);
	}

	public boolean hasOnly(String... keys) {

		if (!(this.getArguments().size() == keys.length)) {

			return false;
		}

		for (String key : keys) {

			if (!this.has(key)) {

				return false;
			}
		}

		return true;
	}

	public String get(final String key) {

		return this.getArguments().get(key);
	}

	public ComponentArguments pairOrAdd(final String key,
										final String val) {

		return this.pairOrAdd(key, val, false);
	}

	public ComponentArguments pairOrAdd(final String key,
										final String val,
										final boolean va) {

		try {

			this.pairValue(key, val, va);
		} catch (RuntimeException e) {

			this.addArgument(key, val, va);
		}
		return this;
	}

	public ComponentArguments pairValue(final String key,
										final String val) {

		return this.pairValue(key, val, false);
	}

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

	public ComponentArguments addAll(final Map<String, String> map) {

		this.getArguments().putAll(map);

		return this;
	}

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
