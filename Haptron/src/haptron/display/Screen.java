package haptron.display;

import java.awt.Graphics2D;

public interface Screen extends Renderable {
	
	public void open(Display parent);
	
	public void close(Display parent);
	
	public void resize(int width, int height);
	
	@Override
	public boolean render(Graphics2D g);
}
