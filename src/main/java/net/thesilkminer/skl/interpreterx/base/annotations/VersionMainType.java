package net.thesilkminer.skl.interpreterx.base.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used on the various version catalog fields to
 * specify the main version class.
 *
 * <p>The specified version must extend the main class,
 * annotated with {@link Extension}.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2.1 (SKL Interpreter)
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VersionMainType {

	/**
	 * Gets the main version class.
	 *
	 * @return
	 *      The main version class.
	 *
	 * @since 0.2.1
	 */
	@NotNull
	Class<?> value();
}
