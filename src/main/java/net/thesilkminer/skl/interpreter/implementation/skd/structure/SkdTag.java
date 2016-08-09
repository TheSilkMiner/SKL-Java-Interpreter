package net.thesilkminer.skl.interpreter.implementation.skd.structure;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdProperty;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdTag;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * This class represents a tag in skd, which is e.g. {@code <tag></tag>}
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public class SkdTag implements ISkdTag {

	private final String name;
	private String content;
	private boolean voidElement;
	private final List<ISkdTag> children;
	private final List<ISkdProperty> properties;
	private boolean closed;

	private SkdTag(final String name) {

		Preconditions.checkNotNull(name, "Tag name must not be null");
		this.name = name;
		this.content = null;
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
	public static SkdTag of(@Nonnull final String name) {

		return new SkdTag(Preconditions.checkNotNull(name));
	}

	@Override
	public String getName() {

		return this.name;
	}

	@Override
	public Optional<String> getContent() {

		return Optional.ofNullable(this.content);
	}

	@Override
	public boolean isVoidElement() {

		return this.voidElement;
	}

	@Override
	public List<ISkdTag> getChildren() {

		return this.children;
	}

	@Override
	public List<ISkdProperty> getProperties() {

		return this.properties;
	}

	@Override
	public void addChildTag(final ISkdTag tag) {
		Preconditions.checkState(!this.closed(), "Tag closed");
		Preconditions.checkState(!this.voidElement,
				      "The tag is void");
		this.children.add(tag);
	}

	@Override
	public void setAsChild(final ISkdTag parent) {

		parent.getChildren().add(this);
	}

	@Override
	public void removeChildTag(final ISkdTag tag) {
		Preconditions.checkState(!this.closed(), "Tag closed");
		this.children.remove(tag);
	}

	@Override
	public void setVoidElement() {
		Preconditions.checkState(!this.closed(), "Tag closed");
		Preconditions.checkState(this.content == null,
				      "The tag has already some content");
		this.voidElement = true;
	}

	@Override
	public void setContent(@Nonnull final String content) {
		Preconditions.checkState(!this.closed(), "Tag closed");
		Preconditions.checkState(!this.voidElement, "The tag is void");
		Preconditions.checkNotNull(content, "Use removeContent() to remove the content");
		this.content = content;
	}

	@Override
	public void removeContent() {
		Preconditions.checkState(!this.closed(), "Tag closed");
		Preconditions.checkState(!this.voidElement, "The tag is void");
		this.content = null;
	}

	@Override
	public boolean addProperty(@Nonnull final ISkdProperty property) {

		if (this.hasProperty(property)) {

			return false;
		}

		this.properties.add(property);
		return true;
	}

	@Override
	public boolean removeProperty(@Nonnull ISkdProperty property) {

		if (!this.hasProperty(property)) {

			return false;
		}

		this.properties.remove(property);
		return true;
	}

	@Override
	public boolean hasProperty(@Nonnull ISkdProperty property) {

		return this.properties.contains(property);
	}

	@Override
	public void close() {
		this.closed = true;
	}

	@Override
	public boolean closed() {
		return this.closed;
	}

	@Override
	public boolean equals(final Object obj) {

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

		builder += "<";
		builder += this.name;
		builder += " ";

		for (ISkdProperty prop : this.properties) {

			builder += prop.toString();
			builder += " ";
		}

		builder = builder.substring(0, builder.length() - 1);

		if (this.voidElement) {

			builder += " />";

			return builder;
		}

		builder += ">";

		for (final ISkdTag tag : this.children) {

			final String[] lines = tag.toString().split("\\n");

			for (final String line : lines) {
				builder += "\n\t";
				builder += line;
			}
		}

		if (!this.children.isEmpty()) {

			builder += "\n";
		}

		if (this.content != null) {

			final String[] lines = this.content.split("\\n");

			for (final String line : lines) {

				builder += "\n\t";
				builder += line;
			}

			builder += "\n";
		}

		if (this.children.isEmpty() && this.content == null) {

			builder += "\n";
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
	public static void main(String... args) {

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

		//main.setContent(Optional.of("Test content"));
		main.setContent("Test content");

		childThree.setVoidElement();

		System.out.println(main.toString());

		main.removeChildTag(childThree);

		System.out.println(main.toString());

		System.out.println(childOne.removeProperty(propertyTwo));
		System.out.println(childOne.removeProperty(propertyOne));

		System.out.println(main.toString());

		//main.setContent(Optional.empty());
		main.removeContent();

		System.out.println(main.toString());

		System.out.println("============= DATABASE CONSTRUCTION ATTEMPT =============");

		final SkdTag dbMain = SkdTag.of("main");

		final SkdTag dbChild = SkdTag.of("child");
		dbChild.setAsChild(dbMain);

		final SkdTag dbTag1 = SkdTag.of("tag");
		//dbTag1.setContent(Optional.of("Content test"));
		dbTag1.setContent("Content test");
		dbTag1.setAsChild(dbChild);

		final SkdTag dbTag2 = SkdTag.of("tag");
		//dbTag2.setContent(Optional.of("Content test\n#2"));
		dbTag2.setContent("Content test\n#2");
		dbTag2.setAsChild(dbChild);

		final SkdTag dbOther = SkdTag.of("other");
		dbOther.setAsChild(dbMain);

		final SkdTag dbProperties = SkdTag.of("properties");
		dbProperties.addProperty(SkdProperty.getProperty("test1", "yes"));
		dbProperties.addProperty(SkdProperty.getProperty("test2", Optional.of("bella")));
		dbProperties.setAsChild(dbOther);

		final SkdTag dbAVoidTag1 = SkdTag.of("aVoidTag");
		dbAVoidTag1.setVoidElement();
		dbAVoidTag1.setAsChild(dbProperties);

		final SkdTag dbAVoidTagWithProps = SkdTag.of("aVoidTagWithProps");
		dbAVoidTagWithProps.setVoidElement();
		dbAVoidTagWithProps.addProperty(SkdProperty.getProperty("prop", "prop"));
		dbAVoidTagWithProps.setAsChild(dbProperties);

		final SkdTag dbAVoidTag2 = SkdTag.of("aVoidTag");
		dbAVoidTag2.setVoidElement();
		dbAVoidTag2.setAsChild(dbOther);

		System.out.println(dbMain);
	}
}
