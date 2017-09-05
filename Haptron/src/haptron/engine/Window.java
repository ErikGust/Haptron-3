package haptron.engine;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Window extends JFrame {
	private static final long serialVersionUID = 4671487526006221657L;
	private static final Dimension preferredSize = new Dimension(900, 660);

	public Window() {
		getContentPane().setPreferredSize(preferredSize);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
}
