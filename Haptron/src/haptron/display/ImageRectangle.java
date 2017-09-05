package haptron.display;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

public class ImageRectangle extends Rectangle implements Renderable {
	private static final long serialVersionUID = 8614240979020683022L;
	
	private Image image;
	
	public Object extra;
	
	public ImageRectangle() {
		
	}
	
	public ImageRectangle(Image image) {
		this();
		this.image = image;
	}

	@Override
	public boolean render(Graphics2D g) {
		final int x = this.x;
		final int y = this.y;
		final int w = this.width;
		final int h = this.height;
		final Image i = this.image;
		
		if(i != null) g.drawImage(i, x, y, w, h, null);
		
		return true;
	}
	
	public ImageRectangle copy() {
		return (ImageRectangle) this.clone();
	}

	public Image getImage() {
		return image;
	}

	public synchronized void setImage(Image image) {
		this.image = image;
	}
}
