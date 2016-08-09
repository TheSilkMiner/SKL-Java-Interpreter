package net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype;

import com.google.common.collect.Lists;

import net.thesilkminer.skl.interpreter.api.skd.SkdApi;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

/**
 * Holds all the various valid doctype declarations.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public final class DocTypes {

	private final List<IDocTypeProvider> providers;
	private static DocTypes singleton;

	private DocTypes() {

		this.providers = Lists.newArrayList();

		try {

			Class<?> clazz = Class.forName("net.thesilkminer.skl.interpreter."
					      + "implementation.skd.structure.providers.doctype."
					      + "DefaultProvider");
			this.providers.add((IDocTypeProvider) clazz.getConstructor().newInstance());
		} catch (final ReflectiveOperationException ex) {

			SkdApi.get().api().logger().severe("Implementation unavailable");
			ex.printStackTrace();
		}
	}

	/**
	 * Gets the unique instance of the doctype register.
	 *
	 * @return
	 * 		The unique instance of this class.
	 *
	 * @since 0.2
	 */
	public static DocTypes get() {

		if (singleton == null) {

			singleton = new DocTypes();
		}

		return singleton;
	}

	/**
	 * Adds a provider to the list of valid providers.
	 *
	 * <p>It checks if the addition can be performed, first.
	 * See {@link #validate}.</p>
	 *
	 * @param provider
	 * 		The provider to add.
	 * @return
	 * 		If the provider has been added successfully.
	 */
	@SuppressWarnings("UnusedReturnValue") //API method
	public boolean addProvider(final IDocTypeProvider provider) {

		return this.validate(provider) && this.providers.add(provider);
	}

	private boolean validate(final IDocTypeProvider provider) {

		return provider.name() != null
				&& !provider.name().isEmpty()
				&& provider.canUse()
				&& provider.docTypeUrl() != null;
	}

	/**
	 * Returns if the specified provider is valid and thus can
	 * be used.
	 *
	 * @param provider
	 * 		The provider to check.
	 * @return
	 * 		If it is valid.
	 *
	 * @since 0.2
	 */
	public boolean isProviderValid(final IDocTypeProvider provider) {

		return this.providers.contains(provider);
	}

	/**
	 * Gets the provider for the specified declaration.
	 *
	 * @param declaration
	 * 		The declaration.
	 * @return
	 * 		The provider for the declaration or
	 * 		{@link Optional#empty()} if none is
	 *    	available.
	 */
	public Optional<IDocTypeProvider> getProviderFor(final IDocTypeDeclaration declaration) {

		for (final IDocTypeProvider provider : this.providers) {

			try {

				if (provider.docTypeUrl().equals(declaration.getStyleSheet())
						      && this.isProviderValid(provider)) {

					return Optional.of(provider);
				}
			} catch (final MalformedURLException ex) {

				ex.printStackTrace();
			}
		}

		return Optional.empty();
	}
}
