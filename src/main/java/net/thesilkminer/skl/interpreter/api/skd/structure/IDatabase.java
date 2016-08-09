package net.thesilkminer.skl.interpreter.api.skd.structure;

import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeDeclaration;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.version.IDatabaseVersionDeclaration;

/**
 * Represents the general structure of an SKD database.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public interface IDatabase extends IAcceptable<IDatabase> {

	/**
	 * Gets the current doctype declaration.
	 *
	 * @return
	 * 		The current doctype declaration.
	 *
	 * @since 0.2
	 */
	IDocTypeDeclaration docType();

	/**
	 * Sets the current doctype definition.
	 *
	 * <p>Every implemented method should check if the
	 * specified doctype declaration is valid before
	 * applying.</p>
	 *
	 * <p>If the doctype is valid, the implementation
	 * should check if the declaration can be applied
	 * to this specific database. This can be and
	 * should be performed by calling the
	 * {@link #canApplyDocType(IDocTypeDeclaration)}
	 * method.</p>
	 *
	 * <p>If the doctype can be applied, the implementation
	 * should apply the doctype by calling the
	 * {@link #apply(IDocTypeDeclaration)} method.</p>
	 *
	 * <p>Only if all these conditions are met, the method can
	 * return {@code true}.</p>
	 *
	 * @param declaration
	 * 		The new doctype definition.
	 * @return
	 * 		If the addition was successful ({@code true}) or
	 *	    not ({@code false}).
	 *
	 * @since 0.2
	 */
	boolean docType(final IDocTypeDeclaration declaration);

	/**
	 * Gets if the specified doctype declaration can be applied.
	 *
	 * @param declaration
	 * 		The doctype declaration.
	 * @return
	 * 		If it can be applied.
	 *
	 * @since 0.2
	 */
	boolean canApplyDocType(final IDocTypeDeclaration declaration);

	/**
	 * Applies the declaration.
	 *
	 * @param declaration
	 *		The declaration to apply.
	 *
	 * @since 0.2
	 */
	void apply(final IDocTypeDeclaration declaration);

	/**
	 * Gets the declaration of this database's version.
	 *
	 * @return
	 * 		The declaration of this database's version.
	 *
	 * @since 0.2
	 */
	IDatabaseVersionDeclaration version();

	/**
	 * Sets the specified version as the database's version.
	 *
	 * @param declaration
	 * 		The new declaration.
	 *
	 * @since 0.2
	 */
	void version(final IDatabaseVersionDeclaration declaration);

	/**
	 * Gets the tags' and properties' structure of this database.
	 *
	 * @return
	 * 		The tags' and properties' structure of this database.
	 *
	 * @since 0.2
	 */
	IStructure structure();

	/**
	 * Sets the tags' and properties' structure of this database.
	 *
	 * @param structure
	 * 		The tags' and properties' structure of this database.
	 *
	 * @since 0.2
	 */
	void structure(final IStructure structure);
}
