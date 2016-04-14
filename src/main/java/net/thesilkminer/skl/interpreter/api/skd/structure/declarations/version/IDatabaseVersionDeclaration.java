package net.thesilkminer.skl.interpreter.api.skd.structure.declarations.version;

import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.IDeclaration;

/**
 * Represents the Version declaration of a database.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public interface IDatabaseVersionDeclaration extends IDeclaration {

	/**
	 * Gets the version specified by this declaration.
	 *
	 * @return
	 * 		The version specified by this declaration.
	 *
	 * @since 0.2
	 */
	String version();

	/**
	 * Sets the version of this document.
	 *
	 * <p>This version must <b>NOT</b> be validated against
	 * a certain set of rules: every parser may have a
	 * different one.</p>
	 *
	 * <p>The version value must not be {@code null}, though:
	 * in this case the method should either fail fast (in
	 * which case it should declare it in the documentation)
	 * or attempt to recover by using the previous value as
	 * version. If the previous value is {@code null}, then
	 * the method should completely fail.</p>
	 *
	 * @param version
	 * 		The new document version.
	 *
	 * @since 0.2
	 */
	void version(final String version);
}
