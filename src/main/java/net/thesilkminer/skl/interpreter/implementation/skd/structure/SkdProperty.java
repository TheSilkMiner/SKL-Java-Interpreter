package net.thesilkminer.skl.interpreter.implementation.skd.structure;

import com.google.common.base.Preconditions;

import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdProperty;
import org.jetbrains.annotations.Contract;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class represents a set of properties that are
 * found in a SKD file's tag.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public class SkdProperty implements ISkdProperty {

	private final String name;
	private String value;

	private SkdProperty(@Nonnull final String name, @Nullable final String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * This method is used to obtain an instance of an SkdProperty.
	 *
	 * @param name
	 * 		The name of the property.
	 * @param value
	 * 		The value. May be empty.
	 * @return
	 * 		A new SkdProperty instance.
	 *
	 * @deprecated Use {@link #getProperty(String, String)} instead.
	 *
	 * @since 0.2
	 */
	@Contract(value = "!null, !null -> !null; _, _ -> fail", pure = true)
	@Deprecated
	@Nonnull
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static SkdProperty getProperty(@Nonnull final String name,
	                                      @Nonnull final Optional<String> value) {
		return getProperty(name, value.orElse(null));
	}

	/**
	 * This method is used to obtain an instance of an SkdProperty.
	 *
	 * @param name
	 * 		The name of the property.
	 * @param value
	 * 		The value. May be null.
	 * @return
	 * 		A new SkdProperty instance.
	 *
	 * @since 0.2
	 */
	@Contract(value = "!null, !null -> !null; !null, null -> !null; null, _ -> fail",
			  pure = true)
	@Nonnull
	public static SkdProperty getProperty(@Nonnull final String name,
	                                      @Nullable final String value) {
		return new SkdProperty(
				Preconditions.checkNotNull(name, "Property name must not be null"),
				value
		);
	}

	@Nonnull
	@Override
	public String getName() {
		return this.name;
	}

	@Nonnull
	@Override
	public Optional<String> getValue() {
		return Optional.ofNullable(this.value);
	}

	@Override
	public void setValue(@Nonnull final String value) {
		Preconditions.checkNotNull(value,
				"Value must not be null. Use #removeValue() instead");
		Preconditions.checkArgument(!value.isEmpty(),
				"Use #removeValue() instead of setting the content to empty");
		this.value = value;
	}

	@Override
	public void removeValue() {
		this.value = null;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}

		ISkdProperty that = (ISkdProperty) obj;
		return Objects.equals(this.getName(), that.getName())
				&& Objects.equals(this.getValue(), that.getValue());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getName(), this.getValue());
	}

	@Nonnull
	@Override
	public String toString() {
		String str = "";

		str += this.getName();
		str += "=\"";
		str += this.getValue().orElse("");
		str += "\"";

		return str;
	}
}
