package net.thesilkminer.skl.interpreter.api.skd.structure;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Represents a tag inside the SKD language specification.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public interface ISkdTag extends IAcceptable<ISkdTag> {

	/**
	 * Gets the tag's name.
	 *
	 * @return
	 * 		The tag's name.
	 *
	 * @since 0.2
	 */
	@Nonnull
	String getName();

	/**
	 * Gets the content of the tag (aka the part between brackets).
	 *
	 * @return
	 * 		The tag's content. It may be {@link Optional#empty()}.
	 *
	 * @since 0.2
	 */
	@Nonnull
	Optional<String> getContent();

	/**
	 * Gets if this is a void element.
	 *
	 * @return
	 * 		If the tag is a void tag.
	 *
	 * @since 0.2
	 */
	boolean isVoidElement();

	/**
	 * Gets the list of children tags.
	 *
	 * @return
	 * 		The list of children tags.
	 *
	 * @since 0.2
	 */
	@Nonnull
	List<ISkdTag> getChildren();

	/**
	 * Gets the properties of this tag.
	 *
	 * @return
	 * 		The tag's properties.
	 *
	 * @since 0.2
	 */
	@Nonnull
	List<ISkdProperty> getProperties();

	/**
	 * Adds a child tag to this tag.
	 *
	 * <p>Remember that the addition of tags is order-sensitive.</p>
	 *
	 * @param tag
	 * 		The tag to add.
	 *
	 * @since 0.2
	 */
	void addChildTag(@Nonnull final ISkdTag tag);

	/**
	 * Sets the tag as a child of another tag.
	 *
	 * <p>Just a convenience method: you can avoid implementing
	 * this if you want. State this decision in the Javadoc,
	 * though.</p>
	 *
	 * @param parent
	 * 		The parent of this tag
	 *
	 * @since 0.2
	 */
	void setAsChild(@Nonnull final ISkdTag parent);

	/**
	 * Removes a child tag.
	 *
	 * @param tag
	 * 		The tag to remove.
	 *
	 * @since 0.2
	 */
	void removeChildTag(@Nonnull final ISkdTag tag);

	/**
	 * Sets the tag as a void element.
	 *
	 * @since 0.2
	 */
	void setVoidElement();

	/**
	 * Sets the tag's content.
	 *
	 * @param content
	 * 		A content. It can be {@link Optional#empty()}.
	 *
	 * @deprecated
	 *      Use {@link #setContent(String)} or
	 *      {@link #removeContent()} instead.
	 *
	 * @since 0.2
	 */
	@Deprecated
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	default void setContent(@Nonnull final Optional<String> content) {
		if (content.isPresent()) {
			this.setContent(content.get());
		} else {
			this.removeContent();
		}
	}

	/**
	 * Sets the tag's content.
	 *
	 * <p>To remove the content, consider using
	 * {@link #removeContent()} instead.</p>
	 *
	 * @param content
	 *      The new content. It must not be null.
	 *
	 * @since 0.2.1
	 */
	void setContent(@Nonnull final String content);

	/**
	 * Removes the current tag's content.
	 *
	 * @since 0.2.1
	 */
	void removeContent();

	/**
	 * Adds a property to the tag.
	 *
	 * @param property
	 * 		The property to add.
	 * @return
	 * 		True if the property was added successfully, false if not.
	 *
	 * @since 0.2
	 */
	boolean addProperty(@Nonnull final ISkdProperty property);

	/**
	 * Removes a property to the tag.
	 *
	 * @param property
	 * 		The property to remove.
	 * @return
	 * 		True if the property was removed successfully, false if not.
	 *
	 * @since 0.2
	 */
	boolean removeProperty(@Nonnull final ISkdProperty property);

	/**
	 * Gets if the tag has the specified property.
	 *
	 * @param property
	 * 		The property to look for.
	 * @return
	 * 		True if found, false otherwise.
	 *
	 * @since 0.2
	 */
	boolean hasProperty(@Nonnull final ISkdProperty property);

	/**
	 * Closes a tag.
	 *
	 * @implNote
	 *      This method must be called when a tag is
	 *      closed and no further edits can be performed
	 *      during that database reading. Setting as a
	 *      children of another tag or adding a children
	 *      is not considered "editing" a tag in a strict
	 *      manner.
	 *
	 * @since 0.2.1
	 */
	void close();

	/**
	 * Gets if a tag has been closed.
	 *
	 * <p>If this tag is a {@link #setVoidElement() void}
	 * {@link #isVoidElement() tag}, then this method must
	 * always return {@code true}. In other cases, this
	 * method returns {@code true} if and only if the
	 * method {@link #close()} has been called.</p>
	 *
	 * @return
	 *      If the tag is closed.
	 *
	 * @since 0.2.1
	 */
	boolean closed();
}
