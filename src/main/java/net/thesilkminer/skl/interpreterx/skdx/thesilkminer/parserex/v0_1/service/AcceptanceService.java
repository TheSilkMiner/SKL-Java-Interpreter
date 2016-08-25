package net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.service;

import net.thesilkminer.skl.interpreter.api.skd.holder.IDatabaseHolder;
import net.thesilkminer.skl.interpreter.api.skd.logging.ISkdLogger;
import net.thesilkminer.skl.interpreter.api.skd.parser.ISkdParser;
import net.thesilkminer.skl.interpreter.api.skd.service.ISkdService;
import net.thesilkminer.skl.interpreter.api.skd.structure.IDatabase;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdProperty;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdTag;
import net.thesilkminer.skl.interpreter.api.skd.structure.IStructure;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeDeclaration;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.version.IDatabaseVersionDeclaration;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Service used to manage the acceptance of various
 * tags and properties from a central location.
 *
 * @param <T>
 *     The type that should be accepted by this acceptance
 *     service instance.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
@SuppressWarnings("ConstantConditions") // Don't implement useless stuff
public abstract class AcceptanceService<T> implements ISkdService {

	public abstract boolean canAccept(@Nonnull final T type);

	@Nonnull
	public abstract T accept(@Nonnull final T type);

	/* ======== BOILERPLATE CODE ======== */

	@Override
	public void init() {}

	@Override
	public void finalizeService() {}

	@Nonnull
	@Override
	public ISkdLogger logger() {
		return null;
	}

	@Nonnull
	@Override
	public ISkdParser parser(@Nonnull final IDatabaseHolder databaseHolder) {
		return null;
	}

	@Nonnull
	@Override
	public IDatabaseHolder databaseHolder(@Nonnull final Object object) {
		return null;
	}

	@Nonnull
	@Override
	public IDatabase database(@Nonnull final IDocTypeDeclaration type,
	                          @Nonnull final IDatabaseVersionDeclaration version,
	                          @Nonnull final IStructure structure) {
		return null;
	}

	@Nonnull
	@Override
	public IDocTypeDeclaration doctype(@Nonnull final String type) {
		return null;
	}

	@Nonnull
	@Override
	public IDatabaseVersionDeclaration version(@Nullable final String version) {
		return null;
	}

	@Nonnull
	@Override
	public IStructure structure(@Nonnull final List<ISkdTag> main) {
		return null;
	}

	@Nonnull
	@Override
	public ISkdTag tag(@Nonnull final String name) {
		return null;
	}

	@Nonnull
	@Override
	public ISkdProperty property(@Nonnull final String name, @Nonnull final Object value) {
		return null;
	}
}
