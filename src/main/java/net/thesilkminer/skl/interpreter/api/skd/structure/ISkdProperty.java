package net.thesilkminer.skl.interpreter.api.skd.structure;

import java.util.Optional;

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
	 * 		The property's name
	 */
	@SuppressWarnings("unused")
	String getName();

	/**
	 * Gets the property value. It may be {@link Optional#empty()}
	 *
	 * @return
	 * 		The property's value
	 */
	@SuppressWarnings("unused")
	Optional<String> getValue();

	/**
	 * Sets the value of the property.
	 *
	 * @param value
	 * 		The new value
	 */
	@SuppressWarnings("unused")
	void setValue(final String value);

	/**
	 * Clears the value of the property.
	 */
	@SuppressWarnings("unused")
	void removeValue();
}
