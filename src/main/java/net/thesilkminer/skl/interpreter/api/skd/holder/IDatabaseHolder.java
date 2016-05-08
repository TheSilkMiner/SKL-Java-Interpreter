package net.thesilkminer.skl.interpreter.api.skd.holder;

import java.util.Optional;

/**
 * Marks the specified object as an object capable of
 * holding a database.
 *
 * @since 0.2
 */
public interface IDatabaseHolder {

	/**
	 * Gets if the current database holder supports writing
	 * operations, along with reading ones.
	 *
	 * @return
	 * 		If the holder supports writing operations.
	 *
	 * @since 0.2
	 */
	boolean writable();

	/**
	 * Gets the name of the database.
	 *
	 * <p>If no name can be specified, due to the current
	 * holder not supporting names or any other reason, then
	 * this method must return {@link Optional#empty()}.</p>
	 *
	 * @return
	 * 		The name of the database.
	 */
	Optional<String> name();
}
