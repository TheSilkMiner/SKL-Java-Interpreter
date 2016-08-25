package net.thesilkminer.skl.interpreter.api.skd.structure;

import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeDeclaration;

import java.util.List;
import java.util.Optional;
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

	/**
	 * Gets the non-null tag at the specified index.
	 *
	 * <p>In other words, gets the tag at the specified {@code index},
	 * not counting eventually {@code null} tags.</p>
	 *
	 * @implNote
	 *      By default, it is the same as calling
	 *      {@code this.mainTags().get(index)}
	 *
	 * @param index
	 *      The tag's index.
	 * @return
	 *      An {@link Optional} containing the {@code index}-th
	 *      non-{@code null} tag, if present.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	default Optional<ISkdTag> getIndexTagNonNull(final int index) {
		return Optional.ofNullable(this.mainTags().get(index));
	}

	/**
	 * Gets the amount of non-null main tags contained in this structure.
	 *
	 * @implNote
	 *      By default, it is the same as calling
	 *      {@code this.mainTags().size()}.
	 *
	 * @return
	 *      The amount of non-{@code null} main tags.
	 *
	 * @since 0.2.1
	 */
	default int nonNullSize() {
		return this.mainTags().size();
	}
}
