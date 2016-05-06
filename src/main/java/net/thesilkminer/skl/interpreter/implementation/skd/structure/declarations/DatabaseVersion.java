package net.thesilkminer.skl.interpreter.implementation.skd.structure.declarations;

import com.google.common.base.Preconditions;

import net.thesilkminer.skl.interpreter.api.skd.parser.ISkdParser;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.IDeclaration;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.version.IDatabaseVersionDeclaration;

/**
 * Represents the version declaration of an SKD database.
 *
 * <p>Generally the version declaration is
 * {@code <!SKD version=&gt;version&lt;>}. See
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

	private DatabaseVersion() {

		this.version = ISkdParser.CURRENT_VERSION;
	}

	/**
	 * Gets a new version declaration instance with the current version
	 * as the default version.
	 *
	 * @return
	 * 		A new instance.
	 *
	 * @since 0.2
	 */
	public static IDatabaseVersionDeclaration get() {

		return new DatabaseVersion();
	}

	/**
	 * Gets a new version declaration instance with the specified version
	 * as the default version.
	 *
	 * @param version
	 * 		The version.
	 * @return
	 * 		A new instance.
	 *
	 * @since 0.2
	 */
	public static IDatabaseVersionDeclaration get(final String version) {

		final IDatabaseVersionDeclaration d = DatabaseVersion.get();
		d.version(version);
		return d;
	}

	/**
	 * Gets a dummy instance of this declaration for use within parsers.
	 *
	 * @return
	 * 		A dummy instance of this declaration for use within parsers.
	 *
	 * @since 0.2
	 */
	public static IDeclaration dummy() {

		return get();
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

		return String.format("%s version <version>", this.getDeclarationName());
	}

	@Override
	public String toString() {

		return "<"
				+ this.getDeclarationSyntax().replace("<version>",
						this.version())
				+ ">";
	}
}
