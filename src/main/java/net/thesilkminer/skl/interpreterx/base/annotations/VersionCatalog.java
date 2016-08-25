package net.thesilkminer.skl.interpreterx.base.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to identify an enum holding the various
 * extension versions.
 *
 * <p>An enum holding this annotation should contain only
 * variables with all uppercase names, corresponding to
 * the various versions, adding {@code v}s at the start
 * and replacing {@code .}s with {@code _}s.</p>
 *
 * <p>E.g.:</p>
 *
 * <pre>
 *    {@literal @}VersionCatalog
 *     public enum ParserExtensionVersions {
 *        {@literal @}VersionMainType(01.class)
 *         V0_1,
 *        {@literal @}VersionMainType(02.class)
 *         V0_2,
 *        {@literal @}VersionMainType(03.class)
 *         V0_3,
 *        {@literal @}VersionMainType(10.class)
 *         V1_0,
 *         ;
 *     }
 * </pre>
 *
 * @author TheSilkMiner
 *
 * @since 0.2.1 (SKL Interpreter)
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface VersionCatalog {

}
