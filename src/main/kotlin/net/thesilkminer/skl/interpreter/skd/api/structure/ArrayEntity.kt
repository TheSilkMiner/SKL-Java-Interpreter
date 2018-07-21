package net.thesilkminer.skl.interpreter.skd.api.structure

import org.joou.ULong
import java.util.Optional

/**
 * Represents an array-like entity inside a database.
 *
 * Array-like entities are a special kind of entity
 * that share most similarities with "normal" entities
 * but place some restrictions on the children of the
 * entity itself.
 *
 * Array-like entities cannot have information pairs as
 * direct children and all children entities need to have
 * the same name, the same properties and the same
 * first-level children. If all these properties are
 * satisfied, the entity may be considered an array-like
 * entity.
 *
 * Much like "normal" entities, array-like entities can
 * have no, one or multiple children entities inside them.
 * Each children may be either an entity or an array-like
 * entity without any issues. This because every child
 * entity is separate from the parent. Note that if a
 * child entity is an array-like entity, then all the
 * other children need to be array-like entities. If this
 * is not the case, the contract is violated an exception
 * may be raised at parsing and/or serializing time.
 *
 * Array-like entities can also have one, no or multiple
 * properties. Every property is a combination of key and
 * value pairs that directly specifies entity data. Properties
 * reside directly in the declaration of the array-like entity
 * and can either be generated automatically (like with IDs or
 * array markers) or added manually. Every property must be
 * unique inside the entity it is referring to. See [Property]
 * for more information.
 *
 * Array-like entities can be uniquely identified by an ID
 * defined by the user of the array-like entity itself. Every
 * ID must be unique inside the database. An exception are the
 * IDs that are automatically generated, such as the ones for
 * entities inside this entity.
 *
 * Array-like entities, differently from entities, share a
 * size and a set of operators that allow them to be used as
 * arrays (as in, the language constructs of the Kotlin
 * programming language). More specifically, their elements
 * can either be indexed via integer indexes or [ULong] indexes.
 * This allows array-like entities to be iterated over
 * (see [Iterable]) and to access children directly through
 * [get] and [set] (when available).
 *
 * A major difference between array-like entities and array
 * constructs in a programming language such as Kotlin is that
 * array-like entities do not have a fixed size. Their size can
 * be mutated thanks to the [addChildEntity] method, increasing
 * it by one every time, and the [removeChildEntity] method,
 * decreasing it by one every time. Using the array access syntax,
 * though (hence the [get] and [set] methods) does not mutate
 * the array in any way: for this reason, it is illegal to attempt
 * to access a position outside the current bounds of the
 * array-like entity using these operators. Some implementations
 * may allow an off-by-one error using the [set] method to allow
 * increasing the value by one, but this is not guaranteed and
 * must be considered an illegal operation anyway.
 *
 * As an example, the following is a declaration of an
 * array-like entity according to the XML-like syntax
 * of an SKD database.
 *
 * ```xml
 * <array-like this="is" a="property" skd$array="">
 *     <item skd$id="0" identifier="true">
 *         <key value="value0" />
 *     </item>
 *     <item skd$id="1" identifier="true">
 *         <key value="value1" />
 *     </item>
 *     <item skd$id="2" identifier="true">
 *         <key value="value2" />
 *     </item>
 *     <item skd$id="3" identifier="true">
 *         <key value="value3" />
 *     </item>
 * </array-like>
 * ```
 *
 * For more information regarding entities directly, refer to
 * [Entity].
 *
 * @since 0.3
 */
interface ArrayEntity : Entity, Iterable<ArrayEntity> {

    /**
     * Gets this array-like entity as instance of a "normal"
     * entity.
     *
     * This does not automatically convert this array-like
     * entity in a "normal" entity. To do so, please call
     * [setArray] first.
     *
     * @return This array-like entity as a "normal" entity.
     *
     * @since 0.3
     */
    fun asEntity() : Entity

