package net.thesilkminer.skl.interpreter.implementation.skd.structure.providers.doctype;

import net.thesilkminer.skl.interpreter.api.skd.structure.IStructure;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeProvider;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents the default provider for an SKD doctype.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
@SuppressWarnings("unused")
public class DefaultProvider implements IDocTypeProvider {

	/*
	public DefaultProvider() {

		DocTypes.get().addProvider(this);
	}
	*/

	@Override
	public String name() {

		return "default";
	}

	@Override
	public boolean canUse() {

		return true;
	}

	@Override
	public URL docTypeUrl() {

		try {

			return new URL("http://thesilkminer.net/sks/skd/default.skd");
		} catch (final MalformedURLException ex) {

			throw new RuntimeException("This should never happen", ex);
		}
	}

	@Override
	public boolean isStructureValidForProvider(final IStructure structure) {

		return true; //TODO
	}
}
