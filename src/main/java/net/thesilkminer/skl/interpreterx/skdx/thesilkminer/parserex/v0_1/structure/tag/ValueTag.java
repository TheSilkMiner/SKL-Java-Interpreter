package net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.tag;

import net.thesilkminer.skl.interpreter.api.skd.SkdApi;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdProperty;

import org.jetbrains.annotations.Contract;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a tag that only holds a {@code value}.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public class ValueTag extends AbstractTag {

	/**
	 * Constructs a value tag with the specified name and
	 * a {@code null} value.
	 *
	 * @param name
	 *      The tag's name.
	 *
	 * @since 0.1
	 */
	public ValueTag(@Nonnull final String name) {
		this(name, null);
	}

	/**
	 * Constructs a value tag with the give {@code name} and
	 * the given {@code value}.
	 *
	 * @param name
	 *      The tag's name.
	 * @param value
	 *      The tag's value. It can be {@code null}.
	 *
	 * @since 0.1
	 */
	public ValueTag(@Nonnull final String name, @Nullable final String value) {
		super(name);
		this.setVoidElement();
		super.addProperty(SkdApi.get().api().property("value", this.toString(value)));
	}

	@Nonnull
	@Override
	public List<ISkdProperty> getProperties() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addProperty(@Nonnull final ISkdProperty property) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeProperty(@Nonnull final ISkdProperty property) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasProperty(@Nonnull final ISkdProperty property) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the value associated with this value tag.
	 *
	 * @return
	 *      The tag's value.
	 *
	 * @since 0.1
	 */
	@Nonnull
	public String getValue() {
		return super.getProperties().get(0).getValue().orElseThrow(RuntimeException::new);
	}

	/**
	 * Gets the value associated with this value tag or {@code null}
	 * if none is available.
	 *
	 * @return
	 *      The tag's value or {@code null}.
	 *
	 * @since 0.1
	 */
	@Nullable
	public String getValueOrNull() {
		return this.fromString(this.getValue());
	}

	/**
	 * Sets the value associated to this tag.
	 *
	 * @param val
	 *      The new value.
	 *
	 * @since 0.1
	 */
	public void setValue(@Nullable final String val) {
		super.getProperties().get(0).setValue(this.toString(val));
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
