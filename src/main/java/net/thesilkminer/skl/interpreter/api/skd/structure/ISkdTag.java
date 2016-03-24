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
public interface ISkdTag {

	/**
	 * Gets the tag's name.
	 *
	 * @return
	 * 		The tag's name.
	 */
	String getName();

	/**
	 * Gets the content of the tag (aka the part between brackets).
	 *
	 * @return
	 * 		The tag's content. It may be {@link Optional#empty()}.
	 */
	Optional<String> getContent();

	/**
	 * Gets if this is a void element.
	 *
	 * @return
	 * 		If the tag is a void tag.
	 */
	boolean isVoidElement();

	/**
	 * Gets the list of children tags.
	 *
	 * @return
	 * 		The list of children tags.
	 */
	List<ISkdTag> getChildren();

	/**
	 * Gets the properties of this tag.
	 *
	 * @return
	 * 		The tag's properties.
	 */
	List<ISkdProperty> getProperties();

	/**
	 * Adds a child tag to this tag.
	 *
	 * <p>Remember that the addition of tags is order-sensitive.</p>
	 *
	 * @param tag
	 * 		The tag to add.
	 */
	void addChildTag(final ISkdTag tag);

	/**
	 * Sets the tag as a child of another tag.
	 *
	 * <p>Just a convenience method: you can avoid implementing
	 * this if you want. State this decision in the Javadoc,
	 * though.</p>
	 *
	 * @param parent
	 * 		The parent of this tag
	 */
	void setAsChild(final ISkdTag parent);

	/**
	 * Removes a child tag.
	 *
	 * @param tag
	 * 		The tag to remove
	 */
	void removeChildTag(final ISkdTag tag);

	/**
	 * Sets the tag as a void element.
	 */
	void setVoidElement();

	/**
	 * Sets the tag's content.
	 *
	 * @param content
	 * 		A content. It can be {@link Optional#empty()}.
	 */
	void setContent(@Nonnull final Optional<String> content);

	/**
	 * Adds a property to the tag.
	 *
	 * @param property
	 * 		The property to add.
	 * @return
	 * 		True if the property was added successfully, false if not.
	 */
	boolean addProperty(@Nonnull final ISkdProperty property);

	/**
	 * Removes a property to the tag.
	 *
	 * @param property
	 * 		The property to remove.
	 * @return
	 * 		True if the property was removed successfully, false if not.
	 */
	boolean removeProperty(@Nonnull final ISkdProperty property);

	/**
	 * Gets if the tag has the specified property.
	 *
	 * @param property
	 * 		The property to look for.
	 * @return
	 * 		True if found, false otherwise.
	 */
	boolean hasProperty(@Nonnull final ISkdProperty property);
}