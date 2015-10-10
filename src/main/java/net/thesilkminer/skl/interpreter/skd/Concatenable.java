package net.thesilkminer.skl.interpreter.skd;

/**
 * Created by TheSilkMiner on 10/10/2015.
 * Package: net.thesilkminer.skl.interpreter.skd.
 * Project: Java Interpreter.
 */

/**
 * Marks a class as provider of a secondary {@code toString()} method,
 * which allows for a better concatenation of the representation.
 */
public interface Concatenable {

	/**
	 * Returns a string representation of the object. In general, the
	 * {@code toString} method returns a string that
	 * "textually represents" this object. The result should
	 * be a concise but informative representation that is easy for a
	 * person to read.
	 * It is recommended that all subclasses override this method.
	 *
	 * <p>The {@code toString} method for class {@code Object}
	 * returns a string consisting of the name of the class of which the
	 * object is an instance, the at-sign character `{@code @}', and
	 * the unsigned hexadecimal representation of the hash code of the
	 * object. In other words, this method returns a string equal to the
	 * value of:</p>
	 * <blockquote>
	 * <pre>
	 * getClass().getName() + '@' + Integer.toHexString(hashCode())
	 * </pre></blockquote>
	 *
	 * @return  a string representation of the object.
	 */
	String toString(boolean toConcat);
}
