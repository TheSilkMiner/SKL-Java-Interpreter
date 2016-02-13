package net.thesilkminer.skl.interpreter.implementation.sks.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Frame used to manage the SKS interpreter.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class UiFrame extends JFrame {

	/**
	 * Constructs an instance of this UiFrame.
	 *
	 * @since 0.2
	 */
	public UiFrame() {

		this.setTitle("SKS Interpreter");
		this.setMinimumSize(new java.awt.Dimension(500, 500));
		this.setPreferredSize(new java.awt.Dimension(500, 500));
		this.setResizable(false);
		this.setLocation(this.getWidth() / 2, this.getHeight() / 2);

		final String frameIconPath = "/assets/sks_interpreter/gui/icon.png";
		final ImageIcon frameIcon = new ImageIcon(UiFrame.class.getResource(frameIconPath));
		this.setIconImage(frameIcon.getImage());

		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

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
		banner.setBackground(new Color(0xFFF400));
		banner.setLayout(new BorderLayout(0, 0));
		this.add(banner);

		final JLabel software = new JLabel("SKS Interpreter");
		software.setForeground(new Color(0x000000));
		software.setBorder(new EmptyBorder(0, 24, 0, 0));
		banner.add(software, BorderLayout.CENTER);

		software.setFont(software.getFont().deriveFont(software.getFont().getSize() + 12F));

		final JLabel icon = new JLabel("");
		final String iconPath = "/assets/sks_interpreter/gui/logo.png";
		icon.setIcon(new ImageIcon(UiFrame.class.getResource(iconPath)));
		icon.setMinimumSize(new Dimension(72, 72));
		icon.setPreferredSize(new Dimension(72, 72));
		banner.add(icon, BorderLayout.WEST);

		final JPanel content = new JPanel();
		content.setBorder(new EmptyBorder(24, 24, 6, 24));
		this.add(content);

		final JLabel welcome = new JLabel("Welcome to the SKS Interpreter!");
		content.add(welcome);

		final JPanel buttons = new JPanel();
		buttons.setBorder(new EmptyBorder(6, 24, 24, 24));
		this.add(buttons);

		final JButton sks = new JButton("Run Script");
		sks.addActionListener(e -> {

			final JDialog dialog = new JDialog();
			final JPanel progressPanel = new JPanel();
			final JProgressBar bar = new JProgressBar();
			bar.setStringPainted(true);
			bar.setString("Running script...");
			bar.setIndeterminate(true);
			progressPanel.add(bar);
			dialog.add(progressPanel);
			dialog.setTitle("Running script...");
			dialog.setSize(new Dimension(400, 120));
			dialog.setLocation(dialog.getWidth() / 2, dialog.getHeight() / 2);
			dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			dialog.setVisible(true);

			final java.awt.Rectangle area = dialog.getContentPane().getBounds();
			((javax.swing.JComponent) dialog.getContentPane()).paintImmediately(area);

			ScriptRunTask task = new ScriptRunTask(this);
			task.addPropertyChangeListener(event -> {

				if ("progress".equals(event.getPropertyName())) {

					final int newVal = (Integer) event.getNewValue();

					switch (newVal) {

						case 50:
							dialog.setVisible(false);
							String msg = "File selection cancelled";
							JOptionPane.showMessageDialog(this, msg);
							break;
						case 80:
							dialog.setVisible(false);
							String ms = "Unable to read file";
							JOptionPane.showMessageDialog(this,
									      ms,
									      null,
									      JOptionPane
							               .ERROR_MESSAGE);
							break;
						case 100:
							dialog.setVisible(false);
							String me = "Script ran successfully";
							JOptionPane.showMessageDialog(this, me);
							break;
						default:
							break;
					}
				}
			});
			task.execute();
		});
		buttons.add(sks);

		this.setVisible(true);
	}
}
