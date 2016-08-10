package net.thesilkminer.skl.interpreter.api.skd.service;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;

import net.thesilkminer.skl.interpreter.api.skd.holder.IDatabaseHolder;
import net.thesilkminer.skl.interpreter.api.skd.logging.ISkdLogger;
import net.thesilkminer.skl.interpreter.api.skd.parser.ISkdParser;
import net.thesilkminer.skl.interpreter.api.skd.structure.IDatabase;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdProperty;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdTag;
import net.thesilkminer.skl.interpreter.api.skd.structure.IStructure;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeDeclaration;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.version.IDatabaseVersionDeclaration;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the service used to implement the API.
 *
 * <p>Services may be loaded and substituted by other
 * services implementations.</p>
 *
 * <p>On loading, the method {@link #init()} is
 * called, while, when substituted, the service is
 * finalized with the method {@link #finalizeService()}.</p>
 *
 * <p>The service's constructor must NOT have any
 * parameters.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2.1
 */
public interface ISkdService {

	/**
	 * Initializes the service.
	 *
	 * @implNote Place everything related initialization in
	 *      here, not in the constructor.
	 *
	 * @since 0.2.1
	 */
	void init();

	/**
	 * Obtains the various information from the previous listener.
	 *
	 * <p>The listener passed to this can be {@code null} if this
	 * is the first service registered.</p>
	 *
	 * <p>The listener is always unloaded before calling this
	 * method. Also, the listener must be cast to the specified
	 * type through reflection.</p>
	 *
	 * @param service
	 *      The old service, unloaded.
	 * @param serviceClass
	 *      The type of the service.
	 *
	 * @since 0.2.1
	 */
	default void grabInfo(@Nullable final ISkdService service,
	                      @Nullable final Class<? extends ISkdService> serviceClass) {}

	/**
	 * Gets if the service is currently in a usable state.
	 *
	 * @return
	 *      If the service is in a usable state.
	 *
	 * @since 0.2.1
	 */
	default boolean isUsable() {
		return true;
	}

	/**
	 * Used to allow cleanup of this specific service, when
	 * substituted by other services.
	 *
	 * @since 0.2.1
	 */
	void finalizeService();

	/**
	 * Obtains a logger instance for this specific implementation.
	 *
	 * <p>The logger returned must be the same object for every
	 * call. Different instances are not allowed.</p>
	 *
	 * @return
	 *      A logger instance.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	ISkdLogger logger();

	/**
	 * Gets a new instance of the {@link ISkdParser parser} for the specified
	 * database holder.
	 *
	 * <p>Refer to the parser specific Javadoc for more information.</p>
	 *
	 * @param databaseHolder
	 *      The {@link IDatabaseHolder} object to pass to the parser.
	 * @return
	 *      A new parser instance.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	ISkdParser parser(@Nonnull final IDatabaseHolder databaseHolder);

	/**
	 * Gets a new {@link IDatabaseHolder database holder} for the specified object.
	 *
	 * <p>Refer to the service-specific Javadoc for more information
	 * regarding allowed object for the specified database holder.</p>
	 *
	 * @param object
	 *      The {@link Object} used to construct the database holder.
	 * @return
	 *      A new database holder.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	IDatabaseHolder databaseHolder(@Nonnull final Object object); //TODO Generify

	/**
	 * Gets a new {@link IDatabase database} instance, given the specific
	 * items.
	 *
	 * @param type
	 *      The {@link IDocTypeDeclaration doctype declaration} of the database.
	 * @param version
	 *      The {@link IDatabaseVersionDeclaration version} of the database.
	 * @param structure
	 *      The {@link IStructure structure} of the database.
	 * @return
	 *      A new database.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	IDatabase database(@Nonnull final IDocTypeDeclaration type,
	                   @Nonnull final IDatabaseVersionDeclaration version,
	                   @Nonnull final IStructure structure);

	/**
	 * Callback used to allow the service to manipulate the
	 * database and/or substitute it before returning.
	 *
	 * @param database
	 *      The original database.
	 * @return
	 *      The edited database or a completely new instance.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	default IDatabase databaseCallback(@Nonnull final IDatabase database) {
		return database;
	}

	/**
	 * Obtains a new {@link IDocTypeDeclaration doctype declaration} given the
	 * specified type.
	 *
	 * @param type
	 *      The doctype's type.
	 * @return
	 *      A new doctype declaration.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	IDocTypeDeclaration doctype(@Nonnull final String type);

	/**
	 * Obtains a {@link IDatabaseVersionDeclaration version declaration} for
	 * a database.
	 *
	 * @param version
	 *      The database's version.
	 * @return
	 *      A new version declaration.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	IDatabaseVersionDeclaration version(@Nullable final String version);

	/**
	 * Obtains a {@link IStructure structure} for the specified
	 * {@link List list} of {@link ISkdTag tags}.
	 *
	 * @param main
	 *      The list of main tags.
	 * @return
	 *      A new structure instance.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	IStructure structure(@Nonnull final List<ISkdTag> main);

	/**
	 * Gets an {@link ISkdTag tag} with the specified name.
	 *
	 * @param name
	 *      The name of the tag.
	 * @return
	 *      A new tag.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	ISkdTag tag(@Nonnull final String name);

	/**
	 * Callback used to allow the service to manipulate the
	 * tag and/or substitute it before addition.
	 *
	 * <p>This callback is called <b>ONLY</b> when a tag is
	 * closed.</p>
	 *
	 * @param tag
	 *      The original, just parsed tag.
	 * @return
	 *      The modified tag or a completely new {@link ISkdTag}
	 *      instance.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	default ISkdTag tagCallback(@Nonnull final ISkdTag tag) {
		return tag;
	}

	/**
	 * Gets a new {@link ISkdProperty property} with the specified
	 * name and value.
	 *
	 * <p>The value is an {@link Object}, but it should provide some
	 * method of serialization.</p>
	 *
	 * @param name
	 *      The property's name.
	 * @param value
	 *      The property's value.
	 * @return
	 *      A new property instance.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	ISkdProperty property(@Nonnull final String name, @Nonnull final Object value);

	/**
	 * Callback used to allow the service to manipulate the
	 * property and/or substitute it before addition.
	 *
	 * @param property
	 *      The original property.
	 * @return
	 *      The modified property or a completely new
	 *      {@link ISkdProperty} instance.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	default ISkdProperty propertyCallback(@Nonnull final ISkdProperty property) {
		return property;
	}

	/**
	 * Gets the eventual additional types that this service can handle.
	 *
	 * <p>E.g., if this service can handle a special type of {@link ISkdTag},
	 * say {@code ListTag}, the service will return a {@link Multimap}
	 * containing an entry with the key {@code ISkdTag.class} and as a
	 * value, by default, a {@link List}, containing as a single entry
	 * {@code ListTag.class}.</p>
	 *
	 * <p>All the values for the specific type are asked by the parser if
	 * they
	 * {@link net.thesilkminer.skl.interpreter.api.skd.structure.IAcceptable#canAccept(Object)
	 * can accept the type} and
	 * then, if they allow the specific type to be applied, they
	 * {@link net.thesilkminer.skl.interpreter.api.skd.structure.IAcceptable#accept(Object)
	 * accept the tag}.</p>
	 *
	 * @return
	 *      A Multimap containing the various mapping types.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	default Multimap<Class<?>, Class<?>> additionalTypes() {
		return ImmutableListMultimap.of();
	}
}
