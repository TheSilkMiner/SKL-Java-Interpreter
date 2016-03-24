package net.thesilkminer.skl.interpreter.implementation.skd.structure.declarations;

import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.DocTypes;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeDeclaration;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeProvider;

import java.util.Optional;

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

	@Override
	public String getDocType() {

		return this.docType;
	}

	@Override
	public boolean setDocType(final String type) {

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

	@Override
	public String getDeclarationName() {

		return "!DOCTYPE";
	}

	@Override
	public String getDeclarationSyntax() {

		return String.format("%s skd <stylesheet>", this.getDeclarationName());
	}
}