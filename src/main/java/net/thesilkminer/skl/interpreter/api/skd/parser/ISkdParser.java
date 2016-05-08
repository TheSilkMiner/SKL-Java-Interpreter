package net.thesilkminer.skl.interpreter.api.skd.parser;

import net.thesilkminer.skl.interpreter.api.skd.holder.IDatabaseHolder;
import net.thesilkminer.skl.interpreter.api.skd.structure.IDatabase;

import java.util.Optional;

/**
 * Represents the API entry for an SKD parser.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
@SuppressWarnings("unused")
public interface ISkdParser {

	/**
	 * Represents the current version of the SKD language.
	 *
	 * @since 0.2
	 */
	String CURRENT_VERSION = "0.2";

	/**
	 * Initializes the parser previously created.
	 *
	 * <p>Initializing means checking if the file is valid to be processed.</p>
	 *
	 * @param force
	 * 		Whether to allow non-skd-ending files to be parsed.
	 * 		    True is used to allow, false to avoid.
	 *
	 * @since 0.2
	 */
	void init(final boolean force);

	/**
	 * Gets if the parser has been initialized.
	 *
	 * @return
	 * 		If the parser has been initialized.
	 *
	 * @since 0.2
	 */
	boolean init();

	/**
	 * Gets if the parser has errored.
	 *
	 * @return
	 * 		If the parser has errored.
	 *
	 * @since 0.2
	 */
	boolean errored();

	/**
	 * Reads the file and creates an in-memory representation
	 * of the content.
	 *
	 * @return
	 * 		The in-memory representation of the database file.
	 *
	 * @since 0.2
	 */
	IDatabase read();

	/**
	 * Writes the database back to the file specified when
	 * initializing the parser.
	 *
	 * <p>By default, this method will call
	 * {@link #write(IDatabase, IDatabaseHolder)} with the
	 * currently stored database holder, retrieved through
	 * {@link #databaseHolder()}.</p>
	 *
	 * <p>Normally you shouldn't override this method.</p>
	 *
	 * @param database
	 * 		The database you need to write.
	 * @return
	 * 		If the process was successful.
	 *
	 * @since 0.2
	 */
	default boolean write(final IDatabase database) {

		return this.write(database, this.databaseHolder());
	}

	/**
	 * Writes the database to the specified file.
	 *
	 * @param database
	 * 		The database you need to write.
	 * @param holder
	 * 		The script holder.
	 * @return
	 * 		If the process was successful.
	 *
	 * @since 0.2
	 */
	boolean write(final IDatabase database, final IDatabaseHolder holder);

	/**
	 * Gets the name of the database.
	 *
	 * @return
	 * 		The database's name.
	 *
	 * @deprecated
	 * 		Use {@link #getDatabaseName()} instead.
	 *
	 * @since 0.2
	 */
	@Deprecated
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	default String databaseName() {

		return this.getDatabaseName().isPresent() ? this.getDatabaseName().get() : null;
	}

	/**
	 * Gets the name of the database.
	 *
	 * @return
	 * 		The database's name.
	 *
	 * @apiNote
	 * 	    This method's name will be changed back to
	 * 	    {@code databaseName} when the deprecated method
	 * 	    will be removed.
	 *
	 * @since 0.2
	 */
	Optional<String> getDatabaseName();

	/**
	 * Gets the current parser's database holder.
	 *
	 * @return
	 * 		The database holder.
	 *
	 * @since 0.2
	 */
	IDatabaseHolder databaseHolder();
}
