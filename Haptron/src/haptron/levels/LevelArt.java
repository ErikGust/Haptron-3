package haptron.levels;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

import haptron.Tile;
import haptron.engine.Console;
import haptron.monster.Monster;
import haptron.utils.ImageUtil;
import haptron.utils.Resources;

public class LevelArt {
	public static final Image heart;
	
	public static void drawBackground(Graphics2D g, int display_width, int display_height) {
		for(int i = 0; i <= display_width; i+= 100) {
			for(int j = 0; j <= display_height; j+= 100) {
				Tile.SOLID.render(g, i, j, 100, 100);
			}
		}
	}
	
	public static void drawBackground(Graphics2D g, int display_width, 
			int display_height, int level_width, int level_height) {
		final int X = display_width / 2 - ((level_width * Tile.SIZE) / 2);
		final int Y = display_height / 2 - ((level_height * Tile.SIZE) / 2);
		final int stroke_width = 4;
		
		drawBackground(g, display_width, display_height);
		
		g.setColor(new Color(0,0,0,100));
		g.fillRect(X-stroke_width, Y, stroke_width, stroke_width+level_height*Tile.SIZE);
		g.fillRect(X-stroke_width, Y-stroke_width, stroke_width*2+level_width*Tile.SIZE, stroke_width);
		
		g.fillRect(X+level_width*Tile.SIZE, Y, stroke_width, level_height*Tile.SIZE);
		g.fillRect(X, Y+level_height*Tile.SIZE, level_width*Tile.SIZE+stroke_width, stroke_width);
	}
	
	public static void removeVirtual(Tile[] tiles) {
		for(int i = 0; i < tiles.length; i++) {
			if(tiles[i] == Tile.VIRTUAL) tiles[i] = Tile.AIR;
		}
	}
	
	public static int fill(Tile[] tiles, Monster[] monsters, 
			int width, int height) {
		boolean[] clear = new boolean[tiles.length];
		
		for(Monster monster: monsters) {
			int start = (int)monster.y*width + (int)monster.x;
			check(start, clear, tiles, width, height);
		}
		
		int n_solid = 0;
		for(int i = 0; i < clear.length; i++) {
			if(clear[i]) {
				tiles[i] = Tile.AIR;
			}
			else {
				tiles[i] = Tile.SOLID;
				n_solid++;
			}
		}
		
		double div = (double)n_solid / (double) tiles.length;
		return (int) Math.round(div*100);
	}
	
	public static int getPercent(Tile[] tiles) {
		int n_solid = 0;
		for(int i = 0; i < tiles.length; i++) {
			if(tiles[i] == Tile.SOLID) n_solid++;	
		}
		return (int) (Math.round((n_solid / (double)tiles.length) * 100)); 
	}
	
	private static void check(int start, boolean[] clear, Tile[] tiles,
			int width, int height) {
		final int cap;
		boolean[] checked;
		int[] stack;
		int stack_pointer;
		
		cap = width * height;
		checked = new boolean[cap];
		stack = new int[cap];
		stack_pointer = 1;
		stack[0] = -1;
		stack[stack_pointer] = start;
		
		int n;
		do {
			int cur = stack[stack_pointer];
			checked[cur] = true;
			if(cur < 0 || cur >= cap);
			else if(tiles[cur] != Tile.AIR);
			else { // current tile empty
				clear[cur] = true;
				n = cur - width;
				if(n >= 0 && n < cap && !checked[n]) {
					stack[++stack_pointer] = n;
					continue;
				}
				n = cur + width;
				if(n >= 0 && n < cap && !checked[n]) {
					stack[++stack_pointer] = n;
					continue;
				}
				n = cur - 1;
				if(n >= 0 && n < cap && !checked[n]) {
					stack[++stack_pointer] = n;
					continue;
				}
				n = cur + 1;
				if(n >= 0 && n < cap && !checked[n]) {
					stack[++stack_pointer] = n;
					continue;
				}
			}
			stack_pointer--;
		} while(stack_pointer > 0);
	}
	
	static {
		Image heart_l;
		try {
			heart_l = ImageUtil.removeBackgrounnd(
					ImageIO.read(
							Resources.getResource("haptron.heart")),
					Color.green);
		} catch (IOException e) {
			Console.error(e);
			System.exit(1);
			heart_l = null;
		}
		heart = heart_l;
	}
}
