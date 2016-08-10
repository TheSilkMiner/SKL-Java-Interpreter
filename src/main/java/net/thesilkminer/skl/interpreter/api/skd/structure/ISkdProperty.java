package net.thesilkminer.skl.interpreter.api.skd.structure;

import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Represents a property in the SKD language specifications.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public interface ISkdProperty extends IAcceptable<ISkdProperty> {

	/**
	 * Gets the name of the property.
	 *
	 * @return
	 * 		The property's name.
	 *
	 * @since 0.2
	 */
	@Nonnull
	String getName();

	/**
	 * Gets the property value. It may be {@link Optional#empty()}
	 *
	 * @return
	 * 		The property's value.
	 *
	 * @since 0.2
	 */
	@Nonnull
	Optional<String> getValue();

	/**
	 * Sets the value of the property.
	 *
	 * <p>To remove the property value, do not pass {@link null}
	 * to this method. Use {@link #removeValue()} instead.</p>
	 *
	 * @param value
	 * 		The new value
	 *
	 * @since 0.2
	 */
	void setValue(@Nonnull final String value);

	/**
	 * Clears the value of the property.
	 *
	 * @since 0.2
	 */
	void removeValue();
}
