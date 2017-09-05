package haptron.display;

public interface Renderer {
	public Renderable[] getRenderables();
	public void addRenderable(Renderable r);
}
