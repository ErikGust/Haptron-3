package haptron.monster;

import haptron.Direction;
import haptron.Tile;
import haptron.display.Renderable;
import haptron.events.Event;
import haptron.levels.Level;

import java.awt.Graphics2D;
import java.awt.Image;

public class Monster implements Renderable, Event {
	public static final boolean LEFT = false;
	public static final boolean RIGHT = true;
	public static final boolean UP = false;
	public static final boolean DOWN = true;
	
	public double x;
	public double y;
	public final double size;
	public final double speed;
	public final int damage;
	private Image image;
	
	public boolean dirX;
	public boolean dirY;
	public final int type;
	private final Level level;
	
	public Monster(MonsterData data, Level lvl) {
		x = 0;
		y = 0;
		dirX = LEFT;
		dirY = UP;
		speed = data.speed;
		size = data.size;
		damage = data.damage;
		image = data.image;
		type = data.ordinal();
		level = lvl;
	}
	
	private boolean coliding(Direction dir, double _x, double _y) {
		Tile tile;
		double x;
		double y;
		
		boolean coliding = false;
		switch(dir) {
		case LEFT:
			x = _x - size/2;
			y = _y - size/2;
			for(;y < _y + size/2; y+= Math.min(1, (_y + size/2) - y)) {
				if(!validPoint(x,y)) {
					coliding =  true;
					continue;
				}
				tile = level.at(x, y);
				switch(tile) {
				case VIRTUAL:
					level.damagePlayer();
				case SOLID:
					if(this.damage >= 1)
						level.set(level.indexAt(x, y), Tile.AIR);
					coliding = true;
				default:
					break;
				}
			}
			break;
		case RIGHT:
			x = _x + size/2;
			y = _y - size/2;
			for(;y < _y + size/2; y+= Math.min(1, (_y + size/2) - y)) {
				if(!validPoint(x,y)) {
					coliding =  true;
					continue;
				}
				tile = level.at(x, y);
				switch(tile) {
				case VIRTUAL:
					level.damagePlayer();
				case SOLID:
					if(this.damage >= 1)
						level.set(level.indexAt(x, y), Tile.AIR);
					coliding = true;
				default:
					break;
				}
			}
			break;
		case UP:
			x = _x - size/2;
			y = _y - size/2;
			for(; x < _x + size/2; x+= Math.min(1, (_x + size/2) - x)) {
				if(!validPoint(x,y)) {
					coliding =  true;
					continue;
				}
				tile = level.at(x, y);
				switch(tile) {
				case VIRTUAL:
					level.damagePlayer();
				case SOLID:
					if(this.damage >= 1)
						level.set(level.indexAt(x, y), Tile.AIR);
					coliding =  true;
				default:
					break;
				}
			}
			break;
		case DOWN:
			x = _x - size/2;
			y = _y + size/2;
			for(; x < _x + size/2; x+= Math.min(1, (_x + size/2) - x)) {
				if(!validPoint(x,y)) {
					coliding =  true;
					continue;
				}
				tile = level.at(x, y);
				switch(tile) {
				case VIRTUAL:
					level.damagePlayer();
				case SOLID:
					if(this.damage >= 1)
						level.set(level.indexAt(x, y), Tile.AIR);
					coliding = true;
				default:
					break;
				}
			}
			break;
		default:
			break;
		}
		return coliding;
	}
	
	private boolean validPoint(double x, double y) {
		if(x < 0) return false;
		else if(x >= level.width) return false;
		else if(y < 0) return false;
		else if(y >= level.height) return false;
		else return true;
	}

	@Override
	public synchronized boolean tick() {
		if(level == null) return false;
		double speedX = speed;
		double speedY = speed;
		if(dirX) {
			if(coliding(Direction.RIGHT, x+speedX, y)) {
				dirX = false;
			}
		} else {
			if(coliding(Direction.LEFT, x-speedX, y)) {
				dirX = true;
			}
		}
		if(dirY) {
			if(coliding(Direction.DOWN, x, y+speedY)) {
				dirY = false;
			}
		} else {
			if(coliding(Direction.UP, x, y-speedY)) {
				dirY = true;
			}
		}
		
		x += (dirX ? speedX : -speedX);
		y += (dirY ? speedY : -speedY);
		return true;
	}

	@Override
	public synchronized boolean render(Graphics2D g) {
		final double x = this.x - size / 2;
		final double y = this.y - size / 2;
		final double w = size;
		final double h = size;
		final Image i = image;
		
		g.drawImage(i, (int)(x*Tile.SIZE), (int)(y*Tile.SIZE), 
				(int)(w*Tile.SIZE), (int)(h*Tile.SIZE), null);
		
		return true;
	}
}
