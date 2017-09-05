package haptron;

import haptron.engine.Console;
import haptron.utils.Resources;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public enum Tile {
	AIR, SOLID, VIRTUAL, SELECTED;
	public static final int SIZE = 20;
	private static Image tiles;
	public final Image image;
	
	Tile() {
		Image i;
		try {
			i = loadImage();
		} catch (IOException e) {
			Console.error(e);
			System.exit(-1);
			i = null;
		}
		image = i;
	}
	
	private Image loadImage() throws IOException {
		if(tiles == null) tiles = loadTiles();
		
		final int height = tiles.getHeight(null);
		final int width = height;
		
		BufferedImage image = new BufferedImage(width, height, 
				BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = image.createGraphics();
		g.drawImage(tiles, -ordinal()*width, 0, null);
		g.dispose();
		
		return image;
	}

	private static Image loadTiles() throws IOException {
		return ImageIO.read(
				Resources.getResource("haptron.tiles"));
	}
	
	public void render(Graphics2D g, int x, int y) {
		g.drawImage(image, x, y, SIZE, SIZE, null);
	}
	
	public void render(Graphics2D g, int x, int y, int width, int height) {
		g.drawImage(image, x, y, width, height, null);
	}
}
