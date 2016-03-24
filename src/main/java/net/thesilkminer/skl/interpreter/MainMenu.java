package net.thesilkminer.skl.interpreter;

import net.thesilkminer.skl.interpreter.implementation.sks.ui.UiFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

/**
 * Panel which holds the Main Menu.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
@SuppressWarnings("WeakerAccess")
// Nope. We want implementations to be able to access this class.
public class MainMenu extends JPanel {

	/**
	 * Constructs an instance of the main menu.
	 *
	 * @since 0.2
	 */
	public MainMenu() {

		this.setPreferredSize(new Dimension(500, 500));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		try {

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final ReflectiveOperationException | UnsupportedLookAndFeelException e) {

			System.err.println("An error has occured while setting the Look And Feel");
		}

		final JPanel banner = new JPanel();
		banner.setBorder(new EmptyBorder(24, 24, 24, 24));
		banner.setPreferredSize(new Dimension(500, 120));
		banner.setMinimumSize(new Dimension(100, 120));
		banner.setMaximumSize(new Dimension(700, 120));
		banner.setBackground(new Color(0x2336C2));
		this.add(banner);
		banner.setLayout(new BorderLayout(0, 0));

		final JLabel software = new JLabel("SKL Java Interpreter");
		software.setForeground(new Color(0xFFFFFF));
		software.setBorder(new EmptyBorder(0, 24, 0, 0));
		banner.add(software, BorderLayout.CENTER);

		software.setFont(software.getFont().deriveFont(software.getFont().getSize() + 12F));

		final JLabel icon = new JLabel("");
		final String iconPath = "/assets/skl_java_interpreter/gui/logo.png";
		icon.setIcon(new ImageIcon(JavaInterpreter.class.getResource(iconPath)));
		icon.setMinimumSize(new Dimension(72, 72));
		icon.setPreferredSize(new Dimension(72, 72));
		banner.add(icon, BorderLayout.WEST);

		final JPanel content = new JPanel();
		content.setBorder(new EmptyBorder(24, 24, 6, 24));
		this.add(content);

		final JLabel welcome = new JLabel("Welcome to the SKL Java Interpreter!");
		content.add(welcome);

		final JLabel choose = new JLabel("Choose an interpreter to run "
				      + "using the buttons below.");
		content.add(choose);

		final JPanel buttons = new JPanel();
		buttons.setBorder(new EmptyBorder(6, 24, 24, 24));
		this.add(buttons);

		final JButton sks = new JButton("SKS Interpreter");
		sks.addActionListener(e -> 	new UiFrame());
		buttons.add(sks);
	}
}
