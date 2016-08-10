package net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype;

import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdTag;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.IDeclaration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Represents the DocType declaration of an SKD database.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public interface IDocTypeDeclaration extends IDeclaration {

	/**
	 * Gets the type of the document.
	 *
	 * @return
	 * 		The type of the document.
	 *
	 * @since 0.2
	 */
	@Nonnull
	String getDocType();

	/**
	 * Sets the type of the document.
	 *
	 * <p>This method should validate the document's type
	 * first and then try to add it to this document.</p>
	 *
	 * @param type
	 * 		The type of the document.
	 * @return
	 * 		If the type was set successfully.
	 *
	 * @since 0.2
	 */
	boolean setDocType(@Nonnull final String type);

	/**
	 * Validates this document type.
	 *
	 * @return
	 * 		If the document type is valid.
	 *
	 * @since 0.2
	 */
	boolean validate();

	/**
	 * Gets the stylesheet's URL of this document.
	 *
	 * @return
	 * 		The stylesheet's URL of this document.
	 * @throws MalformedURLException
	 * 		See {@link URL#URL(String)}.
	 *
	 * @since 0.2
	 */
	@Nonnull
	default URL getStyleSheet() throws MalformedURLException {
		return new URL(this.getDocType());
	}

	/**
	 * Applies the current doctype declaration and style sheet
	 * to the provided list of tags.
	 *
	 * @param tags
	 * 		The list of tags it should apply the style sheet to.
	 *
	 * @since 0.2
	 */
	default void apply(@Nonnull final List<ISkdTag> tags) {
		//TODO Implement for 0.3
	}
}
