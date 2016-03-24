package net.thesilkminer.skl.interpreter.api.skd.structure.declarations;

/**
 * Represents a generic declaration.
 *
 * <p>This interface is not meant to be implemented, just extended.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public interface IDeclaration {

	/**
	 * Gets the name of the declaration.
	 *
	 * @return
	 * 		The declaration's name.
	 *
	 * @since 0.2
	 */
	String getDeclarationName();

	/**
	 * Gets the syntax of the declaration.
	 *
	 * @return
	 * 		The declaration's syntax.
	 *
	 * @since 0.2
	 */
	String getDeclarationSyntax();
}
