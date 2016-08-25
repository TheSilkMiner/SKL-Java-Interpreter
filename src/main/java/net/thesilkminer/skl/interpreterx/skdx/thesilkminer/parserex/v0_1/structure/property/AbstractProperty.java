package net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.property;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdProperty;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Main class extended by all ParserEx properties, containing some
 * default implementations and other useful methods.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public abstract class AbstractProperty implements ISkdProperty {

	@FunctionalInterface
	public interface CanAcceptHandler<T extends ISkdProperty> {
		boolean canAccept(@Nonnull final T type);
	}

	@FunctionalInterface
	public interface AcceptHandler<T extends ISkdProperty> {
		@Nullable
		T accept(@Nonnull final T type);
	}

	private static final Collection<Class<?>> REGISTERED_PROPS = Lists.newArrayList();
	private static final Map<Class<? extends AbstractProperty>,
			Pair<CanAcceptHandler<? super ISkdProperty>,
						AcceptHandler<? super ISkdProperty>>> PAIRS =
			Maps.newHashMap();

	private final String key;
	private String value;

	protected AbstractProperty(@Nonnull final String name) {
		this.key = Preconditions.checkNotNull(name);
		this.value = null;

		REGISTERED_PROPS.add(this.getClass());
	}

	public static void register(@Nonnull final Class<? extends AbstractProperty> clazz,
	                            @Nonnull final CanAcceptHandler<? super ISkdProperty> cah,
	                            @Nonnull final AcceptHandler<? super ISkdProperty> ah) {
		PAIRS.put(clazz, Pair.of(cah, ah));
	}

	public static Collection<Class<?>> getProperties() {
		return ImmutableList.copyOf(REGISTERED_PROPS);
	}

	public static Map<Class<? extends AbstractProperty>,
			Pair<CanAcceptHandler<? super ISkdProperty>,
					AcceptHandler<? super ISkdProperty>>> getPairs() {
		return ImmutableMap.copyOf(PAIRS);
	}

	@Nonnull
	@Override
	public String getName() {
		return this.key;
	}

	@Nonnull
	@Override
	public Optional<String> getValue() {
		return Optional.ofNullable(this.value);
	}

	@Override
	public void setValue(@Nonnull final String value) {
		Preconditions.checkNotNull(value, "Use #removeValue() instead");
		if (value.isEmpty()) {
			this.removeValue();
		}
		this.value = value;
	}

	@Override
	public void removeValue() {
		this.value = null;
	}

	@Nonnull
	@Override
	public String toString() {
		String str = "";

		str += this.getName();
		str += "=\"";
		str += this.getValue().orElse("");
		str += "\"";

		return str;
	}

	/**
	 * Removes this tag from the list of properties.
	 *
	 * <p><strong>Calling this method will prevent your property from
	 * being queried for acceptance.</strong></p>
	 *
	 * @since 0.1
	 */
	protected void removeFromList() {
		final StackTraceElement[] it = new Exception().getStackTrace();
		if (!it[1].getClassName().contains("ParserExService")) {
			return;
		}
		REGISTERED_PROPS.remove(this.getClass());
	}
}