    /**
     * Gets the child entity at the `index` specified, if possible.
     *
     * This method can also be called using the array-like syntax
     * with the index operator:
     * ```kotlin
     * element = entity[index]
     * ```
     *
     * @param[index] An [ULong] specifying the index of the entity
     * to get.
     * @return The entity at the specified index, if available.
     * @exception IndexOutOfBoundsException If the index given is
     * outside the bounds of the current array-like entity.
     *
     * @since 0.3
     */
    operator fun get(index: ULong) : Entity

    /**
     * Sets the child entity at the `index` specified with the
     * entity given through `value`, if possible.
     *
     * This method can also be called using the array-like syntax
     * with the index operator:
     * ```kotlin
     * entity[index] = value
     * ```
     *
     * Attempting to set a value that is outside the current bounds
     * of this array-like entity throws an exception. To expand the
     * array bounds, use [addChildEntity] instead. Some implementations
     * may allow off-by-one errors performing the same operations as in
     * [addChildEntity], but this contract is not guaranteed nor
     * enforced.
     *
     * @param[index] An [ULong] specifying the index of the entity
     * to set.
     * @param[value] The entity to set at the specified `index`.
     * @exception IndexOutOfBoundsException If the index given is
     * outside the current bounds of the array-like entity. To enlarge
     * the size of the entity, please consider using [addChildEntity]
     * instead.
     *
     * @since 0.3
     */
    operator fun set(index: ULong, value: Entity)

    /**
     * Gets the child entity at the `index` specified, if possible.
     *
     * This method can also be called using the array-like syntax
     * with the index operator:
     * ```kotlin
     * element = entity[index]
     * ```
     *
     * @param[index] An [Int] specifying the index of the entity
     * to get.
     * @return The entity at the specified index, if available.
     * @exception IndexOutOfBoundsException If the index given is
     * outside the bounds of the current array-like entity.
     *
     * @since 0.3
     */
    operator fun get(index: Int) : Entity

    /**
     * Sets the child entity at the `index` specified with the
     * entity given through `value`, if possible.
     *
     * This method can also be called using the array-like syntax
     * with the index operator:
     * ```kotlin
     * entity[index] = value
     * ```
     *
     * Attempting to set a value that is outside the current bounds
     * of this array-like entity throws an exception. To expand the
     * array bounds, use [addChildEntity] instead. Some implementations
     * may allow off-by-one errors performing the same operations as in
     * [addChildEntity], but this contract is not guaranteed nor
     * enforced.
     *
     * @param[index] An [Int] specifying the index of the entity
     * to set.
     * @param[value] The entity to set at the specified `index`.
     * @exception IndexOutOfBoundsException If the index given is
     * outside the current bounds of the array-like entity. To enlarge
     * the size of the entity, please consider using [addChildEntity]
     * instead.
     *
     * @since 0.3
     */
    operator fun set(index: Int, value: Entity)

    /**
     * Gets the size of the current array-like entity.
     *
     * @return An [ULong] representing the current size of this
     * array-like entity.
     *
     * @since 0.3
     */
    fun size() : ULong

    /**
     * Gets the size of this array-like entity.
     *
     * @return An [Int] representing the current size of this
     * array-like entity.
     *
     * @since 0.3
     */
    fun intSize() : Int

    /**
     * Clears the entirety of this array-like entity, removing
     * all the children entities currently present.
     *
     * As a side effect, this array-like entity size will result
     * equal to `0`.
     *
     * @since 0.3
     */
    fun clear()

    /**
     * Adds an entity as a child of this entity.
     *
     * The entity must respect the constraints of this array-like
     * entity. More specifically, the name must match with the one
     * of all the other children entities, the properties must
     * all be present and have the same key and the first-level
     * children must all be the same. Refer directly to the
     * [ArrayEntity] documentation for more clarification.
     *
     * This array-like entity size is also augmented by one to
     * accommodate the newly added children.
     *
     * @param[entity] The entity to add
     * @exception IllegalArgumentException If the entity violates
     * one or more of the above constraints.
     *
     * @since 0.3
     */
    override fun addChildEntity(entity: Entity)

