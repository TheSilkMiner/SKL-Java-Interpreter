package net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype;

import net.thesilkminer.skl.interpreter.api.skd.structure.IStructure;

import java.net.URL;

/**
 * Marks a provider of a valid doctype.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public interface IDocTypeProvider {

	/**
	 * Gets the name of the doctype.
	 *
	 * @return
	 * 		The name of the doctype.
	 *
	 * @since 0.2
	 */
	String name();

	/**
	 * Returns if the specified doctype declaration
	 * can be used.
	 *
	 * <p>This should always return {@code true}, unless
	 * it is no more valid (e.g. after updates) or still
	 * a WIP declaration.</p>
	 *
	 * @return
	 *		If the specified doctype declaration can be used.
	 *
	 * @since 0.2
	 */
	boolean canUse();

	/**
	 * Returns the url of the doctype.
	 *
	 * <p>It doesn't need to exist: it can be used as
	 * placeholders for a specific document structure.</p>
	 *
	 * @return
	 * 		The URL of the doctype.
	 *
	 * @since 0.2
	 */
	URL docTypeUrl();

	/**
	 * Returns if the specified structure is valid for the
	 * provider.
	 *
	 * @param structure
	 * 		The structure to check.
	 * @return
	 * 		If the specified structure is valid for the provider.
	 */
	boolean isStructureValidForProvider(final IStructure structure);
}
