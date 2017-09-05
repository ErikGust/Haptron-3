package haptron;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.imageio.ImageIO;

import haptron.display.Renderable;
import haptron.engine.Console;
import haptron.events.Event;
import haptron.levels.Level;
import haptron.utils.ImageUtil;
import haptron.utils.Resources;

public class Player implements Renderable, Event {
	public static final int SIZE = 1;
	private static final Image haptron;
	public double x;
	public double y;
	private int lives;
	private MovementAnimation anim;
	private final Level level;
	private boolean update_fill;
	
	public Player(double x, double y, int lives, Level level) {
		this.x = x;
		this.y = y;
		this.lives = lives;
		this.level = level;
	}

	@Override
	public boolean tick() {
		if(anim != null) {
			if(!anim.tick()) {
				if(level.at(x,y) != Tile.SOLID) {
					Direction dir = anim.direction;
					anim = null;
					if(!move(dir))
						level.fill();
				}
				else {
					anim = null;
					if(update_fill) {
						level.fill();
						update_fill = false;
					}
				}
			}
			if(level.at(x,y) == Tile.AIR) {
				level.set(level.indexAt(x, y), Tile.VIRTUAL);
				update_fill = true;
			}
		} else if(level.at(x,y) != Tile.SOLID) {
			if(!move(Direction.UP))
				level.fill();
		}
		
		return true;
	}

	@Override
	public boolean render(Graphics2D g) {
		final int x = (int)((this.x-0.5) * Tile.SIZE);
		final int y = (int)((this.y-0.5) * Tile.SIZE);
		final int width = Tile.SIZE;
		final int height = Tile.SIZE;
		
		g.drawImage(haptron, x, y, width, height, null);
		
		return true;
	}

	public int getLives() {
		return lives;
	}

	public boolean move(Direction dir) {
		if(anim == null) {
			if(x <= 0.5 && dir == Direction.LEFT) {
				return false;
			}
			if(x >= level.width - 0.5 && dir == Direction.RIGHT) {
				return false;
			}
			if(y <= 0.5 && dir == Direction.UP) {
				return false;
			}
			if(y >= level.height - 0.5 && dir == Direction.DOWN) {
				return false;
			}
			anim = new MovementAnimation(dir);
			return true;
		}
		return false;
	}

	static {
		Image img;
		try {
			img = ImageUtil.removeBackgrounnd(
					ImageIO.read(
							Resources.getResource("haptron.haptron")),
					Color.green);
		} catch (Exception e) {
			Console.error(e);
			System.exit(-1);
			img = null;
		}
		haptron = img;
	}
	
	private class MovementAnimation implements Event {
		private static final double speed = 0.5;
		private final Direction direction;
		private double time;
		
		public MovementAnimation(Direction direction) {
			this.direction = direction;
			time = 0;
		}
		
		@Override
		public boolean tick() {
			switch(direction) {
			case UP:
				y -= speed;
				break;
			case DOWN:
				y += speed;
				break;
			case LEFT:
				x -= speed;
				break;
			case RIGHT:
				x += speed;
				break;
			default:
				break;
			}
			
			time++;
			return time < (1/speed);
		}
	}

	public void damage() {
		anim = null;
		x = level.player_spawn_x;
		y = level.player_spawn_y;
		lives--;
		if(lives <= 0) level.gameOver();
	}
	
	public static Image getImage() {
		return haptron;
	}
}