    /**
     * Removes the given entity from the children of this entity.
     *
     * The entity is removed according to an `equals` check, so
     * only if the two entities match completely in all their
     * elements.
     *
     * If no matching entities are found, then the array-like
     * entity is left as-is. Implementations may log an error
     * message if they deem necessary to do so.
     *
     * If the removal is successful, all the other children
     * entities that follow the one that was removed are shifted
     * by one to avoid the presence of ghost entities. As an
     * example, consider the following list of entities:
     * `A B C D E F G`. Removing the entity identified in this
     * example by the letter `D` should leave a blank spot as
     * follows: `A B C   E F G`. To avoid that blank spot, all
     * the other entities are shifted by one: `A B C E F G`.
     * This also has the side effect of reducing this array-like
     * entity size by one.
     *
     * @param[entity] The entity to remove.
     *
     * @since 0.3
     */
    override fun removeChildEntity(entity: Entity)

    /**
     * Removes the child entity identified by the given ID from
     * the children of this entity.
     *
     * If no entities with the given ID are found, then the
     * entity is left as-is. Implementations may log an error
     * message if they deem necessary to do so.
     *
     * If the removal is successful, all the other children
     * entities that follow the one that was removed are shifted
     * by one to avoid the presence of ghost entities. As an
     * example, consider the following list of entities:
     * `A B C D E F G`. Removing the entity identified in this
     * example by the letter `D` should leave a blank spot as
     * follows: `A B C   E F G`. To avoid that blank spot, all
     * the other entities are shifted by one: `A B C E F G`.
     * This also has the side effect of reducing this array-like
     * entity size by one.
     *
     * @param[id] The ID of the entity to remove.
     *
     * @since 0.3
     */
    override fun removeChildEntity(id: String)

    /**
     * Removes the child entity with the given name from the
     * children of this entity.
     *
     * This call is guaranteed to fail unless no entity with the
     * given name is present in this array-like entity. In other
     * words, all the entities inside an array-like entity have
     * the same name, so attempting to remove only one will surely
     * result in ambiguity. This method will thus always throw an
     * [UnsupportedOperationException] except when the passed in
     * name does not correspond to the internal name of the children.
     *
     * @param[name] The name of the entity to be removed.
     * @exception UnsupportedOperationException If multiple matches
     * are found.
     *
     * @since 0.3
     */
    override fun removeChildEntityByName(name: String)

    /**
     * Removes all children entities with the given name from the
     * children of this entity.
     *
     * If no entities with the given name are found, the entity
     * is left as-is. Implementations may log an error message if
     * they deem necessary to do so.
     *
     * Otherwise, this corresponds to calling the [clear] method on
     * this array-like entity. Refer to it for more information.
     *
     * @param[name] The name of the entities to remove.
     *
     * @since 0.3
     */
    override fun removeAllChildEntitiesByName(name: String)

    /**
     * Removes the child entity of this array-like entity that
     * is present at the given index.
     *
     * If the removal is successful, all the other children
     * entities that follow the one that was removed are shifted
     * by one to avoid the presence of ghost entities. As an
     * example, consider the following list of entities:
     * `A B C D E F G`. Removing the entity identified in this
     * example by the letter `D` should leave a blank spot as
     * follows: `A B C   E F G`. To avoid that blank spot, all
     * the other entities are shifted by one: `A B C E F G`.
     * This also has the side effect of reducing this array-like
     * entity size by one.
     *
     * If the index refers to a position that is not present
     * in the current bounds of the array-like entity, then an
     * exception is raised and the entity is left as-is.
     *
     * @param[index] An [Int] specifying the position of the entity
     * to remove.
     * @exception IndexOutOfBoundsException If the index is not
     * inside the current bounds of the array-like entity.
     *
     * @since 0.3
     */
    fun removeChildEntityByIndex(index: Int)

