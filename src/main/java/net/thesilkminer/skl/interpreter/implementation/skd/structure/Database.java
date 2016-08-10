package net.thesilkminer.skl.interpreter.implementation.skd.structure;

import com.google.common.base.Preconditions;

import net.thesilkminer.skl.interpreter.api.skd.structure.IDatabase;
import net.thesilkminer.skl.interpreter.api.skd.structure.IStructure;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeDeclaration;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.version.IDatabaseVersionDeclaration;

import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;

/**
 * Represents the full structure of an SKD database.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class Database implements IDatabase {

	private IDocTypeDeclaration docType;
	private IDatabaseVersionDeclaration version;
	private IStructure struct;

	private Database(@Nonnull final IDocTypeDeclaration docType,
	                 @Nonnull final IDatabaseVersionDeclaration version,
	                 @Nonnull final IStructure struct) {
		// Structure must be loaded before otherwise a NPE is thrown.
		this.structure(struct);

		this.docType(docType);
		this.version(version);
	}

	/**
	 * Gets a new database instance from the specified declarations.
	 *
	 * @param docType
	 * 		The doctype.
	 * @param version
	 * 		The database's version.
	 * @param struct
	 * 		The database's structure.
	 * @return
	 * 		A new instance with the specified parameters.
	 *
	 * @since 0.2
	 */
	@Contract(value = "!null, !null, !null -> !null; _, _, _ -> fail", pure = true)
	@Nonnull
	public static IDatabase newDatabase(@Nonnull final IDocTypeDeclaration docType,
	                                    @Nonnull final IDatabaseVersionDeclaration version,
	                                    @Nonnull final IStructure struct) {
		return new Database(docType, version, struct);
	}

	@Nonnull
	@Override
	public IDocTypeDeclaration docType() {
		return this.docType;
	}

	@Override
	public boolean docType(@Nonnull final IDocTypeDeclaration declaration) {
		Preconditions.checkNotNull(declaration,
				      "You cannot create a database without a doctype declaration");

		if (!declaration.validate()) {
			return false;
		}

		if (!this.canApplyDocType(declaration)) {
			return false;
		}

		try {
			this.apply(declaration);
			this.docType = declaration;
			return true;
		} catch (final RuntimeException ex) {
			return false;
		}
	}

	@Override
	public boolean canApplyDocType(@Nonnull final IDocTypeDeclaration declaration) {
		return this.structure().canApply(declaration);
	}

	@Override
	public void apply(@Nonnull final IDocTypeDeclaration declaration) {
		this.structure().apply(declaration);
	}

	@Nonnull
	@Override
	public IDatabaseVersionDeclaration version() {
		return this.version;
	}

	@Override
	public void version(@Nonnull final IDatabaseVersionDeclaration declaration) {
		this.version = declaration;
	}

	@Nonnull
	@Override
	public IStructure structure() {
		return this.struct;
	}

	@Override
	public void structure(@Nonnull final IStructure structure) {
		this.struct = structure;
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
}
