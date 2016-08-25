package net.thesilkminer.skl.interpreterx.base.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the specified class is compatible with
 * multiple version of the specified extension.
 *
 * @author TheSilkMiner
 *
 * @since 0.2.1 (SKL Interpreter)
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface MultipleVersionCompatible {

	/**
	 * Holds a list of all the compatible versions, following
	 * Maven range styling.
	 *
	 * @return
	 *      A list of all the compatible versions.
	 *
	 * @since 0.2.1 (SKL Interpreter)
	 */
	String value();
}
