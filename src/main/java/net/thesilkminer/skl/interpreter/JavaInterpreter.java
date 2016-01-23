package net.thesilkminer.skl.interpreter;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 * Main window of the Java Interpreter.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public class JavaInterpreter extends JFrame implements Runnable {

	private Thread currentThread;

	private boolean running;

	public static void main(String[] args) {

		JavaInterpreter thiz = new JavaInterpreter();
		thiz.run();
		thiz.openGui();
	}

	@Override
	public void run() {

		if (running && !currentThread.isAlive()) {

			currentThread = Thread.currentThread();

			if (!currentThread.isAlive()) {

				currentThread.start();
			}
		}

		running = false;
		currentThread = null;
	}

	private void openGui() {

		try {

			this.warn();
			this.constructComponent();
		} catch (Throwable t) {

			System.err.println(t.getMessage());
			// Display crash window
		}
	}

	private void warn() {

		JOptionPane.showMessageDialog(null,
				      "Beta build! Confidential! Do not redistribute!",
				      "Warning",
				      JOptionPane.INFORMATION_MESSAGE,
				      null);
	}

	private void constructComponent() {

		this.setTitle("SKL Java Interpreter");
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setMinimumSize(new java.awt.Dimension(500, 500));
		this.setPreferredSize(new java.awt.Dimension(500, 500));
		this.setResizable(false);
		this.add(new MainMenu());

		final String iconPath = "/assets/skl_java_interpreter/gui/icon.png";
		final ImageIcon icon = new ImageIcon(JavaInterpreter.class.getResource(iconPath));
		this.setIconImage(icon.getImage());

		this.setLocation(this.getWidth() / 2, this.getHeight() / 2);
		this.setVisible(true);
	}
}
