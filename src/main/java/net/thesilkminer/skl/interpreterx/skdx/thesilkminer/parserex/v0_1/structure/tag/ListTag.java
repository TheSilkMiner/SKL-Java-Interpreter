package net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.tag;

import com.google.common.base.Preconditions;

import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdTag;

import javax.annotation.Nonnull;

/**
 * Tag representing a list holder of various tags.
 *
 * <p>A tag of this type follows the structure:</p>
 *
 * <pre>
 *     {@literal <}listTag{@literal >}
 *         {@literal <}listElement{@literal >}
 *         {@literal <}listElement{@literal >}
 *         {@literal <}listElement{@literal >}
 *     {@literal <}/listTag{@literal >}
 * </pre>
 *
 * <p>By default, if the name supplied ends with {@code s},
 * the child tags will have to be the specified name, minus
 * the {@code s}.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public class ListTag extends AbstractTag {

	private final String childTagsName;

	/**
	 * Constructs a list tag with the specified {@code name} and
	 * a constructed {@code childTagsName}.
	 *
	 * <p>The constructed name is constructed as follows:</p>
	 *
	 * <p>if the specified {@code name} ends with an {@code s},
	 * the child tags name is the same as {@code name}, with
	 * the final {@code s} removed;</p>
	 *
	 * <p>if the specified {@code name} does not end with an
	 * {@code s}, the same {@code name} is given as the child
	 * tags name.</p>
	 *
	 * @param name
	 *      The tag's main name.
	 *
	 * @since 0.1
	 */
	public ListTag(@Nonnull final String name) {
		this(name, name.endsWith("s") ? name.substring(name.length() - 1) : name);
	}

	/**
	 * Constructs a list tag with the specified {@code name} and
	 * the specified {@code childTagsName}.
	 *
	 * @param name
	 *      The tag's main name.
	 * @param childTagsName
	 *      The name child tags must have to be accepted.
	 *
	 * @since 0.1
	 */
	public ListTag(@Nonnull final String name, @Nonnull final String childTagsName) {
		super(name);
		this.childTagsName = childTagsName;
		super.removeContent();
	}

	@Override
	public void addChildTag(@Nonnull final ISkdTag tag) {
		Preconditions.checkNotNull(tag);
		Preconditions.checkArgument(tag.getName().equals(this.childTagsName),
				"The child tag must have %s name to be accepted",
				this.childTagsName);
		super.addChildTag(tag);
	}

	@Override
	public void setContent(@Nonnull final String content) {
		throw new UnsupportedOperationException("List tags must not have content");
	}

	@Override
	public void appendToContent(@Nonnull final String content) {
		this.setContent(content);
	}
}
