package net.thesilkminer.skl.interpreter.api.skd.structure;

import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeDeclaration;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Represents a general structure for an SKD database.
 *
 * <p>The structure resembles the one for XML files.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public interface IStructure extends IAcceptable<IStructure> {

	/**
	 * Gets the current main tags of a database.
	 *
	 * <p>By "main tags", we mean the parent tags
	 * from where all the other tags are child.</p>
	 *
	 * @return
	 * 		The main tags of the database.
	 *
	 * @since 0.2
	 */
	@Nonnull
	List<ISkdTag> mainTags();

	/**
	 * Sets the current main tags of a database.
	 *
	 * <p>By "main tags", we mean the parent tags
	 * from where all the other tags are child.</p>
	 *
	 * @param tags
	 * 		The main tags of the database.
	 *
	 * @since 0.2
	 */
	void mainTags(@Nonnull final List<ISkdTag> tags);

	/**
	 * Gets if the specified declaration can be applied to the
	 * structure.
	 *
	 * @param declaration
	 * 		The declaration.
	 * @return
	 * 		If the declaration can be applied.
	 *
	 * @since 0.2
	 */
	boolean canApply(@Nonnull final IDocTypeDeclaration declaration);

	/**
	 * Applies the specified declaration.
	 *
	 * @param declaration
	 * 		The declaration to apply.
	 *
	 * @since 0.2
	 */
	void apply(@Nonnull final IDocTypeDeclaration declaration);
}
