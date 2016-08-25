package net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.tag;

import net.thesilkminer.skl.interpreter.api.skd.SkdApi;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdProperty;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a tag that hosts a pair of values, called
 * left and right.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public class PairTag extends AbstractTag {

	/**
	 * Constructs a new instance of this pair tag with the
	 * given {@code name} and both values set to {@code null}.
	 *
	 * @param name
	 *      The tag's name.
	 *
	 * @since 0.1
	 */
	public PairTag(@Nonnull final String name) {
		this(name, null, null);
	}

	/**
	 * Constructs a new instance of this pair tag with the
	 * given {@code name} and default values.
	 *
	 * @param name
	 *      The tag's name.
	 * @param left
	 *      The left side of this tag.
	 * @param right
	 *      The right side of this tag.
	 *
	 * @since 0.1
	 */
	public PairTag(@Nonnull final String name,
	               @Nullable final String left,
	               @Nullable final String right) {
		super(name);
		this.setVoidElement();
		super.addProperty(SkdApi.get().api().property("left", this.toString(left)));
		super.addProperty(SkdApi.get().api().property("right", this.toString(right)));
	}

	/**
	 * Constructs a new instance of this pair tag with the given
	 * {@code name} and default values.
	 *
	 * @param name
	 *      The tag's name.
	 * @param pair
	 *      A pair of both key and value.
	 *
	 * @since 0.1
	 */
	public PairTag(@Nonnull final String name, @Nonnull final Pair<String, String> pair) {
		this(name, pair.getLeft(), pair.getRight());
	}

	@Nonnull
	@Override
	public List<ISkdProperty> getProperties() {
		throw new UnsupportedOperationException("Use #getLeft() or #getRight() instead");
	}

	@Contract("_ -> fail")
	@Override
	public boolean addProperty(@Nonnull final ISkdProperty property) {
		throw new UnsupportedOperationException();
	}

	@Contract("_ -> fail")
	@Override
	public boolean removeProperty(@Nonnull final ISkdProperty property) {
		throw new UnsupportedOperationException();
	}

	@Contract("_ -> fail")
	@Override
	public boolean hasProperty(@Nonnull final ISkdProperty property) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the left value of this pair tag.
	 *
	 * @return
	 *      The left value of this pair tag.
	 *
	 * @since 0.1
	 */
	@Nonnull
	public String getLeft() {
		return super.getProperties().get(0).getValue().orElseThrow(RuntimeException::new);
	}

	/**
	 * Gets the right value of this pair tag.
	 *
	 * @return
	 *      The right value of this pair tag.
	 *
	 * @since 0.1
	 */
	@Nonnull
	public String getRight() {
		return super.getProperties().get(1).getValue().orElseThrow(RuntimeException::new);
	}

	/**
	 * Sets the left value of this pair tag.
	 *
	 * @param left
	 *      The new left value.
	 *
	 * @since 0.1
	 */
	public void setLeft(@Nullable final String left) {
		super.getProperties().get(0).setValue(this.toString(left));
	}

	/**
	 * Sets the right value of this pair tag.
	 *
	 * @param right
	 *      The new right value.
	 *
	 * @since 0.1
	 */
	public void setRight(@Nullable final String right) {
		super.getProperties().get(1).setValue(this.toString(right));
	}

	/**
	 * Sets the new values from the given pair of strings.
	 *
	 * @param left
	 *      The left value of the pair.
	 * @param right
	 *      The right value of the pair.
	 *
	 * @since 0.1
	 */
	public void fromPair(@Nullable final String left, @Nullable final String right) {
		this.setLeft(left);
		this.setRight(right);
	}

	/**
	 * Sets the new values from the given {@code pair}.
	 *
	 * @param pair
	 *      The new values.
	 *
	 * @since 0.1
	 */
	public void fromPair(@Nonnull final Pair<String, String> pair) {
		this.fromPair(pair.getLeft(), pair.getValue());
	}

	/**
	 * Returns the values of this pair tag as an {@link Pair Apache Pair}.
	 *
	 * @return
	 *      A new mutable pair with this left and right values.
	 *
	 * @since 0.1
	 */
	@Contract(pure = true)
	@Nonnull
	public Pair<String, String> toPair() {
		return MutablePair.of(this.getLeft(), this.getRight());
	}

	@Contract(pure = true)
	@Nullable
	@SuppressWarnings("unused")
	private String fromString(@Nonnull final String string) {
		return "null".equals(string) ? null : string;
	}

	@Contract(value = "_ -> !null", pure = true)
	@Nonnull
	private String toString(@Nullable final String string) {
		return String.valueOf(string);
	}
}
