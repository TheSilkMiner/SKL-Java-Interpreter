package net.thesilkminer.skl.interpreter.api.skd.parser;

import net.thesilkminer.skl.interpreter.api.skd.structure.IDatabase;

import java.io.File;

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
	 * Writes the database back to the file.
	 *
	 * @param database
	 * 		The database you need to write.
	 * @return
	 * 		If the process was successful.
	 *
	 * @since 0.2
	 */
	boolean write(final IDatabase database);

	/**
	 * Writes the database to the specified file.
	 *
	 * <p>If not otherwise stated, this method is
	 * currently not supported and, as such, will
	 * simply call the {@link #write(IDatabase)} method.</p>
	 *
	 * @param database
	 * 		The database you need to write.
	 * @param file
	 * 		The file.
	 * @return
	 * 		If the process was successful.
	 *
	 * @since 0.2
	 */
	default boolean write(final IDatabase database, final File file) {

		return this.write(database);
	}

	/**
	 * Gets the name of the database.
	 *
	 * @return
	 * 		The database's name.
	 *
	 * @since 0.2
	 */
	String databaseName();
}
