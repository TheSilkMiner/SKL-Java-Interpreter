package net.thesilkminer.skl.interpreter.api.skd.structure;

import javax.annotation.Nonnull;

/**
 * Marks a type as able to accept a type.
 *
 * @param <T>
 *     The type to accept.
 *
 * @author TheSilkMiner
 *
 * @since 0.2.1
 */
interface IAcceptable<T> {

	/**
	 * Used by an {@link net.thesilkminer.skl.interpreter.api.skd.parser.ISkdParser}
	 * to identify if the specified type can hold the type.
	 *
	 * <p>By default, this method always returns {@code true}.</p>
	 *
	 * @param type
	 *      The type to accept.
	 * @return
	 *      If the type can be accepted.
	 *
	 * @since 0.2.1
	 */
	default boolean canAccept(@Nonnull final T type) {
		return true;
	}

	/**
	 * Called by an {@link net.thesilkminer.skl.interpreter.api.skd.parser.ISkdParser}
	 * to accept the specified type.
	 *
	 * <p>This method will always be called after {@link #canAccept(T)}
	 * and only if the previous method returned {@code true}.</p>
	 *
	 * @param type
	 *      The new tag to accept.
	 *
	 * @since 0.2.1
	 */
	default void accept(@Nonnull final T type) {}
}
