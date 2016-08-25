package net.thesilkminer.skl.interpreter.api.skd.structure;

//import javax.annotation.Nonnull;

/**
 * Marks a type as able to acceptNoImplement a type.
 *
 * <p>Implementors of this interface should provide two methods:
 * {@code static <T> boolean canAcceptNoImplement(@Nonnull final T type)} and
 * {@code static <T> ... acceptNoImplement(@Nonnull final T type} (where
 * {@code ...} is the current class type).</p>
 *
 * <p>These methods will be called at run-time through duck typing and
 * will provide various methods.</p>
 *
 * <p>Refer to the non-static methods contained in this class for
 * more information.</p>
 *
 * @param <T>
 *     The type to acceptNoImplement.
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
	 *      The type to acceptNoImplement.
	 * @return
	 *      If the type can be accepted.
	 *
	 * @since 0.2.1
	 */
	//default boolean canAcceptNoImplement(@Nonnull final T type) {
	//	return true;
	//}

	/**
	 * Called by an {@link net.thesilkminer.skl.interpreter.api.skd.parser.ISkdParser}
	 * to acceptNoImplement the specified type.
	 *
	 * <p>This method will always be called after {@link #canAcceptNoImplement(T)}
	 * and only if the previous method returned {@code true}.</p>
	 *
	 * @param type
	 *      The new tag to acceptNoImplement.
	 *
	 * @since 0.2.1
	 */
	//default void acceptNoImplement(@Nonnull final T type) {}
}
