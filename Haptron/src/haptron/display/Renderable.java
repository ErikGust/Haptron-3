package haptron.display;

import java.awt.Graphics2D;

@FunctionalInterface
public interface Renderable {
	public boolean render(Graphics2D g);
}
