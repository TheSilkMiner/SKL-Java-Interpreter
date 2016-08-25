package net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.service.acceptance;

import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdProperty;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.service.AcceptanceService;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.property.AbstractProperty;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.property.MarkerProperty;

import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Manages the acceptance of various properties.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public class PropertiesAcceptanceService extends AcceptanceService<ISkdProperty> {

	private Class<? extends AbstractProperty> acceptProperties;

	@Override
	public boolean canAccept(@Nonnull final ISkdProperty type) {
		this.acceptProperties = null;
		AbstractProperty.getPairs().entrySet().stream()
				.forEach(it -> {
					if (it.getValue().getKey().canAccept(type)) {
						this.acceptProperties = it.getKey();
					}
				});
		return this.acceptProperties != null;
	}

	@Nonnull
	@Override
	public ISkdProperty accept(@Nonnull final ISkdProperty type) {
		if (this.acceptProperties == null) {
			throw new RuntimeException();
		}
		final ISkdProperty prop = Optional.ofNullable(
				AbstractProperty.getPairs().get(this.acceptProperties)
				.getValue().accept(type)
		).orElseThrow(RuntimeException::new);
		this.acceptProperties = null;
		return prop;
	}

	@Override
	public void init() {
		AbstractProperty.register(MarkerProperty.class, it -> {
			final String name = it.getName();
			if (name.isEmpty()) {
				return false;
			}
			final Optional<String> optValue = it.getValue();
			return !optValue.isPresent();
		}, it -> new MarkerProperty(it.getName()));
	}
}
