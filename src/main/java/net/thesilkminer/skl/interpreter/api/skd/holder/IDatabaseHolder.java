package net.thesilkminer.skl.interpreter.api.skd.holder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Optional;

import javax.annotation.Nonnull;

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
	@Nonnull
	Optional<String> name();

	/**
	 * Gets if the current database holder can be directly
	 * accepted by a
	 * {@link net.thesilkminer.skl.interpreter.api.skd.parser.ISkdParser parser}
	 * or if its initialization must be forced.
	 *
	 * @return
	 *      If it can be accepted by default
	 *
	 * @since 0.2.1
	 */
	default boolean canBeAcceptedByDefault() {
		return false;
	}

	/**
	 * Obtains a {@link BufferedReader buffered reader} that can
	 * be used to read this holder's contents.
	 *
	 * <p>Usually APIs should not enforce specific types
	 * for implementation: in this case it is enforced because
	 * parsing is done line by line.</p>
	 *
	 * @return
	 *      A valid and {@link Nonnull not null} buffered reader.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	BufferedReader readerStream();

	/**
	 * Gets a {@link BufferedWriter} used to write on this database
	 * holder.
	 *
	 * <p>Implementation must return a valid buffered writer for
	 * this specific database holder, if {@link #writable()} returns
	 * {@code true}.</p>
	 *
	 * <p>If this database holder is not writable, implementations
	 * must return {@link Optional#empty()}.</p>
	 *
	 * @return
	 *      An {@link Optional} containing a valid buffered writer,
	 *      or empty if not applicable.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	default Optional<BufferedWriter> writerStream() {
		return Optional.empty();
	}
}
