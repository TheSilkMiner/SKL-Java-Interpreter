package net.thesilkminer.skl.interpreter.implementation.skd.structure;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdProperty;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdTag;

import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

	private SkdTag(@Nonnull final String name) {
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
	@Contract(value = "null -> fail; !null -> !null", pure = true)
	@Nonnull
	public static SkdTag of(@Nonnull final String name) {
		return new SkdTag(Preconditions.checkNotNull(name));
	}

	@Nonnull
	@Override
	public String getName() {
		return this.name;
	}

	@Nonnull
	@Override
	public Optional<String> getContent() {
		return Optional.ofNullable(this.content);
	}

	@Override
	public boolean isVoidElement() {
		return this.voidElement;
	}

	@Nonnull
	@Override
	public List<ISkdTag> getChildren() {
		return this.children;
	}

	@Nonnull
	@Override
	public List<ISkdProperty> getProperties() {
		return this.properties;
	}

	@Override
	public void addChildTag(@Nonnull final ISkdTag tag) {
		Preconditions.checkState(!this.closed(), "Tag closed");
		Preconditions.checkState(!this.voidElement,
				      "The tag is void");
		this.children.add(tag);
	}

	@Override
	public void setAsChild(@Nonnull final ISkdTag parent) {
		parent.getChildren().add(this);
	}

	@Override
	public void removeChildTag(@Nonnull final ISkdTag tag) {
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
	public boolean removeProperty(@Nonnull final ISkdProperty property) {
		if (!this.hasProperty(property)) {
			return false;
		}

		this.properties.remove(property);
		return true;
	}

	@Override
	public boolean hasProperty(@Nonnull final ISkdProperty property) {
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
	public boolean equals(@Nullable final Object obj) {
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

	@Nonnull
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
	@SuppressWarnings("deprecation")
	public static void main(@Nonnull final String... args) {
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

		main.setContent("Test content");

		childThree.setVoidElement();

		System.out.println(main.toString());

		try {
			Thread.sleep(1000);
			System.out.println("\n");
		} catch (final InterruptedException exception) {
			//We don't care
		}

		main.removeChildTag(childThree);

		System.out.println(main.toString());

		try {
			Thread.sleep(1000);
			System.out.println("\n");
		} catch (final InterruptedException exception) {
			//We don't care
		}

		System.out.println(childOne.removeProperty(propertyTwo));
		System.out.println(childOne.removeProperty(propertyOne));

		System.out.println(main.toString());

		try {
			Thread.sleep(1000);
			System.out.println("\n");
		} catch (final InterruptedException exception) {
			//We don't care
		}

		childTwo.getProperties().get(0).removeValue();

		main.removeContent();

		System.out.println(main.toString());

		try {
			Thread.sleep(1000);
			System.out.println("\n");
		} catch (final InterruptedException exception) {
			//We don't care
		}

		System.out.println("============= DATABASE CONSTRUCTION ATTEMPT =============");

		final ISkdTag db$main = SkdTag.of("main");

		final ISkdTag db$main$child = SkdTag.of("child");
		db$main.addChildTag(db$main$child);

		final ISkdTag db$main$child$tag£1 = SkdTag.of("tag");
		db$main$child$tag£1.setContent("Content test");
		db$main$child.addChildTag(db$main$child$tag£1);

		final ISkdTag db$main$child$tag£2 = SkdTag.of("tag");
		db$main$child$tag£2.setContent("Content test\n#2");
		db$main$child.addChildTag(db$main$child$tag£2);

		final ISkdTag db$main$child$child = SkdTag.of("child");
		db$main$child.addChildTag(db$main$child$child);

		final ISkdTag db$main$child$child$evenMoreNested = SkdTag.of("evenMoreNested");
		db$main$child$child.addChildTag(db$main$child$child$evenMoreNested);

		final ISkdTag db$main$child$child$evenMoreNested$nestedAsHell =
				SkdTag.of("nestedAsHell");
		db$main$child$child$evenMoreNested$nestedAsHell.setVoidElement();
		db$main$child$child$evenMoreNested
				.addChildTag(db$main$child$child$evenMoreNested$nestedAsHell);

		final ISkdTag db$main$other = SkdTag.of("other");
		db$main$other.setAsChild(db$main);

		final ISkdTag db$main$other$properties = SkdTag.of("properties");
		db$main$other$properties.setAsChild(db$main$other);

		final ISkdProperty db$main$other$properties_test1 =
				SkdProperty.getProperty("test1", "yes");
		db$main$other$properties.addProperty(db$main$other$properties_test1);

		final ISkdProperty db$main$other$properties_test2 =
				SkdProperty.getProperty("test2", "bella");
		db$main$other$properties.addProperty(db$main$other$properties_test2);

		final ISkdProperty db$main$other$properties_test3 =
				SkdProperty.getProperty("test3", "spaces supported!!!");
		db$main$other$properties.addProperty(db$main$other$properties_test3);

		final ISkdTag db$main$other$properties$aVoidTag = SkdTag.of("aVoidTag");
		db$main$other$properties$aVoidTag.setVoidElement();
		db$main$other$properties$aVoidTag.setAsChild(db$main$other$properties);

		final ISkdTag db$main$other$properties$aVoidTagWithProps =
				SkdTag.of("aVoidTagWithProps");
		db$main$other$properties$aVoidTagWithProps.setVoidElement();
		db$main$other$properties$aVoidTagWithProps.addProperty(
				SkdProperty.getProperty("prop", "prop")
		);
		db$main$other$properties$aVoidTagWithProps.setAsChild(db$main$other$properties);

		final ISkdTag db$main$other$aVoidTag = SkdTag.of("aVoidTag");
		db$main$other$aVoidTag.setVoidElement();
		db$main$other$aVoidTag.setAsChild(db$main$other);

		final ISkdTag db$voidMain = SkdTag.of("voidMain");
		db$voidMain.setVoidElement();

		final net.thesilkminer.skl.interpreter.api.skd.structure.IStructure struct =
				Structure.newInstance(
						java.util.Arrays.asList(db$main, db$voidMain)
				);

		System.out.println(struct);
	}
}
