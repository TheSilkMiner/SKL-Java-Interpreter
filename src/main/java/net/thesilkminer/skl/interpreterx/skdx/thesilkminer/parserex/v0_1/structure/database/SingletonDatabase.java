package net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.database;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import net.thesilkminer.skl.interpreter.api.skd.SkdApi;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdTag;
import net.thesilkminer.skl.interpreter.api.skd.structure.IStructure;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeDeclaration;

import java.util.Collections;
import javax.annotation.Nonnull;

/**
 * Represents a database containing only one (valid) main tag.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public class SingletonDatabase extends AbstractDatabase {

	/**
	 * Constructs a new singleton database instance.
	 *
	 * @since 0.1
	 */
	public SingletonDatabase() {
		super.structure(SkdApi.get().api().structure(Lists.newArrayList()));
	}

	@Nonnull
	@Override
	public IStructure structure() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void structure(@Nonnull final IStructure structure) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canApplyDocType(@Nonnull final IDocTypeDeclaration declaration) {
		return super.structure().canApply(Preconditions.checkNotNull(declaration));
	}

	@Override
	public void apply(@Nonnull final IDocTypeDeclaration declaration) {
		super.structure().apply(Preconditions.checkNotNull(declaration));
	}

	@Nonnull
	@Override
	public String toString() {
		return super.docType().toString()
				+ "\n"
				+ super.version().toString()
				+ "\n\n"
				+ super.structure().toString();
	}

	/**
	 * Gets the main tag of this database.
	 *
	 * @return
	 *      This database's main tag.
	 *
	 * @since 0.1
	 */
	@Nonnull
	public ISkdTag mainTag() {
		return super.structure().getIndexTagNonNull(0).orElseThrow(RuntimeException::new);
	}

	/**
	 * Sets the main tag of this database.
	 *
	 * @param tag
	 *      The new tag.
	 *
	 * @since 0.1
	 */
	public void mainTag(@Nonnull final ISkdTag tag) {
		super.structure().mainTags(Collections.singletonList(tag));
	}
}
