package net.thesilkminer.skl.interpreter.implementation.skd.structure;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdTag;
import net.thesilkminer.skl.interpreter.api.skd.structure.IStructure;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeDeclaration;

import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Represents the structure of the database.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class Structure implements IStructure {

	private List<ISkdTag> parents = Lists.newArrayList();

	private Structure(final List<ISkdTag> parents) {
		this.parents = parents;
	}

	/**
	 * Constructs a new structure out of the specified tags.
	 *
	 * @param tags
	 * 		The tags.
	 * @return
	 * 		A new structure instance.
	 *
	 * @deprecated Use {@link #newInstance(List)} instead.
	 *             To reproduce the behaviour of this method,
	 *             replace a call made with the argument
	 *             {@link Optional#empty()} with one with
	 *             {@link Lists#newArrayList() an empty list}.
	 *
	 * @since 0.2
	 */
	@Contract(value = "null -> fail; !null -> !null", pure = true)
	@Deprecated
	@Nonnull
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static IStructure newInstance(@Nonnull final Optional<List<ISkdTag>> tags) {
		return newInstance(tags.orElse(Lists.newArrayList()));
	}

	/**
	 * Constructs a new structure out of the specified tags.
	 *
	 * @param tags
	 *      The tags.
	 * @return
	 *      A new structure instance.
	 *
	 * @since 0.2
	 */
	@Contract(value = "null -> fail; !null -> !null", pure = true)
	@Nonnull
	public static IStructure newInstance(@Nonnull final List<ISkdTag> tags) {
		return new Structure(Preconditions.checkNotNull(tags, "tags"));
	}

	@Nonnull
	@Override
	public List<ISkdTag> mainTags() {
		return this.parents;
	}

	@Override
	public void mainTags(@Nonnull final List<ISkdTag> tags) {
		this.parents = tags;
	}

	@Override
	public boolean canApply(@Nonnull final IDocTypeDeclaration declaration) {
		return declaration.validate();
	}

	@Override
	public void apply(@Nonnull final IDocTypeDeclaration declaration) {
		declaration.apply(this.mainTags());
	}

	@Nonnull
	@Override
	public String toString() {
		String toString = "";

		for (final ISkdTag tag : this.mainTags()) {
			toString += tag.toString();
			toString += "\n";
		}

		return toString;
	}
}
