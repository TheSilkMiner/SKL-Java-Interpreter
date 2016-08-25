package net.thesilkminer.skl.interpreterx.base.annotations;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;

/**
 * Marker annotation used to identify main annotation
 * classes.
 *
 * @author TheSilkMiner
 *
 * @since 0.2.1 (SKL Interpreter)
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Extension {

	enum Language {
		SKS,
		SKL,
		SKD;

		@Contract(value = "-> !null", pure = true)
		@NotNull
		@Override
		public String toString() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}
	}

	/**
	 * Holds the ID of the extension.
	 *
	 * <p>This value must match the extensions package name.</p>
	 *
	 * @return
	 *      The extension id.
	 *
	 * @since 0.2.1
	 */
	@NotNull
	String id();

	/**
	 * Gets the name of the extension.
	 *
	 * @return
	 *      The name of the extension.
	 *
	 * @since 0.2.1
	 */
	@NotNull
	String name() default "";

	/**
	 * Gets the current version of the extension.
	 *
	 * <p>The value must be contained in the specified catalog
	 * type. {@code V}s are added automatically.</p>
	 *
	 * @return
	 *      The current version of the extension.
	 *
	 * @since 0.2.1
	 */
	@NotNull
	String version() default "";

	/**
	 * Gets the author of this extension.
	 *
	 * @return
	 *      The author of this extension.
	 *
	 * @since 0.2.1
	 */
	@NotNull
	String author();

	/**
	 * Gets the language component this extension is
	 * developed for.
	 *
	 * @return
	 *      The language component this extension is for.
	 *
	 * @since 0.2.1
	 */
	@NotNull
	Language language();

	/**
	 * Gets the version catalog of the extension.
	 *
	 * @return
	 *      The version catalog of the extension.
	 *
	 * @since 0.2.1
	 */
	@NotNull
	Class<? extends Enum<?>> versionCatalog();
}
