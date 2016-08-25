/**
 * This package contains all the various extensions to the
 * SKL Java interpreter.
 *
 * <p>Classes in here are not part of the official API, so
 * implementations that rely on them should express it
 * explicitly.</p>
 *
 * <p>Every extension must be located in a package named
 * according to the following scheme:</p>
 *
 * {@code
 *     net.thesilkminer.skl.interpreterx.<part>x.<author>.<extension_name>.<version>;
 * }
 *
 * <p>where {@code part} identifies the language component
 * the extension is developed for (e.g., if the extension has
 * been developed for {@code skd}, this string will be substituted
 * to the {@code part} value.</p>
 *
 * <p>where {@code author} identifies the author of the
 * extension.</p>
 *
 * <p>where {@code extension_name} identifies the name of
 * the extension, written all lowercase, without spaces or
 * other signs in between (e.g., if the extension's name
 * is {@code New Extension}, the package would be named
 * {@code newextension}).</p>
 *
 * <p>where {@code version} represents the current version of
 * the extension, with every {@code .} replaced with a {@code _},
 * eventually prefixed with a {@code v} (e.g., {@code v1_0_RC}).</p>
 *
 * <p>All extensions must be versioned. Using classes between
 * different versions packages is not allowed. To re-use the
 * same classes between different versions, the developer
 * should either copy them between packages, place them in
 * a special, un-versioned package supported by the current
 * structure (the package version must be {@code v_shared}) or
 * use the provided annotation
 * {@link net.thesilkminer.skl.interpreterx.base.annotations.MultipleVersionCompatible}
 * and specify the various versions in the {@code value}.</p>
 *
 * <p>All classes contained in the special un-versioned package will
 * be shared across <strong>all</strong> versions, so they must be
 * completely backwards compatible.</p>
 *
 * <p>All classes annotated by the above annotation MUST be compatible
 * only withing the specified range of values.</p>
 *
 * <p>Also, all packages must contain a brief {@code package-info.java}
 * containing at least the author and the first existence of
 * the package (through {@literal @}since or other means).</p>
 *
 * <p>Last, but not least, the extension's main package (the one
 * which contains the version sub-packages, should contain a
 * class named with the name of the extension and annotated
 * with the various annotations in the
 * {@code net.thesilkminer.skl.interpreterx.base} package.</p>
 *
 * @author TheSilkMiner (specifications)
 *
 * @since 0.2.1 (specifications)
 */
package net.thesilkminer.skl.interpreterx;
