package net.thesilkminer.skl.interpreterx.base;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import net.thesilkminer.skl.interpreterx.base.annotations.Extension;
import net.thesilkminer.skl.interpreterx.base.annotations.VersionCatalog;
import net.thesilkminer.skl.interpreterx.base.annotations.VersionMainType;
import net.thesilkminer.skl.interpreterx.base.interfaces.INeedsInit;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Manager used to control all the various extensions
 * provided by implementations.
 *
 * @author TheSilkMiner
 *
 * @since 0.2.1 (SKL Interpreter)
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class ExtensionManager {

	/**
	 * Represents a request made to the extension manager.
	 *
	 * <p>Request is itself a wrapper type, containing two
	 * {@link String} instances: one for the
	 * {@link Request#id id} and one for the
	 * {@link Request#name name} of the extension.</p>
	 *
	 * <p>For no reason the two values can be both {@code null}
	 * at the same time. Either {@code id} or {@code name}
	 * must be filled with the extension specific value.</p>
	 *
	 * <p>In case both values are non-{@code null}, then the
	 * {@code id} takes precedence over the {@code name}
	 * specified.</p>
	 *
	 * <p>For more information, please refer to the code
	 * of {@link #obtain(Request) this method}.</p>
	 *
	 * @author TheSilkMiner
	 *
	 * @since 0.2.1 (SKL Interpreter)
	 */
	public static final class Request {

		/**
		 * Holds the ID of the extension.
		 *
		 * <p>This values takes precedence over {@link #name}.</p>
		 *
		 * @since 0.2.1
		 */
		@Nullable public String id;

		/**
		 * Holds the name of the extension.
		 *
		 * @since 0.2.1
		 */
		@Nullable public String name;
	}

	/**
	 * Wrapper type used to hold the result of an extension request.
	 *
	 * <p>The main use of this wrapper is the possibility for the caller
	 * to obtain a specific extension version, thanks to the
	 * {@link #obtain(String)} method.</p>
	 *
	 * <p>In case the programmer prefers to obtain the wrapped object,
	 * then a simple call to {@link #obtain()} will return it.</p>
	 *
	 * @param <C>
	 *     The wrapped object type. It should be automatically inferred
	 *     by the compiler.
	 *
	 * @since 0.2.1 (SKL Interpreter)
	 */
	public static final class ExtensionType<C> {

		private C wrapped;

		/**
		 * Obtains the original, unwrapped object.
		 *
		 * @return
		 *      The unwrapped object.
		 *
		 * @since 0.2.1 (SKL Interpreter)
		 */
		@Contract(value = "-> !null", pure = true)
		@NotNull
		public C obtain() {
			return this.wrapped;
		}

		/**
		 * Obtains an instance of the wrapped extension with the
		 * specified version.
		 *
		 * <p>This method is usually preferred, instead of manual
		 * initialization.</p>
		 *
		 * @param ver
		 *      The version string.
		 * @return
		 *      A {@link Pair} containing an instance of the extension
		 *      and its class. The pair can also be supplied to
		 *      {@link #cast(Pair)} or to {@link #cast(Class, Object)}
		 *      to obtain a casted instance.
		 *
		 * @since 0.2.1 (SKL Interpreter)
		 */
		@Contract(value = "null -> fail", pure = true)
		@NotNull
		public Pair<? extends C, Class<? extends C>> obtain(@NotNull final String ver) {
			final Extension extension = Optional.ofNullable(
					this.wrapped.getClass().getAnnotation(Extension.class))
					.orElseThrow(RuntimeException::new);
			final Class<? extends Enum<?>> versionCatalog = extension.versionCatalog();

			Optional.ofNullable(versionCatalog
						.getAnnotation(VersionCatalog.class))
					.orElseThrow(() ->
							new RuntimeException("Invalid catalog"));

			final String version = (ver
						.toUpperCase(Locale.ENGLISH)
						.startsWith("V") ? ver : "V".concat(ver))
					.toUpperCase(Locale.ENGLISH)
					.replace('.', '_');

			final Field spec = this.searchEnumField(versionCatalog, version);

			final VersionMainType main = Optional.ofNullable(
					spec.getAnnotation(VersionMainType.class)
			).orElseThrow(() ->
				new RuntimeException("Missing @VersionMainType annotation"));

			@SuppressWarnings("unchecked")
			final Class<? extends C> clazz = (Class<? extends C>) main.value();
			final C instance;
			try {
				instance = clazz.getConstructor().newInstance();
			} catch (final ReflectiveOperationException exception) {
				throw new RuntimeException(exception);
			}

			return Pair.of(instance, clazz);
		}

		private Field searchEnumField(final Class<?> clazz, final String fieldName) {
			return Arrays.stream(clazz.getDeclaredFields())
					.filter(it -> it.getName().equals(fieldName))
					.filter(it -> Modifier.isPublic(it.getModifiers()))
					.filter(it -> Modifier.isStatic(it.getModifiers()))
					.filter(it -> Modifier.isFinal(it.getModifiers()))
					.findFirst()
					.orElseThrow(() -> new RuntimeException("Invalid version: "
									+ fieldName));
		}
	}

	private static final ExtensionManager IT = new ExtensionManager();

	private final Map<Extension, Class<?>> registered;

	private ExtensionManager() {
		this.registered = Maps.newHashMap();
	}

	/**
	 * Gets the singleton instance of the extension manager.
	 *
	 * @return
	 *      The manager's unique instance.
	 *
	 * @since 0.2.1 (SKL Interpreter)
	 */
	@Contract(value = "-> !null", pure = true)
	@NotNull
	public static ExtensionManager it() {
		return IT;
	}

	/**
	 * Registers a specific class as an extension.
	 *
	 * @param toRegister
	 *      The class to register.
	 *
	 * @since 0.2.1 (SKL Interpreter)
	 */
	@Contract("null -> fail; !null -> _")
	public void register(@NotNull
	                     @SuppressWarnings("TypeMayBeWeakened")
	                     final Class<?> toRegister) {
		final Extension extension = Optional.ofNullable(
				toRegister.getAnnotation(Extension.class))
				.orElseThrow(() -> new RuntimeException("Missing @Extension"));
		this.registered.put(extension, toRegister);
	}

	/**
	 * Obtains an extension which respects the specified requests.
	 *
	 * <p>The passed in request is a wrapper around two values: refer
	 * to the {@link Request specific Javadoc} for more information.</p>
	 *
	 * @param request
	 *      The request.
	 * @param <T>
	 *      The class type of the extension. This is normally inferred
	 *      by the compiler.
	 * @return
	 *      An extension wrapped in an {@link ExtensionType}.
	 *
	 * @since 0.2.1 (SKL Interpreter)
	 */
	@Contract(value = "null -> fail; !null -> !null", pure = true)
	@NotNull
	public <T> ExtensionType<T> obtain(@NotNull final Request request) {
		if (request.id == null && request.name == null) {
			throw new RuntimeException();
		}

		final Class<?> clazz = request.id == null
				? this.obtainByName(request.name)
				: this.obtainById(request.id);

		final ExtensionType<T> type = new ExtensionType<>();

		try {
			type.wrapped = (T) clazz.getConstructor().newInstance();
		} catch (final ReflectiveOperationException exception) {
			throw new RuntimeException(exception);
		}

		return type;
	}

	/**
	 * Casts the object in the specified {@code pair} with the
	 * class specified in the {@code pair} itself.
	 *
	 * <p>The pair structure must contain the object on the left
	 * side, while the class is on the right side.</p>
	 *
	 * @param pair
	 *      The pair containing the object to cast and the class.
	 * @param <T>
	 *      The type the object should be after the cast.
	 * @return
	 *      A casted object of the specified type.
	 *
	 * @since 0.2.1 (SKL Interpreter)
	 */
	@Contract(value = "null -> fail; !null -> !null", pure = true)
	@NotNull
	public <T> T cast(@NotNull final Pair<Object, Class<T>> pair) {
		return this.cast(pair.getRight(), pair.getLeft());
	}

	/**
	 * Casts the specified {@code object} to the type specified
	 * by {@code clazz}.
	 *
	 * <p>A {@link RuntimeException} is thrown if the cast causes
	 * a {@link ClassCastException}.</p>
	 *
	 * @param clazz
	 *      The type the object should be cast to.
	 * @param object
	 *      The object to cast.
	 * @param <T>
	 *      The type the object should be after the cast.
	 * @return
	 *      The casted object.
	 *
	 * @since 0.2.1 (SKL Interpreter)
	 */
	@Contract("null, null -> fail; null, _ -> fail; _, null -> fail; !null, !null -> !null")
	@NotNull
	@SuppressWarnings("unchecked") // Due to the dead code
	public <T> T cast(@NotNull final Class<T> clazz, @NotNull final Object object) {
		Preconditions.checkNotNull(clazz);
		try {
			return clazz.cast(Preconditions.checkNotNull(object));
		} catch (final ClassCastException exception) {
			Throwables.propagate(exception);
			return (T) object; //Dead code
		}
	}

	/**
	 * Initializes the specified extension, calling all the various
	 * methods needed.
	 *
	 * @param extension
	 *      The extension to initialize.
	 * @param <T>
	 *      The type of the extension. It should be automatically
	 *      inferred by the Java Compiler.
	 * @return
	 *      The extension itself, when the process is completed.
	 *
	 * @since 0.2.1 (SKL Interpreter)
	 */
	@Contract(value = "null -> fail; !null -> !null", pure = true)
	@NotNull
	public <T> T init(@NotNull final T extension) {
		Preconditions.checkNotNull(extension);

		if (extension instanceof INeedsInit) {
			((INeedsInit) extension).init();
		}

		return extension;
	}

	private Class<?> obtainByName(@NotNull final String name) {
		return this.registered.entrySet().stream()
				.filter(it -> it.getKey().name().equals(name))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Invalid extension name"))
				.getValue();
	}

	private Class<?> obtainById(@NotNull final String id) {
		return this.registered.entrySet().stream()
				.filter(it -> it.getKey().id().equals(id))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Invalid extension ID"))
				.getValue();
	}
}
