package haptron.levels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;

import haptron.Direction;
import haptron.Player;
import haptron.Tile;
import haptron.display.Button;
import haptron.display.Display;
import haptron.display.Screen;
import haptron.engine.Console;
import haptron.engine.Haptron;
import haptron.events.Event;
import haptron.menus.MainMenu;
import haptron.monster.Monster;
import haptron.monster.MonsterData;
import haptron.utils.Clock;
import haptron.utils.Timer;

public class Level implements Screen, KeyListener {
	private static final Font console;
	private final Event game_over;
	private final Event level_up;
	private final Event win;
	public final int width;
	public final int height;
	
	private int display_width;
	private int display_height;
	public final double player_spawn_x;
	public final double player_spawn_y;
	
	private final LevelData level_data;
	private int percent;
	private final int percent_threshold;
	
	private boolean fill;
	
	private final Player player;
	private final Tile[] data;
	private final Monster[] monsters;
	private boolean close;
	
	private final Button mainMenu;
	
	private final Haptron haptron;
	
	private final boolean[] keys;
	
	private Thread thread;
	
	private Timer timer;
	
	private Clock clock;
	
	private LevelBuilder fallback;
	
	public Level(LevelData level_data, LevelBuilder fallback) {
		this(level_data, 5, fallback.haptron);
		this.fallback = fallback;
		this.mainMenu.setText("Stop");
	}
	
