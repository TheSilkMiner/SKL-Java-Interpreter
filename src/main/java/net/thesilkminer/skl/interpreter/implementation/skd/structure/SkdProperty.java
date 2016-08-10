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
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "WeakerAccess"})
public class SkdProperty implements ISkdProperty {

	private final String name;
	private Optional<String> value;

	private SkdProperty(@Nonnull final String name, @Nonnull final Optional<String> value) {
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
	 */
	@Contract(value = "!null, !null -> !null; _, _ -> fail", pure = true)
	@Nonnull
	public static SkdProperty getProperty(@Nonnull final String name,
										  @Nonnull
										  final
										  Optional<String>
											value) {
		Preconditions.checkNotNull(name, "Name must not be null");
		Preconditions.checkNotNull(value, "Value must not be null. "
				      + "Use Optional.empty() instead");
		return new SkdProperty(name, value);
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
	 */
	@Contract(value = "!null, !null -> !null; !null, null -> !null; null, _ -> fail",
			  pure = true)
	@Nonnull
	public static SkdProperty getProperty(@Nonnull final String name,
										  @Nullable
										  final String
											value) {
		Optional<String> opt = Optional.empty();

		if (value != null) {
			opt = Optional.of(value);
		}

		return SkdProperty.getProperty(name, opt);
	}

	@Nonnull
	@Override
	public String getName() {
		return this.name;
	}

	@Nonnull
	@Override
	public Optional<String> getValue() {
		return this.value;
	}

	@Override
	public void setValue(@Nonnull final String value) {
		this.value = Optional.of(value);
	}

	@Override
	public void removeValue() {
		this.value = Optional.empty();
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
		str += this.getValue().isPresent() ? this.getValue().get() : "";
		str += "\"";

		return str;
	}
}
