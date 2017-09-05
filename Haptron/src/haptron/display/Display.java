package haptron.display;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class Display extends JPanel {
	private static final long serialVersionUID = -195283264560666655L;
	
	public static final int BUFFER_COLORMODEL = BufferedImage.TYPE_INT_ARGB;
	
	private BufferedImage image;
	
	public Display() {
		addComponentListener(new BufferValidationListener());
		validateBuffer();
	}
	
	
	public Graphics2D getContext() {
		return (Graphics2D) image.createGraphics();
	}
	
	public synchronized void validate() {
		validateBuffer();
		super.validate();
	}
	
	public synchronized void swapBuffers() {
		if(image == null) return;
		
		Graphics2D g = (Graphics2D) super.getGraphics();
		
		if(g == null) return;

		g.drawImage(image, 0, 0, null);
	}
	
	@Override
	public synchronized final void paintComponent(Graphics g) {
		if(image == null) return;
		g.drawImage(image, 0, 0, null);
	}
	
	public synchronized void validateBuffer() {
		final int width = Math.max(getWidth(), 1);
		final int height = Math.max(getHeight(), 1);
		
		if(image != null) image.flush();
		image = null;
		System.gc();
		
		image = new BufferedImage(width, height, 
				BUFFER_COLORMODEL);
	}
	
	private final class BufferValidationListener extends ComponentAdapter {
		
		public void componentResized(ComponentEvent e) {
			Display.this.validateBuffer();
		}
		
		public void componentShown(ComponentEvent e) {
			Display.this.validateBuffer();
		}
	}
	
	@Deprecated
	public Graphics getGraphics() {
		return super.getGraphics();
	}
}