	public Level(LevelData level_data, int lives, final Haptron haptron) {
		this.level_data = level_data;
		this.percent_threshold = level_data.getThreshold();
		this.width 	= level_data.WIDTH;
		this.height = level_data.HEIGHT;
		this.haptron = haptron;
		this.timer = null;
		this.clock = new Clock(Clock.INFINITE);
		
		this.data = new Tile[width * height];
		
		for(int i = 0; i < this.data.length; i++) {
			this.data[i] = Tile.values()[level_data.data(i)];
		}
		
		this.monsters = new Monster [level_data.getMonsterCount()];
		
		for(int i = 0; i < this.monsters.length; i++) {
			monsters[i] = new Monster(MonsterData.values.get(
					(int)level_data.getMonster(i)), this);
			monsters[i].x = level_data.getMonsterX(i);
			monsters[i].y = level_data.getMonsterY(i);
			monsters[i].dirX = level_data.getMonsterDirX(i);
			monsters[i].dirY = level_data.getMonsterDirY(i);
		}
		
		this.player_spawn_x = level_data.getPlayerSpawnX();
		this.player_spawn_y = level_data.getPlayerSpawnY();
		this.player = new Player(player_spawn_x, player_spawn_y, lives, this);
		
		this.thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Level.this.run();
			}
		});
		
		this.keys = new boolean[512];
		
		this.game_over = new Event() {
			@Override
			public boolean tick() {
				mainMenu();
				return false;
			}
		};
		this.win = new Event() {
			@Override
			public boolean tick() {
				mainMenu();
				return false;
			}
		};
		this.level_up = new Event() {
			@Override
			public boolean tick() {
				Level next = getNextLevel();
				haptron.setScreen(next);
				return false;
			}
		};
		
		Color color = new Color(0x102040);
		Font font = new Font("Arial", Font.BOLD, 16);
		
		
		this.mainMenu = new Button("Main Menu") {
			@Override
			public void buttonClicked() {
				mainMenu();
			}
		};
		
		this.mainMenu.setFont(font);
		this.mainMenu.setBackground(color);
	}
	
	public void fill() {
		fill = true;
	}
	
	public void updatePercent() {
		percent = LevelArt.getPercent(data);
	}
	
	public Tile get(int index) {
		return data[index];
	}
	
	public Tile at(double _x, double _y) {
		return data[indexAt(_x,_y)];
	}
	
	public int indexAt(double _x, double _y) {
		return ((int)_y) * width + ((int)_x);
	}

	public void set(int index, Tile tile) {
		data[index] = tile;
		updatePercent();
	}
	
	private void levelUp() {
		if(fallback != null) {
			mainMenu();
			return;
		}
		Console.log("Level Up");
		if(timer == null) {
			if(hasNextLevel())timer = new Timer(40, level_up);
			else timer = new Timer(40, win);
		}
	}
	public void damagePlayer() {
		player.damage();
		LevelArt.removeVirtual(data);
	}

	public void gameOver() {
		Console.log("Game Over");
		if(timer == null) {
			timer = new Timer(40, game_over);
		}
	}
	
	public void mainMenu() {
		if(fallback != null) {
			haptron.setScreen(fallback);
			return;
		}
		Console.log("Main Menu");
		haptron.setScreen(new MainMenu(haptron));
	}
	
	public Level getNextLevel() {
		return new Level(Levels.nextLevel(level_data), 
				player.getLives() < 5 ? player.getLives() + 1 
				: player.getLives(),
				haptron);
	}

	public boolean hasNextLevel() {
		return Levels.nextLevel(level_data) != null;
	}
	
	private void update() {
		clock.tick();
		
		if(timer != null) {
			if(!timer.tick()) 
				timer = null;
		} else {
			
		if(keys[KeyEvent.VK_UP]) 
			player.move(Direction.UP);
		
		if(keys[KeyEvent.VK_DOWN]) 
			player.move(Direction.DOWN);
		
		if(keys[KeyEvent.VK_LEFT]) 
			player.move(Direction.LEFT);
		
		if(keys[KeyEvent.VK_RIGHT]) 
			player.move(Direction.RIGHT);
		
		for(Monster m: monsters)
			m.tick();
		player.tick();
		
		if(fill) {
			percent = LevelArt.fill(data, monsters, width, height);
			if(percent >= percent_threshold) levelUp();
			fill = false;
		}
		
		}
	}

	private void run() {
		long lctms;
		while(!close) {
			lctms = System.currentTimeMillis();
			update();
			try {
				Thread.sleep(1000/25 - (System.currentTimeMillis() - lctms));
			} catch (Exception e) {
				
			}
		}
	}
	
	@Override
	public void open(Display parent) {
		close = false;
		thread.start();
		parent.addKeyListener(this);
		mainMenu.open(parent);
		updatePercent();
	}

	@Override
	public void close(Display parent) {
		close = true;
		thread.interrupt();
		parent.removeKeyListener(this);
		mainMenu.close(parent);
	}

	@Override
	public void resize(int width, int height) {
		display_width = width;
		display_height = height;
		mainMenu.setBounds(width - 130, height - 60, 110, 40);
	}

	@Override
	public synchronized boolean render(Graphics2D g) {
		final int X = display_width / 2 - ((width * Tile.SIZE) / 2);
		final int Y = display_height / 2 - ((height * Tile.SIZE) / 2);
		
		LevelArt.drawBackground(g, display_width, display_height, width, height);
		
		g.translate(X, Y);
	
		for(int i = 0; i < data.length; i++) {
			Tile v = data[i];
			int y = (i / width)*Tile.SIZE;
			int x = (i % width)*Tile.SIZE;
			v.render(g, x, y);
		}
		
		for(Monster m: monsters) 
			m.render(g);
		
		player.render(g);
		
		g.translate(-X,  -Y);

		g.setFont(console);
		
		if(timer != null) {
			if(timer.on_complete == game_over) {
				String game_over_str = "Game Over!";
				Rectangle2D string_bounds = console.getStringBounds(game_over_str, 
						g.getFontRenderContext());
				
				g.setColor(new Color(0,0,0));
				g.fillRect(0, 0, display_width, display_height);
				g.setColor(Color.yellow);
				g.drawString(game_over_str, 
						display_width/2 - (int)string_bounds.getWidth()/2,
						display_height/2 - (int)string_bounds.getHeight()/2);
			}
			else
			if(timer.on_complete == level_up) {
				String level_up_str = "Level Up!";
				Rectangle2D string_bounds = console.getStringBounds(level_up_str, 
						g.getFontRenderContext());
				g.setColor(new Color(0,0,0,100));
				g.fillRect(0, 0, display_width, display_height);
				g.setColor(Color.green);
				g.drawString(level_up_str, 
						display_width/2 - (int)string_bounds.getWidth()/2,
						display_height/2 - (int)string_bounds.getHeight()/2);
			}
			else 
			if(timer.on_complete == win) {
				String win_str = "You Won!";
				Rectangle2D string_bounds = console.getStringBounds(win_str, 
						g.getFontRenderContext());
				g.setColor(new Color(0,0,0,100));
				g.fillRect(0, 0, display_width, display_height);
				g.setColor(Color.green);
				g.drawString(win_str, 
						display_width/2 - (int)string_bounds.getWidth()/2,
						display_height/2 - (int)string_bounds.getHeight()/2);
			}
		}
		
		int h_x = 10;
		int h_y = 10;
		int h_w = 32;
		int h_h = 32;
		for(int i = 0; i < player.getLives(); i++) {
			g.drawImage(LevelArt.heart, h_x, h_y, h_w, h_h, null);
			h_x += h_w + 10;
		}
		
		drawValues(g);
		
		mainMenu.render(g);
		
		return true;
	}
	
	private void drawValues(Graphics2D g) {
		g.setFont(console);
		
		int y = 0;
		
		final String percent = this.percent + "%";
		final String percent_threshold = this.percent_threshold + "%";
		
		Rectangle2D string_size = console.getStringBounds(
				percent, g.getFontRenderContext());
		
		y += (string_size.getHeight() + 10);
		g.translate(0, string_size.getHeight() + 10);
		
		g.setColor(Color.yellow);
		g.drawString(percent, display_width - (int)string_size.getWidth() - 10, 0);

		string_size = console.getStringBounds(
				percent_threshold, g.getFontRenderContext());

		y += (string_size.getHeight() + 10);
		g.translate(0, string_size.getHeight() + 10);
		
		g.setColor(Color.green);
		g.drawString(percent_threshold, display_width - (int)string_size.getWidth() - 10, 0);
		g.translate(0, -y);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) { }
	
	static {
		console = new Font("Arial", Font.BOLD, 26);
	}
}
