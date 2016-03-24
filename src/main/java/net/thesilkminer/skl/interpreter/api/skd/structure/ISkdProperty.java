package net.thesilkminer.skl.interpreter.api.skd.structure;

import java.util.Optional;

/**
 * Represents a property in the SKD language specifications.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public interface ISkdProperty {

	/**
	 * Gets the name of the property.
	 *
	 * @return
	 * 		The property's name
	 */
	String getName();

	/**
	 * Gets the property value. It may be {@link Optional#empty()}
	 *
	 * @return
	 * 		The property's value
	 */
	Optional<String> getValue();

	/**
	 * Sets the value of the property.
	 *
	 * @param value
	 * 		The new value
	 */
	void setValue(final String value);

	/**
	 * Clears the value of the property.
	 */
	void removeValue();
}
