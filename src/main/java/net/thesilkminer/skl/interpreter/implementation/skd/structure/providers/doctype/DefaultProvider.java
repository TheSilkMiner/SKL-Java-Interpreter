package net.thesilkminer.skl.interpreter.implementation.skd.structure.providers.doctype;

import net.thesilkminer.skl.interpreter.api.skd.structure.IStructure;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeProvider;

import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.Nonnull;

/**
 * Represents the default provider for an SKD doctype.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class DefaultProvider implements IDocTypeProvider {

	@Nonnull
	@Override
	public String name() {
		return "default";
	}

	@Override
	public boolean canUse() {
		return true;
	}

	@Nonnull
	@Override
	public URL docTypeUrl() {
		try {
			return new URL("http://thesilkminer.net/sks/skd/default.skd");
		} catch (final MalformedURLException ex) {
			throw new RuntimeException("This should never happen", ex);
		}
	}

	@Override
	public boolean isStructureValidForProvider(@Nonnull final IStructure structure) {
		return true; //TODO
	}
}
