package net.thesilkminer.skl.interpreter.implementation.skd.structure;

import com.google.common.collect.Lists;

import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdTag;
import net.thesilkminer.skl.interpreter.api.skd.structure.IStructure;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeDeclaration;

import java.util.List;
import java.util.Optional;

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
	 * @since 0.2
	 */
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static IStructure newInstance(final Optional<List<ISkdTag>> tags) {

		final List<ISkdTag> parents = tags.isPresent() ? tags.get() : Lists.newArrayList();

		return new Structure(parents);
	}

	@Override
	public List<ISkdTag> mainTags() {

		return this.parents;
	}

	@Override
	public void mainTags(final List<ISkdTag> tags) {

		this.parents = tags;
	}

	@Override
	public boolean canApply(final IDocTypeDeclaration declaration) {

		return declaration.validate();
	}

	@Override
	public void apply(final IDocTypeDeclaration declaration) {

		declaration.apply(this.mainTags());
	}
}
