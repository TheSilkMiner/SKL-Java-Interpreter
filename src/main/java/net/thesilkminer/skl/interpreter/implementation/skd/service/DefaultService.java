package net.thesilkminer.skl.interpreter.implementation.skd.service;

import com.google.common.base.Preconditions;

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
import net.thesilkminer.skl.interpreter.implementation.skd.DatabaseFile;
import net.thesilkminer.skl.interpreter.implementation.skd.SkdLogger;
import net.thesilkminer.skl.interpreter.implementation.skd.SkdParser;
import net.thesilkminer.skl.interpreter.implementation.skd.structure.Database;
import net.thesilkminer.skl.interpreter.implementation.skd.structure.SkdProperty;
import net.thesilkminer.skl.interpreter.implementation.skd.structure.SkdTag;
import net.thesilkminer.skl.interpreter.implementation.skd.structure.Structure;
import net.thesilkminer.skl.interpreter.implementation.skd.structure.declarations.DatabaseVersion;
import net.thesilkminer.skl.interpreter.implementation.skd.structure.declarations.DocType;

import java.io.File;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the default, basic service implementation.
 *
 * <p>This service is initialized by the API as soon as
 * possible.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2.1
 */
public class DefaultService implements ISkdService {

	private ISkdLogger logger;

	@Override
	public void init() {
		this.logger = SkdLogger.get();
	}

	@Override
	public void finalizeService() {

	}

	@Override
	public ISkdLogger logger() {
		return this.logger;
	}

	@Override
	public ISkdParser parser(@Nonnull final IDatabaseHolder databaseHolder) {
		return SkdParser.of(databaseHolder);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p><strong><center>DefaultService implementation</center></strong></p>
	 *
	 * <p>The only allowed type with this service is {@link File}.</p>
	 *
	 * @param object
	 *      The {@link Object} used to construct the database holder.
	 * @return
	 *      A new database holder.
	 *
	 * @since 0.2.1
	 */
	@Override
	public IDatabaseHolder databaseHolder(@Nonnull final Object object) {
		Preconditions.checkArgument(object instanceof File,
				"Default implementation only allows File object");
		return DatabaseFile.of((File) object);
	}

	@Override
	public IDatabase database(@Nonnull final IDocTypeDeclaration type,
	                          @Nonnull final IDatabaseVersionDeclaration version,
	                          @Nonnull final IStructure structure) {
		return Database.newDatabase(type, version, structure);
	}

	@Override
	public IDocTypeDeclaration doctype(@Nonnull final String type) {
		return DocType.of(type);
	}

	@Override
	public IDatabaseVersionDeclaration version(@Nullable final String version) {
		return version == null ? DatabaseVersion.get() : DatabaseVersion.get(version);
	}

	@Override
	public IStructure structure(@Nonnull final List<ISkdTag> main) {
		return Structure.newInstance(Optional.ofNullable(main.isEmpty() ? null : main));
	}

	@Override
	public ISkdTag tag(@Nonnull final String name) {
		return SkdTag.of(name);
	}

	@Override
	public ISkdProperty property(@Nonnull final String name, @Nonnull final Object value) {
		return SkdProperty.getProperty(name, Optional.of(value.toString()));
	}
}
