package net.thesilkminer.skl.interpreter.api.skd;

/*import net.thesilkminer.skl.interpreter.api.skd.holder.IDatabaseHolder;
import net.thesilkminer.skl.interpreter.api.skd.logging.ISkdLogger;
import net.thesilkminer.skl.interpreter.api.skd.parser.ISkdParser;*/
import net.thesilkminer.skl.interpreter.api.skd.service.ISkdService;
import net.thesilkminer.skl.interpreter.api.skd.service.ServiceManager;
/*import net.thesilkminer.skl.interpreter.api.skd.structure.IDatabase;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdProperty;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdTag;
import net.thesilkminer.skl.interpreter.api.skd.structure.IStructure;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeDeclaration;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.version.IDatabaseVersionDeclaration;*/

/*import java.io.File;
import java.lang.reflect.Method;
import java.util.List;*/

import java.util.Optional;
import javax.annotation.Nonnull;
/*import javax.annotation.Nullable;*/

/**
 * Represents the access point of the SKD Api.
 *
 * <p>Every call to the API must be performed from here.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class SkdApi {

	private static final SkdApi SINGLETON = new SkdApi();

	/*private ISkdLogger logger;*/

	private SkdApi() {
		try {
			final Class<?> def = Class
					.forName("net.thesilkminer.skl.interpreter."
							+ "implementation.skd.service."
							+ "DefaultService");
			final ServiceManager manager = ServiceManager.get();
			final ISkdService service = ISkdService.class.cast(def.newInstance());
			manager.provide(SkdApi.class, service);
		} catch (final ReflectiveOperationException ignored) {
			// We don't really care
		}
	}

	/**
	 * Gets the singleton instance of the API.
	 *
	 * @return
	 * 		The singleton instance of the API.
	 *
	 * @since 0.2
	 */
	public static SkdApi get() {

		return SINGLETON;
	}

	/**
	 * Gets a service providing the API.
	 *
	 * @return
	 *      The currently registered.
	 *
	 * @throws RuntimeException If there is no available service.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	public ISkdService api() {
		final Optional<ISkdService> service = this.serviceManager().get(SkdApi.class);
		if (!service.isPresent()) {
			throw new RuntimeException("No available service found");
		}
		return service.get();
	}

	/**
	 * Gets the service manager.
	 *
	 * @return
	 *      The service manager
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	@SuppressWarnings("WeakerAccess")
	public ServiceManager serviceManager() {
		return ServiceManager.get();
	}

	/**
	 * Gets the logger instance of the current implementation.
	 *
	 * @return
	 * 		The logger instance of the current implementation.
	 *
	 * @since 0.2
	 */
	/*
	public ISkdLogger logger() {

		if (this.logger != null) {

			return this.logger;
		}

		try {

			final Class<?> loggerClass = Class.forName("net.thesilkminer.skl."
					      + "interpreter.implementation.skd.SkdLogger");

			final Method get = loggerClass.getDeclaredMethod("get");

			ISkdLogger result = (ISkdLogger) get.invoke(null);
			this.logger = result;
			return result;
		} catch (final ReflectiveOperationException ex) {

			throw new RuntimeException(ex);
		}
	}
	*/
	/**
	 * Gets the parser instance of the current implementation.
	 *
	 * @param holder
	 * 		The IDatabaseHolder you need to create the parser for.
	 * @return
	 * 		The parser instance of the current implementation.
	 *
	 * @since 0.2
	 */
	/*
	public ISkdParser parser(@Nonnull final IDatabaseHolder holder) {

		try {

			final Class<?> parserClass = Class.forName("net.thesilkminer.skl."
					      + "interpreter.implementation.skd.SkdParser");

			final Method of = parserClass.getDeclaredMethod("of",
					                      IDatabaseHolder.class);

			return (ISkdParser) of.invoke(null, holder);
		} catch (final ReflectiveOperationException ex) {

			throw new RuntimeException(ex);
		}
	}
	*/
	/**
	 * Gets a new database holder of the current implementation.
	 *
	 * @param file
	 * 		The file you need to create the holder for.
	 * @return
	 * 		The holder instance of the current implementation.
	 *
	 * @since 0.2
	 */
	/*
	public IDatabaseHolder databaseHolder(final File file) {

		try {

			final Class<?> skdFileClass = Class.forName("net.thesilkminer.skl."
					      + "interpreter.implementation.skd.DatabaseFile");

			final Method of = skdFileClass.getMethod("of", File.class);

			return (IDatabaseHolder) of.invoke(null, file);
		} catch (final ReflectiveOperationException ex) {

			throw new RuntimeException(ex);
		}
	}
	/*
	/**
	 * Gets a new IDatabase instance from the specified declarations.
	 *
	 * @param docType
	 * 		The doctype.
	 * @param version
	 * 		The database's version.
	 * @param struct
	 * 		The database's structure.
	 * @return
	 * 		A new instance with the specified parameters.
	 *
	 * @since 0.2
	 */
	/*
	public IDatabase database(final IDocTypeDeclaration docType,
							  final IDatabaseVersionDeclaration version,
							  final IStructure struct) {

		try {

			final Class<?> databaseClass = Class.forName("net.thesilkminer.skl."
					      + "interpreter.implementation.skd.structure."
					      + "Database");

			final Method newDatabase = databaseClass.getMethod("newDatabase",
					      IDocTypeDeclaration.class,
					      IDatabaseVersionDeclaration.class,
					      IStructure.class);

			return (IDatabase) newDatabase.invoke(null, docType, version, struct);
		} catch (final ReflectiveOperationException ex) {

			throw new RuntimeException(ex);
		}
	}
	*/
	/**
	 * Gets a new IDocTypeDeclaration.
	 *
	 * @param docType
	 * 		The doctype.
	 * @return
	 * 		A new declaration instance.
	 *
	 * @since 0.2
	 */
	/*
	public IDocTypeDeclaration docType(final String docType) {

		try {

			final Class<?> docTypeClass = Class.forName("net.thesilkminer.skl."
					      + "interpreter.implementation.skd.structure"
					      + ".declarations.DocType");

			final Method of = docTypeClass.getMethod("of", String.class);

			return (IDocTypeDeclaration) of.invoke(null, docType);
		} catch (final ReflectiveOperationException ex) {

			throw new RuntimeException(ex);
		}
	}
	*/
	/**
	 * Gets a new IDatabaseVersionDeclaration.
	 *
	 * @param version
	 * 		The version.
	 * @return
	 * 		A new declaration instance.
	 *
	 * @since 0.2
	 */
	/*
	public IDatabaseVersionDeclaration version(@Nullable final String version) {

		try {

			final Class<?> declarationClass = Class.forName("net.thesilkminer.skl."
					      + "interpreter.implementation.skd.structure"
					      + ".declarations.DatabaseVersion");

			final Method get;

			if (version == null) {

				get = declarationClass.getMethod("get");
				return (IDatabaseVersionDeclaration) get.invoke(null);
			}

			get = declarationClass.getMethod("get", String.class);
			return (IDatabaseVersionDeclaration) get.invoke(null, version);
		} catch (final ReflectiveOperationException ex) {

			throw new RuntimeException(ex);
		}
	}
	*/
	/**
	 * Gets a new structure of the current implementation.
	 *
	 * @param tags
	 * 		The main tags of the structure.
	 * @return
	 * 		The structure instance of the current implementation.
	 *
	 * @since 0.2
	 */
	/*
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public IStructure structure(final Optional<List<ISkdTag>> tags) {

		try {

			final Class<?> skdFileClass = Class.forName("net.thesilkminer.skl."
					      + "interpreter.implementation.skd.structure."
					      + "Structure");

			final Method newInstance = skdFileClass.getMethod("newInstance",
					      Optional.class);

			return (IStructure) newInstance.invoke(null, tags);
		} catch (final ReflectiveOperationException ex) {

			throw new RuntimeException(ex);
		}
	}
	*/
	/**
	 * Gets a new instance of an SKD tag.
	 *
	 * @param name
	 * 		The tag's name.
	 * @return
	 * 		A new tag instance.
	 *
	 * @since 0.2
	 */
	/*
	public ISkdTag tag(final String name) {

		try {

			final Class<?> tagClass = Class.forName("net.thesilkminer.skl."
					      + "interpreter.implementation.skd.structure."
					      + "SkdTag");

			final Method of = tagClass.getDeclaredMethod("of", String.class);

			return (ISkdTag) of.invoke(null, name);
		} catch (final ReflectiveOperationException ex) {

			throw new RuntimeException(ex);
		}
	}
	*/
	/**
	 * Gets a new property from the specified values.
	 *
	 * @param name
	 * 		The property's name.
	 * @param value
	 * 		The property's value. It must be either a String or an Optional.
	 * @return
	 * 		A new property.
	 *
	 * @since 0.2
	 */
	/*
	public ISkdProperty property(final String name, final Object value) {

		try {

			final Class<?> propertyClass = Class.forName("net.thesilkminer.skl."
					      + "interpreter.implementation.skd.structure."
					      + "SkdProperty");

			final Class<?> valueClass = value instanceof String
					      ? String.class : Optional.class;

			final Method getProperty = propertyClass.getDeclaredMethod("getProperty",
					      String.class, valueClass);

			return (ISkdProperty) getProperty.invoke(null, name,
					      valueClass.cast(value));
		} catch (final ReflectiveOperationException ex) {

			throw new RuntimeException(ex);
		}
	}
	*/
}
