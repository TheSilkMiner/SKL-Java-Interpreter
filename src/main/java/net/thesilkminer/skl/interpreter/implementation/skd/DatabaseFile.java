package net.thesilkminer.skl.interpreter.implementation.skd;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import net.thesilkminer.skl.interpreter.api.skd.holder.IDatabaseHolder;

import java.io.*;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * This class is used to represent a database file.
 *
 * <p>Refer to the {@link File} class to obtain more information.
 *
 * @author TheSilkMiner
 * @since 1.0
 * @version 1.0
 */
@SuppressWarnings("WeakerAccess")
public class DatabaseFile extends File implements IDatabaseHolder {

	private final String fileName;
	private final String fileExtension;

	/**
	 * Creates a new <code>DatabaseFile</code> instance from a parent pathname string
	 * and a child pathname string.
	 *
	 * <p>If <code>parent</code> is <code>null</code> then the method will
	 * simply fail as soon as possible, throwing a <code>NullPointerException</code>.
	 *
	 * <p>Otherwise the <code>parent</code> pathname string is taken to denote
	 * a directory, and the <code>child</code> pathname string is taken to
	 * denote either a directory or a file.  If the <code>child</code> pathname
	 * string is absolute then it is converted into a relative pathname in a
	 * system-dependent way.  If <code>parent</code> is the empty string then
	 * the new <code>ScriptFile</code> instance is created by converting
	 * <code>child</code> into an abstract pathname and resolving the result
	 * against a system-dependent default directory.  Otherwise each pathname
	 * string is converted into an abstract pathname and the child abstract
	 * pathname is resolved against the parent.
	 *
	 * @param parent
	 * 		The parent pathname string
	 * @param child
	 * 		The child pathname string
	 *
	 * @throws NullPointerException
	 * 		If either <code>parent</code> or <code>child</code> is <code>null</code>
	 */
	private DatabaseFile(@Nonnull final String parent, @Nonnull final String child) {

		super(Preconditions.checkNotNull(parent), Preconditions.checkNotNull(child));
		final String fileName = this.getName();
		final int indexOfDot = fileName.lastIndexOf('.');
		String extension = fileName.substring((indexOfDot > -1 ? indexOfDot : 0));
		final int endOfString = fileName.length() - extension.length();
		String name = fileName.substring(0, endOfString);

		if (name.contains(".")) {

			name = name.substring(0, name.length() - 1);
		}

		if (extension.contains(".")) {

			extension = extension.substring(1);
		}

		this.fileExtension = extension;
		this.fileName = name;
	}

	/**
	 * This method is used to obtain an instance of <code>DatabaseFile</code>
	 * from an existing <code>File</code>.
	 *
	 * <p>This is the only way of performing this action.
	 *
	 * @param file
	 * 		The file from where we should obtain the script file.
	 * @return
	 * 		A new instance of <code>ScriptFile</code>
	 */
	public static DatabaseFile of(@Nonnull File file) {

		return new DatabaseFile(file.getParent(), file.getName());
	}

	/**
	 * Gets the name of the file.
	 *
	 * <p>This method is heavily different from {@link #getName()}, because
	 * while that method returns the file full name, this one returns
	 * only the main part of the name.
	 *
	 * <p>As an example, if a file is called <code>db.skd</code>,
	 * the method {@link #getName()} will return <code>db.skd</code>,
	 * while this method will only return <code>db</code>.
	 *
	 * @return
	 * 		The file's name
	 */
	public String getFileName() {

		return fileName;
	}

	/**
	 * Gets the file extension.
	 *
	 * <p>Usually we Windows programmers define an "extension" of a file
	 * the part which is after the last point of a file name.
	 *
	 * <p>If you are not a Windows developer, maybe an example could be
	 * better: from a file called <code>db.skd</code>, this method will
	 * return only <code>skd</code>. From a file called <code>db.2.skd</code>,
	 * this method will always return <code>skd</code>.
	 *
	 * @return
	 * 		The file's extension
	 */
	public String getFileExtension() {

		return fileExtension;
	}

	@Override
	public boolean writable() {

		return true;
	}

	@Nonnull
	@Override
	public Optional<String> name() {

		return Optional.of(this.getFileName());
	}

	@Override
	public boolean canBeAcceptedByDefault() {
		return this.getFileExtension().equalsIgnoreCase("skd");
	}

	@Nonnull
	@Override
	public BufferedReader readerStream() {
		try {
			return new BufferedReader(new FileReader(this));
		} catch (final FileNotFoundException exception) {
			Throwables.propagate(exception);
			throw new RuntimeException(); //Dead code, but ok
		}
	}

	@Nonnull
	@Override
	public Optional<BufferedWriter> writerStream() {
		final BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(this));
		} catch (final IOException exception) {
			Throwables.propagate(exception);
			throw new RuntimeException(); //Dead code, but ok
		}
		return Optional.of(out);
	}
}
