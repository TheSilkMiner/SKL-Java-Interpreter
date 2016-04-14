package net.thesilkminer.skl.interpreter.implementation.skd.structure.declarations;

import com.google.common.base.Preconditions;

import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.version.IDatabaseVersionDeclaration;

/**
 * Represents the version declaration of an SKD database.
 *
 * <p>Generally the version declaration is
 * {@code <!SKD version="&gt;version&lt;">}. See
 * {@link DatabaseVersion#getDeclarationSyntax()} for more
 * information.</p>
 *
 * <p>This declaration is used by the parser to identify if
 * the specified database can be currently parsed (aka not
 * too old or newer).</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class DatabaseVersion implements IDatabaseVersionDeclaration {

	private String version;

	public DatabaseVersion() {

		//this.version = SkdApi.get().parser().CURRENT_VERSION;
	}

	@Override
	public String version() {

		return this.version;
	}

	@Override
	public void version(final String version) {

		Preconditions.checkNotNull(version, "New version must not be null");

		this.version = version;
	}

	@Override
	public String getDeclarationName() {

		return "SKD";
	}

	@Override
	public String getDeclarationSyntax() {

		return String.format("%s version=\"<version>\"", this.getDeclarationName());
	}
}
