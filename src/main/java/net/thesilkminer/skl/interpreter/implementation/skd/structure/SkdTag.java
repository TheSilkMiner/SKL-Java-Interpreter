package net.thesilkminer.skl.interpreter.implementation.skd.structure;

/**
 * Created by TheSilkMiner on 09/10/2015.
 * Package: net.thesilkminer.skl.interpreter.skd.structure.
 * Project: Java Interpreter.
 */

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * This class represents a tag in skd, which is e.g. {@code <tag></tag>}
 */
public class SkdTag {

	private String name;
	private Optional<String> content;
	private boolean voidElement;
	private List<SkdTag> children;
	private List<SkdProperty> properties;

	private SkdTag(String name) {

		Preconditions.checkNotNull(name, "Tag name must not be null");
		this.name = name;
		this.content = Optional.empty();
		this.children = Lists.newArrayList();
		this.properties = Lists.newArrayList();
	}

	/**
	 * Gets a new instance of an SkdTag.
	 *
	 * @param name
	 * 		The tag's name.
	 * @return
	 * 		A new tag instance
	 */
	public static SkdTag of(@Nonnull String name) {

		return new SkdTag(Preconditions.checkNotNull(name));
	}

	/**
	 * Gets the tag's name.
	 *
	 * @return
	 * 		The tag's name.
	 */
	public String getName() {

		return this.name;
	}

	/**
	 * Gets the content of the tag (aka the part between brackets).
	 *
	 * @return
	 * 		The tag's content. It may be {@link Optional#empty()}.
	 */
	public Optional<String> getContent() {

		return this.content;
	}

	/**
	 * Gets if this is a void element.
	 *
	 * @return
	 * 		If the tag is a void tag.
	 */
	public boolean isVoidElement() {

		return this.voidElement;
	}

	/**
	 * Gets the list of children tags.
	 *
	 * @return
	 * 		The list of childern tags.
	 */
	public List<SkdTag> getChildren() {

		return this.children;
	}

	/**
	 * Gets the properties of this tag.
	 *
	 * @return
	 * 		The tag's properties.
	 */
	public List<SkdProperty> getProperties() {

		return this.properties;
	}

	/**
	 * Adds a child tag to this tag.
	 *
	 * <p>Remember that the addition of tags is order-sensitive.</p>
	 *
	 * @param tag
	 * 		The tag to add.
	 */
	public void addChildTag(SkdTag tag) {

		Preconditions.checkState(!this.voidElement,
				      "The tag is void");
		this.children.add(tag);
	}

	/**
	 * Sets the tag as a child of another tag.
	 *
	 * @param parent
	 * 		The parent of this tag
	 */
	public void setAsChild(SkdTag parent) {

		parent.children.add(this);
	}

	/**
	 * Removes a child tag.
	 *
	 * @param tag
	 * 		The tag to remove
	 */
	public void removeChildTag(SkdTag tag) {

		this.children.remove(tag);
	}

	/**
	 * Sets the tag as a void element.
	 */
	public void setVoidElement() {

		Preconditions.checkState(!this.content.isPresent(),
				      "The tag has already some content");
		this.voidElement = true;
	}

	/**
	 * Sets the tag's content.
	 *
	 * @param content
	 * 		A content. It can be {@link Optional#empty()}.
	 */
	public void setContent(@Nonnull Optional<String> content) {

		Preconditions.checkState(!this.voidElement,
				      "The tag is void");
		Preconditions.checkNotNull(content);
		this.content = content;
	}

	/**
	 * Adds a property to the tag.
	 *
	 * @param property
	 * 		The property to add.
	 * @return
	 * 		True if the property was added successfully, false if not.
	 */
	public boolean addProperty(@Nonnull SkdProperty property) {

		if (this.hasProperty(property)) {

			return false;
		}

		this.properties.add(property);
		return true;
	}

