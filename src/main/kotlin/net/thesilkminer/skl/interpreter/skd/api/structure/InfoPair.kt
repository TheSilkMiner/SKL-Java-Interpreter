package net.thesilkminer.skl.interpreter.skd.api.structure

import java.util.Optional

/**
 * An information pair carries some information in the form
 * of a key-value pair.
 *
 * They are a sort of middle ground between [entities][Entity]
 * and [properties][Property] because they do share common
 * traits of both but are the same time completely different
 * types.
 *
 * An information pair is used in a database to store
 * information regarding an entity that is identified through
 * a key of type [String] and a value of type `T`, variable
 * according to the type given in the database.
 *
 * Information pairs are uniquely identifiable through an ID
 * assigned to them by the user. The ID need not be unique
 * inside the database: it only needs to be unique in the scope
 * of the entity the information pair is a child of.
 *
 * Entities are allowed to have multiple information pairs with
 * the same name, which can be used to represent multiple
 * related information as an example.
 *
 * Information pairs are entities in the sense that they are
 * allowed to be children of other entities (but not array-like
 * entities) and be identified through IDs. Differently from
 * entities, though, they do not allow properties to be defined
 * inside them, not even automatically generated ones, and they
 * cannot have children in the form of entities or information
 * pairs.
 *
 * The similar trait they share with properties is the fact that
 * they are a pair of key and value.
 *
 * As an example, the following is a declaration of an
 * information pair according to the XML-like syntax of an
 * SKD database. Note the void tag.
 *
 * ```xml
 * <key value="value" />
 * ```
 *
 * It is suggested that the end user prefers information pairs
 * instead of properties due to the wider support the language
 * offers. Properties should be in fact considered legacy where
 * possible. This does not imply that properties are deprecated,
 * though. It is entirely possible for an entity to have both
 * information pairs and properties, but in the SKD the former
 * are more encouraged as a matter of style.
 *
 * @param[T] The type of the information pair value.
 *
 * @since 0.3
 */
interface InfoPair<T> {

    /**
     * Gets the key of this information pair.
     *
     * @return The key of this information pair.
     *
     * @since 0.3
     */
    fun getKey(): String

    /**
     * Gets the value of this information pair.
     *
     * @return The value of this information pair.
     *
     * @since 0.3
     */
    fun getValue(): T

    /**
     * Sets the value of this information pair.
     *
     * @param[value] The new value of this information pair.
     *
     * @since 0.3
     */
    fun setValue(value: T)

    /**
     * Gets the value of this information pair converted
     * to a [String].
     *
     * Implementations are free to choose the best way to represent
     * data and to encode the type of the data itself.
     *
     * @return The value of this information pair converted
     * to a [String].
     *
     * @since 0.3
     */
    fun getValueAsString() : String

    /**
     * Sets the value of this information pair from the
     * given [String].
     *
     * Implementations are allowed to throw an exception
     * (namely an [IllegalArgumentException]) if they cannot
     * convert the string to the type of this information pair.
     * Loose conversion is allowed and implementation-dependant
     * (much like in Javascript, for example).
     *
     * @param[value] The value to set this information pair to
     * in a `String`-ified form.
     * @exception IllegalArgumentException If the `value` cannot
     * be converted from a `String` to the type of this information
     * pair.
     *
     * @since 0.3
     */
    fun setValueFromString(value: String)

    /**
     * Gets the unique ID assigned to this information pair.
     *
     * If this information pair has no unique ID associated,
     * then the method returns an [empty optional][Optional.empty].
     *
     * This method does not allow the user to get the value
     * of an automatically generated ID, if present. That value
     * is in fact internal and as such cannot be accessed by
     * outside consumers.
     *
     * @return An [Optional] wrapping either the id or nothing,
     * according to the results.
     *
     * @since 0.3
     */
    fun getId() : Optional<String>

    /**
     * Sets the unique ID of this information pair.
     *
     * If this information pair has an automatic ID associated
     * to it, this method will not modify it, but will add a manual
     * ID alongside it. This happens because the automatically
     * generated ID is internal and as such is transparent
     * to consumers.
     *
     * Explicitly passing an empty string to this method
     * will remove the ID from this information pair.
     *
     * @param[id] An string representing the new ID, or an empty
     * string to remove it.
     *
     * @since 0.3
     */
    fun setId(id: String)
}