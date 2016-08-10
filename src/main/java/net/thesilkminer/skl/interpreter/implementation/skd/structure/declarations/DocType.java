package net.thesilkminer.skl.interpreter.implementation.skd.structure.declarations;

import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.IDeclaration;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.DocTypes;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeDeclaration;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeProvider;

import org.jetbrains.annotations.Contract;

import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Represents the doctype declaration of an SKD database.
 *
 * <p>Generally the declaration is {@code <!DOCTYPE skd "stylesheet">}.
 * See {@link DocType#getDeclarationSyntax()} for more
 * information.</p>
 *
 * <p>A doctype declaration represents the style of a
 * database in language SKD. It must be always valid and
 * it is used by parsers to define specific set of rules.</p>
 *
 * <p>Valid doctype declaration can be found in
 * {@link DocTypes}
 * and must implement with
 * {@link IDocTypeProvider}</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class DocType implements IDocTypeDeclaration {

	private String docType;

	private DocType(@Nonnull final String docType) {
		this.setDocType(docType);
	}

	/**
	 * Gets a new doctype declaration.
	 *
	 * @param docType
	 * 		The doctype.
	 * @return
	 * 		A new declaration instance.
	 *
	 * @since 0.2
	 */
	@Contract(value = "null -> fail; !null -> !null", pure = true)
	@Nonnull
	public static IDocTypeDeclaration of(@Nonnull final String docType) {
		return new DocType(docType);
	}

	/**
	 * Gets a dummy instance of this declaration for use within parsers.
	 *
	 * @return
	 * 		A dummy instance of this declaration for use within parsers.
	 *
	 * @since 0.2
	 */
	@Contract(value = "-> !null", pure = true)
	@Nonnull
	public static IDeclaration dummy() {
		return new DocType("http://abc.def.net");
	}

	@Nonnull
	@Override
	public String getDocType() {
		return this.docType;
	}

	@Override
	public boolean setDocType(@Nonnull final String type) {
		final String backup = this.docType;
		this.docType = type;

		if (!this.validate()) {
			this.docType = backup;
			return false;
		}

		return true;
	}

	@Override
	public boolean validate() {
		final Optional<IDocTypeProvider> provider = DocTypes.get().getProviderFor(this);
		return provider.isPresent() && DocTypes.get().isProviderValid(provider.get());
	}

	@Nonnull
	@Override
	public String getDeclarationName() {
		return "!DOCTYPE";
	}

	@Nonnull
	@Override
	public String getDeclarationSyntax() {
		return String.format("%s skd <stylesheet>", this.getDeclarationName());
	}

	@Nonnull
	@Override
	public String toString() {
		return "<"
 				+ this.getDeclarationSyntax().replace("<stylesheet>",
						this.getDocType())
				+ ">";
	}
}
