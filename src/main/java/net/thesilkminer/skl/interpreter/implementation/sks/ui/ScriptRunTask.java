package net.thesilkminer.skl.interpreter.implementation.sks.ui;

import net.thesilkminer.skl.interpreter.implementation.sks.ScriptFile;
import net.thesilkminer.skl.interpreter.implementation.sks.SksParser;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

/**
 * Runs the script selected.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
@SuppressWarnings("WeakerAccess")
// Same as in MainMenu
public class ScriptRunTask extends SwingWorker<Integer, Integer> {

	private final JFrame parent;

	public ScriptRunTask(final JFrame parent) {

		this.parent = parent;
	}

	@Override
	protected Integer doInBackground() throws Exception {

		final FileFilter filter = new FileFilter() {

			@Override
			public boolean accept(final File file) {

				if (file.isDirectory()) {

					return true;
				}

				String ext = null;
				final String name = file.getName();
				int ind = name.lastIndexOf('.');

				if (0 < ind && ind < name.length() - 1) {

					ext = name.substring(++ind).toLowerCase();
				}

				return "sks".equals(ext);
			}

			@Override
			public String getDescription() {

				return "Script Files (*.sks)";
			}
		};

		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(filter);
		fileChooser.setFileFilter(filter);
		final int returnValue = fileChooser.showDialog(this.parent, "Run script...");

		if (!(returnValue == JFileChooser.APPROVE_OPTION)) {

			this.setProgress(50);
			return null;
		}

		final File script = fileChooser.getSelectedFile();

		if (!script.exists() || !script.canRead()) {

			this.setProgress(80);
			return null;
		}

		final boolean force = !script.getName().endsWith(".sks");

		final ScriptFile scriptFile = ScriptFile.of(script);
		final SksParser parser = SksParser.of(scriptFile);
		parser.initParser(force);
		parser.parse();

		this.setProgress(100);
		return null;
	}
}
