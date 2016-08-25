package net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.database;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.thesilkminer.skl.interpreter.api.skd.SkdApi;
import net.thesilkminer.skl.interpreter.api.skd.structure.IDatabase;
import net.thesilkminer.skl.interpreter.api.skd.structure.IStructure;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeDeclaration;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.version.IDatabaseVersionDeclaration;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Main class extended by all ParserEx databases, containing some
 * default implementations and other useful methods.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public abstract class AbstractDatabase implements IDatabase {

	@FunctionalInterface
	public interface CanAcceptHandler<T extends IDatabase> {
		boolean canAccept(@Nonnull final T type);
	}

	@FunctionalInterface
	public interface AcceptHandler<T extends IDatabase> {
		@Nullable
		T accept(@Nonnull final T type);
	}

	private static final Collection<Class<?>> REGISTERED_DBS = Lists.newArrayList();
	private static final Map<Class<? extends AbstractDatabase>,
				Pair<CanAcceptHandler<? super IDatabase>,
						AcceptHandler<? super IDatabase>>> PAIRS =
			Maps.newHashMap();

	private IDocTypeDeclaration declaration;
	private IDatabaseVersionDeclaration version;
	private IStructure structure;

	protected AbstractDatabase() {
		this.declaration = null;
		this.version = SkdApi.get().api().version(null);
		this.structure = null;

		REGISTERED_DBS.add(this.getClass());
	}

	public static void register(@Nonnull final Class<? extends AbstractDatabase> clazz,
	                            @Nonnull final CanAcceptHandler<? super IDatabase> cah,
	                            @Nonnull final AcceptHandler<? super IDatabase> ah) {
		PAIRS.put(clazz, Pair.of(cah, ah));
	}

	public static Collection<Class<?>> getDatabases() {
		return ImmutableList.copyOf(REGISTERED_DBS);
	}

	public static Map<Class<? extends AbstractDatabase>,
			Pair<CanAcceptHandler<? super IDatabase>,
					AcceptHandler<? super IDatabase>>> getPairs() {
		return ImmutableMap.copyOf(PAIRS);
	}

	@Nonnull
	@Override
	public IDocTypeDeclaration docType() {
		return this.declaration;
	}

	@Override
	public boolean docType(@Nonnull final IDocTypeDeclaration declaration) {
		Preconditions.checkNotNull(declaration);

		if (!declaration.validate()) {
			return false;
		}

		if (!this.canApplyDocType(declaration)) {
			return false;
		}

		try {
			this.apply(declaration);
			this.declaration = declaration;
			return true;
		} catch (final RuntimeException exception) {
			return false;
		}
	}

	@Override
	public boolean canApplyDocType(@Nonnull final IDocTypeDeclaration declaration) {
		return this.structure().canApply(Preconditions.checkNotNull(declaration));
	}

	@Override
	public void apply(@Nonnull final IDocTypeDeclaration declaration) {
		this.structure().apply(Preconditions.checkNotNull(declaration));
	}

	@Nonnull
	@Override
	public IDatabaseVersionDeclaration version() {
		return this.version;
	}

	@Override
	public void version(@Nonnull final IDatabaseVersionDeclaration version) {
		this.version = Preconditions.checkNotNull(version);
	}

	@Nonnull
	@Override
	public IStructure structure() {
		return this.structure;
	}

	@Override
	public void structure(@Nonnull final IStructure structure) {
		this.structure = Preconditions.checkNotNull(structure);
	}

	@Nonnull
	@Override
	public String toString() {
		return this.docType().toString()
				+ "\n"
				+ this.version().toString()
				+ "\n\n"
				+ this.structure().toString();
	}

	/**
	 * Removes this tag from the list of databases.
	 *
	 * <p><strong>Calling this method will prevent your database from
	 * being queried for acceptance.</strong></p>
	 *
	 * @since 0.1
	 */
	protected void removeFromList() {
		final StackTraceElement[] it = new Exception().getStackTrace();
		if (!it[1].getClassName().contains("ParserExService")) {
			return;
		}
		REGISTERED_DBS.remove(this.getClass());
	}
}
