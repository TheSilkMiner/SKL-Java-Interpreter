package net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.property;

import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Represents a property only used as a marker value,
 * which means a name, but no value.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public class MarkerProperty extends AbstractProperty {

	public MarkerProperty(@Nonnull final String name) {
		super(name);
	}

	@Nonnull
	@Override
	public Optional<String> getValue() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setValue(@Nonnull final String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeValue() {
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public String toString() {
		String str = "";

		str += this.getName();
		str += "=\"";
		str += super.getValue().orElse("");
		str += "\"";

		return str;
	}
}
