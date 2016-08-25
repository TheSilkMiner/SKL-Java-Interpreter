package net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.service.acceptance;

import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdProperty;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdTag;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.service.AcceptanceService;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.tag.AbstractTag;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.tag.ListTag;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.tag.PairTag;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.tag.ValueTag;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Manages the acceptance of various tags.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public class TagAcceptanceService extends AcceptanceService<ISkdTag> {

	private Class<? extends AbstractTag> acceptTagService;

	@Override
	public boolean canAccept(@Nonnull final ISkdTag type) {
		this.acceptTagService = null;

		if (!type.closed()) {
			throw new IllegalStateException(
					new IllegalArgumentException(
							"Attempted to accept non-closed tag "
									+ type
									.toString()
									.replace("\n", "")
					)
			);
		}

		AbstractTag.getPairs().entrySet().stream()
				.forEach(it -> {
					if (it.getValue().getKey().canAccept(type)) {
						this.acceptTagService = it.getKey();
					}
				});
		return this.acceptTagService != null;
	}

	@Nonnull
	@Override
	public ISkdTag accept(@Nonnull final ISkdTag type) {
		if (this.acceptTagService == null) {
			throw new RuntimeException();
		}
		final ISkdTag ret = Optional.ofNullable(
				AbstractTag.getPairs().get(this.acceptTagService).getValue()
						.accept(type)
		).orElseThrow(RuntimeException::new);
		this.acceptTagService = null;
		return ret;
	}

	@Override
	public void init() {
		AbstractTag.register(ListTag.class, type -> {
			if (type.getContent().isPresent()) {
				return false;
			}
			if (type.getChildren().isEmpty()) {
				return false;
			}
			if (type.getChildren().size() == 1) {
				// Custom logic for single children tags
				return type.getChildren().get(0).getName().equals(
						type.getName().concat("s")
				);
			}
			final String childNames = type.getChildren().get(0).getName();
			for (final ISkdTag child : type.getChildren()) {
				if (!child.getName().equals(childNames)) {
					return false;
				}
			}
			return true;
		}, type -> {
				final String name = type.getName();
				final String childName = type.getChildren().get(0).getName();
				final ListTag list = new ListTag(name, childName);
				type.getChildren().stream().forEach(list::addChildTag);
				type.getProperties().stream().forEach(list::addProperty);
				return list;
			});

		AbstractTag.register(PairTag.class, type -> {
			if (type.getContent().isPresent()) {
				return false;
			}
			if (!type.getChildren().isEmpty()) {
				return false;
			}
			final List<ISkdProperty> properties = type.getProperties();
			if (properties.size() != 2) {
				return false;
			}
			final ISkdProperty propertyLeft = properties.get(0);
			final ISkdProperty propertyRight = properties.get(1);
			return propertyLeft != null
					&& propertyRight != null
					&& "left".equals(propertyLeft.getName())
					&& "right".equals(propertyRight.getName());
		}, type -> {
				final List<ISkdProperty> properties = type.getProperties();
				final String leftValue = properties.get(0).getValue().orElse(null);
				final String rightValue = properties.get(1).getValue().orElse(null);
				final PairTag part = new PairTag(type.getName());
				part.setLeft(leftValue);
				part.setRight(rightValue);
				return part;
			}
		);

		AbstractTag.register(ValueTag.class, type -> {
			if (type.getContent().isPresent()) {
				return false;
			}
			if (!type.getChildren().isEmpty()) {
				return false;
			}
			final List<ISkdProperty> properties = type.getProperties();
			if (properties.size() != 1) {
				return false;
			}
			final ISkdProperty property = properties.get(0);
			return property != null
					&& "value".equals(property.getName());
		}, type -> {
				final List<ISkdProperty> properties = type.getProperties();
				final String value = properties.get(0).getValue().orElse(null);
				final ValueTag tag = new ValueTag(type.getName());
				tag.setValue(value);
				return tag;
			});
	}
}