    /**
     * Removes the child entity of this array-like entity that
     * is present at the given index.
     *
     * If the removal is successful, all the other children
     * entities that follow the one that was removed are shifted
     * by one to avoid the presence of ghost entities. As an
     * example, consider the following list of entities:
     * `A B C D E F G`. Removing the entity identified in this
     * example by the letter `D` should leave a blank spot as
     * follows: `A B C   E F G`. To avoid that blank spot, all
     * the other entities are shifted by one: `A B C E F G`.
     * This also has the side effect of reducing this array-like
     * entity size by one.
     *
     * If the index refers to a position that is not present
     * in the current bounds of the array-like entity, then an
     * exception is raised and the entity is left as-is.
     *
     * @param[index] An [ULong] specifying the position of the entity
     * to remove.
     * @exception IndexOutOfBoundsException If the index is not
     * inside the current bounds of the array-like entity.
     *
     * @since 0.3
     */
    fun removeChildEntityByIndex(index: ULong)

    /**
     * Adds an information pair to the information pairs
     * children of this entity.
     *
     * This method is guaranteed to throw an exception due to the
     * contract of the array-like entity.
     *
     * @param[infoPair] The information pair to add.
     * @exception UnsupportedOperationException Always.
     *
     * @since 0.3
     */
    override fun addChildInfoPair(infoPair: InfoPair<*>)

    /**
     * Gets a collection of all information pairs that are
     * children of this entity.
     *
     * This method is guaranteed to throw an exception due to
     * the contract of the array-like entity.
     *
     * @return A collection of all information pairs that are
     * children of this entity.
     * @exception UnsupportedOperationException Always.
     *
     * @since 0.3
     */
    override fun getChildInfoPairs(): Collection<InfoPair<*>>

    /**
     * Removes the given information pair from the information
     * pairs children of this entity.
     *
     * This method is guaranteed to throw an exception due to
     * the contract of the array-like entity.
     *
     * @param[infoPair] The information pair to remove.
     * @exception UnsupportedOperationException Always.
     *
     * @since 0.3
     */
    override fun removeChildInfoPair(infoPair: InfoPair<*>)

    /**
     * Removes the child information pair identified by the given
     * ID from the information pairs children of this entity.
     *
     * This method is guaranteed to throw an exception due to the
     * contract of the array-like entity.
     *
     * @param[id] The ID of the information pair to remove.
     * @exception UnsupportedOperationException Always.
     *
     * @since 0.3
     */
    override fun removeChildInfoPair(id: String)

    /**
     * Removes the child information pair with the given key from
     * the information pairs children of this entity.
     *
     * This method is guaranteed to throw an exception due to the
     * contract of the array-like entity.
     *
     * @param[key] The key of the information pair to be removed.
     * @exception UnsupportedOperationException Always.
     *
     * @since 0.3
     */
    override fun removeChildInfoPairByKey(key: String)

    /**
     * Removes all children information pairs with the given key
     * from the information pairs children of this entity.
     *
     * This method is guaranteed to throw an exception due to the
     * contract of the array-like entity.
     *
     * @param[key] The key of the information pairs to remove.
     * @exception UnsupportedOperationException Always.
     *
     * @since 0.3
     */
    override fun removeAllChildrenInfoPairByKey(key: String)

    /**
     * Finds all the children information pairs of this entity with
     * the given key.
     *
     * This method is guaranteed to throw an exception due to the
     * contract of the array-like entity.
     *
     * @param[key] The key of the information pairs to find.
     * @return A collection containing all children information pairs
     * with the given name. The collection may be empty.
     * @exception UnsupportedOperationException Always
     *
     * @since 0.3
     */
    override fun findChildInfoPairsByKey(key: String): Collection<InfoPair<*>>

    /**
     * Finds the child information pair of this entity with the
     * given ID.
     *
     * This method is guaranteed to throw an exception due to the
     * contract of the array-like entity.
     *
     * @param[id] The ID of the information pair to look for.
     * @return An [Optional] wrapping either the found entity
     * or nothing, according to the results.
     * @exception UnsupportedOperationException Always.
     *
     * @since 0.3
     */
    override fun findChildInfoPair(id: String): Optional<InfoPair<*>>
}