	/**
	 * Removes a property to the tag.
	 *
	 * @param property
	 * 		The property to remove.
	 * @return
	 * 		True if the property was removed successfully, false if not.
	 */
	public boolean removeProperty(@Nonnull SkdProperty property) {

		if (!this.hasProperty(property)) {

			return false;
		}

		this.properties.remove(property);
		return true;
	}

	/**
	 * Gets if the tag has the specified property.
	 *
	 * @param property
	 * 		The property to look for.
	 * @return
	 * 		True if found, false otherwise.
	 */
	public boolean hasProperty(@Nonnull SkdProperty property) {

		return this.properties.contains(property);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {

			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {

			return false;
		}

		SkdTag that = (SkdTag) obj;
		return Objects.equals(this.isVoidElement(), that.isVoidElement())
				&&	Objects.equals(this.getName(), that.getName())
				&&	Objects.equals(this.getContent(), that.getContent())
				&&  Objects.equals(this.getChildren(), that.getChildren())
				&&	Objects.equals(this.getProperties(), that.getProperties());
	}

	@Override
	public int hashCode() {

		return Objects.hash(
				this.getName(),
				this.getContent(),
				this.isVoidElement(),
				this.getChildren(),
				this.getProperties()
		);
	}

	@Override
	public String toString() {

		String builder = "";

		builder += "SkdTag{";
		builder += "<";
		builder += this.name;
		builder += " ";

		for (SkdProperty prop : this.properties) {

			builder += prop.toString(true);
			builder += " ";
		}

		builder = builder.substring(0, builder.length() - 1);

		if (this.voidElement) {

			builder += " />";
			builder += "}";

			return builder;
		}

		builder += ">";

		if (this.content.isPresent()) {

			builder += this.content.get();
			builder += " ";
		}

		for (SkdTag tag : this.children) {

			builder += tag.toString(true);
		}

		builder += "</";
		builder += this.name;
		builder += ">";

		builder += "}";

		return builder;
	}

	public String toString(boolean toConcat) {

		if (!toConcat) {

			return this.toString();
		}

		String builder = "";

		builder += "<";
		builder += this.name;
		builder += " ";

		for (SkdProperty prop : this.properties) {

			builder += prop.toString(true);
			builder += " ";
		}

		builder = builder.substring(0, builder.length() - 1);

		if (this.voidElement) {

			builder += " />";

			return builder;
		}

		builder += ">";

		if (this.content.isPresent()) {

			builder += this.content.get();
			builder += " ";
		}

		for (SkdTag tag : this.children) {

			builder += tag.toString(true);
		}

		builder += "</";
		builder += this.name;
		builder += ">";

		return builder;
	}

	/**
	 * Test method.
	 *
	 * @param args
	 * 		args
	 */
	public static void main(String[] args) {

		final SkdProperty propertyOne = SkdProperty.getProperty("test", "example");
		final SkdProperty propertyTwo = SkdProperty.getProperty("name", Optional.of("me"));
		final SkdProperty propertyThree = SkdProperty.getProperty("author", "BELLA");
		final SkdProperty propertyFour = SkdProperty.getProperty("title", Optional.empty());

		final SkdTag childOne = SkdTag.of("child");
		final SkdTag childTwo = SkdTag.of("baby");
		final SkdTag childThree = SkdTag.of("sheep");

		final SkdTag main = SkdTag.of("main");

		childOne.addProperty(propertyOne);
		childTwo.addProperty(propertyTwo);
		main.addProperty(propertyThree);
		main.addProperty(propertyFour);

		childOne.addChildTag(childTwo);
		childThree.setAsChild(main);
		main.addChildTag(childOne);

		main.setContent(Optional.of("Test content"));

		childThree.setVoidElement();

		System.out.println(main.toString(false));

		main.removeChildTag(childThree);

		System.out.println(main.toString(false));

		System.out.println(childOne.removeProperty(propertyTwo));
		System.out.println(childOne.removeProperty(propertyOne));

		System.out.println(main.toString(false));
	}
}
