package net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.tag;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdProperty;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdTag;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Main class extended by all ParserEx tags, containing some
 * default implementations and other useful methods.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public abstract class AbstractTag implements ISkdTag {

	@FunctionalInterface
	public interface CanAcceptHandler<T extends ISkdTag> {
		boolean canAccept(@Nonnull final T type);
	}

	@FunctionalInterface
	public interface AcceptHandler<T extends ISkdTag> {
		@Nullable
		T accept(@Nonnull final T type);
	}

	private static final Collection<Class<?>> REGISTERED_TAGS = Lists.newArrayList();
	private static final Map<Class<? extends AbstractTag>,
			Pair<CanAcceptHandler<? super ISkdTag>,
					AcceptHandler<? super ISkdTag>>> PAIRS =
			Maps.newHashMap();

	private final String name;
	private String content;
	private boolean voidElement;
	private List<ISkdTag> children;
	private List<ISkdProperty> properties;
	private boolean closed;

	protected AbstractTag(@Nonnull final String name) {
		this.name = Preconditions.checkNotNull(name);
		this.content = null;
		this.voidElement = false;
		this.children = Lists.newArrayList();
		this.properties = Lists.newArrayList();
		this.closed = false;

		REGISTERED_TAGS.add(this.getClass());
	}

	public static void register(@Nonnull final Class<? extends AbstractTag> clazz,
	                            @Nonnull final CanAcceptHandler<? super ISkdTag> cah,
	                            @Nonnull final AcceptHandler<? super ISkdTag> ah) {
		PAIRS.put(clazz, Pair.of(cah, ah));
	}

	public static Collection<Class<?>> getTags() {
		return ImmutableList.copyOf(REGISTERED_TAGS);
	}

	public static Map<Class<? extends AbstractTag>,
			Pair<CanAcceptHandler<? super ISkdTag>,
					AcceptHandler<? super ISkdTag>>> getPairs() {
		return ImmutableMap.copyOf(PAIRS);
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
		return ImmutableList.copyOf(this.children);
	}

	@Nonnull
	@Override
	public List<ISkdProperty> getProperties() {
		return ImmutableList.copyOf(this.properties);
	}

	@Override
	public void addChildTag(@Nonnull final ISkdTag tag) {
		Preconditions.checkNotNull(tag);
		Preconditions.checkState(!this.isVoidElement(), "Tag is void");
		Preconditions.checkState(!this.closed(), "Tag closed");
		this.children.add(tag);
	}

	@Override
	public void setAsChild(@Nonnull final ISkdTag parent) {
		Preconditions.checkNotNull(parent);
		Preconditions.checkState(!parent.isVoidElement(), "Parent is void");
		Preconditions.checkState(!parent.closed(), "Parent tag closed");
		parent.addChildTag(this);
	}

	@Override
	public void removeChildTag(@Nonnull final ISkdTag tag) {
		Preconditions.checkNotNull(tag);
		Preconditions.checkState(!this.closed(), "Tag closed");

		if (this.shouldFailOnInvalidChildTagRemoval()) {
			Preconditions.checkState(this.children.contains(tag), "Child not present");
		}

		this.children.remove(tag);
	}

	/**
	 * Returns whether this tag should fail when a software
	 * attempts to remove a child tag that was never added through
	 * the {@link #removeChildTag(ISkdTag)} method.
	 *
	 * @return
	 *      If the method should fail.
	 *
	 * @since 0.1
	 */
	protected boolean shouldFailOnInvalidChildTagRemoval() {
		return false;
	}

	@Override
	public void setVoidElement() {
		Preconditions.checkState(!this.closed(), "Tag closed");
		Preconditions.checkState(!this.getContent().isPresent(),
				"There is already some content");
		this.voidElement = true;
	}

	@Override
	public void setContent(@Nonnull final String content) {
		Preconditions.checkNotNull(content, "Use #removeContent() instead");
		Preconditions.checkState(!this.closed(), "Tag closed");
		this.content = content;
	}

	/**
	 * Appends the given {@code content} to the one already present
	 * in the tag.
	 *
	 * <p>Before appending the content, a newline is added onto the
	 * already available one.</p>
	 *
	 * <p>By default, this method throws an error if a content is
	 * not already present. This behaviour can be controlled with the
	 * {@link #shouldFailIfNoContentIsPresentOnAppending()} method.</p>
	 *
	 * @param content
	 *      The content to append.
	 *
	 * @since 0.1
	 */
	public void appendToContent(@Nonnull final String content) {
		Preconditions.checkNotNull(content);
		Preconditions.checkState(!this.closed(), "Tag closed");

		if (this.shouldFailIfNoContentIsPresentOnAppending()) {
			Preconditions.checkState(this.getContent().isPresent(),
					"Impossible to append on unavailable content");
		}

		if (this.content == null) {
			// Reachable only if the previous check isn't triggered.
			this.content = "";
		}

		this.content += "\n";
		this.content += content;
	}

	/**
	 * Returns whether this tag should throw an exception if there is
	 * no available content when attempting to append some more
	 * content.
	 *
	 * @return
	 *      If the operation should fail.
	 *
	 * @since 0.1
	 */
	protected boolean shouldFailIfNoContentIsPresentOnAppending() {
		return true;
	}

	@Override
	public void removeContent() {
		Preconditions.checkState(!this.closed(), "Tag closed");
		this.content = null;
	}

	@Override
	public boolean addProperty(@Nonnull final ISkdProperty property) {
		Preconditions.checkNotNull(property);
		Preconditions.checkState(!this.closed(), "Tag closed");
		return this.properties.add(property);
	}

	@Override
	public boolean removeProperty(@Nonnull final ISkdProperty property) {
		Preconditions.checkNotNull(property);
		Preconditions.checkState(!this.closed(), "Tag closed");

		if (this.shouldFailOnInvalidPropertyRemoval()) {
			Preconditions.checkState(this.hasProperty(property),
					"Property not present");
		}

		return this.properties.remove(property);
	}

	/**
	 * Returns whether this tag should fail when a software
	 * attempts to remove a property that was never added through
	 * the {@link #removeProperty(ISkdProperty)} method.
	 *
	 * @return
	 *      If the method should fail.
	 *
	 * @since 0.1
	 */
	protected boolean shouldFailOnInvalidPropertyRemoval() {
		return false;
	}

	@Override
	public boolean hasProperty(@Nonnull final ISkdProperty property) {
		return this.properties.contains(Preconditions.checkNotNull(property));
	}

	@Override
	public void close() {
		this.closed = true;
	}

	@Override
	public boolean closed() {
		return this.closed;
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
	 * Removes this tag from the list of tags.
	 *
	 * <p><strong>Calling this method will prevent your tag from
	 * being queried for acceptance.</strong></p>
	 *
	 * @since 0.1
	 */
	protected void removeFromList() {
		final StackTraceElement[] it = new Exception().getStackTrace();
		if (!it[1].getClassName().contains("ParserExService")) {
			return;
		}
		REGISTERED_TAGS.remove(this.getClass());
	}
}
