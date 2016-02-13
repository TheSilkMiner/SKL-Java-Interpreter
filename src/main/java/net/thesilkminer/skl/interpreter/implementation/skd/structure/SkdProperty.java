package net.thesilkminer.skl.interpreter.skd.structure;

/**
 * Created by TheSilkMiner on 09/10/2015.
 * Package: net.thesilkminer.skl.interpreter.skd.structure.
 * Project: Java Interpreter.
 */

import com.google.common.base.Preconditions;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class represents a set of properties that are
 * found in a SKD file's tag.
 */
public class SkdProperty {

	private String name;
	private Optional<String> value;

	private SkdProperty(@Nonnull String name,@Nonnull Optional<String> value) {

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
	public static SkdProperty getProperty(@Nonnull String name,
										  @Nonnull
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
	public static SkdProperty getProperty(@Nonnull String name,
										  @Nullable
										  String value) {

		Optional<String> opt = Optional.empty();

		if (value != null) {

			opt = Optional.of(value);
		}

		return SkdProperty.getProperty(name, opt);
	}

	/**
	 * Gets the name of the property.
	 *
	 * @return
	 * 		The property's name
	 */
	public String getName() {

		return this.name;
	}

	/**
	 * Gets the property value. It may be {@link Optional#empty()}
	 *
	 * @return
	 * 		The property's value
	 */
	public Optional<String> getValue() {

		return this.value;
	}

	/**
	 * Sets the value of the property.
	 *
	 * @param value
	 * 		The new value
	 */
	public void setValue(String value) {

		this.value = Optional.of(value);
	}

	/**
	 * Clears the value of the property.
	 */
	public void removeValue() {

		this.value = Optional.empty();
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {

			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {

			return false;

		}
		SkdProperty that = (SkdProperty) obj;
		return Objects.equals(this.getName(), that.getName())
				&& Objects.equals(this.getValue(), that.getValue());
	}

	@Override
	public int hashCode() {

		return Objects.hash(this.getName(), this.getValue());
	}

	@Override
	public String toString() {

		String returnable = "SkdProperty{";

		returnable += this.getName();
		returnable += "=";
		returnable += "\"" + this.getValue() + "\"";

		returnable += "}";

		return returnable;
	}

	public String toString(boolean toConcat) {

		if (!toConcat) {

			return this.toString();
		}

		String str = "";

		str += this.getName();
		str += "=\"";
		str += this.getValue().isPresent() ? this.getValue().get() : "";
		str += "\"";

		return str;
	}
}
