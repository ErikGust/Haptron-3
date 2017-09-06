package haptron.levels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import haptron.Player;
import haptron.Tile;
import haptron.display.Button;
import haptron.display.Display;
import haptron.display.ImageRectangle;
import haptron.display.Screen;
import haptron.engine.Console;
import haptron.engine.Haptron;
import haptron.menus.MainMenu;
import haptron.monster.Monster;
import haptron.monster.MonsterData;

public class LevelBuilder implements Screen  {
	private static final Font console;

	public int width;
	public int height;
	
	private int display_width;
	private int display_height;
	
	private double player_spawn_x;
	private double player_spawn_y;
	
	private Tile[] data;
	
	private Monster selected_monster;
	
	private ArrayList<Monster> monsters;
	
	private boolean remove;
	
	private boolean editing_spawn;
	
	private boolean editing_threshold;
	
	private Point point1;
	private Point point2;
	
	private final Button save;
	
	private final Button open;
	
	private final Button newLevel;
	
	private final Button mainMenu;
	
	private final Button test;
	
	private ImageRectangle[] monsters_options;
	
	private Display display;
	
	private ImageRectangle chosen_monster;
	
	private int threshold;
	
	private final MouseInputListener mouseListener;
	
	private final KeyboardListener keyboard;

	private int percent_solid;
	
	final Haptron haptron;
	
	public LevelBuilder(Haptron haptron) {
		this.mouseListener = this.new MouseInputListener();
		this.keyboard = this.new KeyboardListener();
		this.chosen_monster = null;
		this.haptron = haptron;
		
		Color color = new Color(0x102040);
		Font font = new Font("Arial", Font.BOLD, 16);
		
		save = new Button("Save") {
			@Override
			public void buttonClicked() {
				LevelBuilder.this.save();
			}
		};
		open = new Button("Open") {
			@Override
			public void buttonClicked() {
				LevelBuilder.this.open();
			}
		};
		newLevel = new Button("New") {
			@Override
			public void buttonClicked() {
				LevelBuilder.this.newLevel();
			}
		};
		mainMenu = new Button("Main Menu") {
			@Override
			public void buttonClicked() {
				LevelBuilder.this.mainMenu();
			}
		};
		test = new Button("Test") {
			@Override
			public void buttonClicked() {
				LevelBuilder.this.haptron.setScreen(new Level(getLevelData(), LevelBuilder.this));
			}
		};
		save.setBackground(color);
		save.setFont(font);
		open.setBackground(color);
		open.setFont(font);
		newLevel.setBackground(color);
		newLevel.setFont(font);
		mainMenu.setBackground(color);
		mainMenu.setFont(font);
		test.setBackground(color);
		test.setFont(font);
		
		monsters_options = new ImageRectangle[MonsterData.values().length];
		for(int i = 0; i < monsters_options.length; i++) {
			MonsterData m = MonsterData.values()[i];
			monsters_options[i] = new ImageRectangle(m.image);
			monsters_options[i].setSize((int)(m.size*Tile.SIZE), (int)(m.size*Tile.SIZE));
			monsters_options[i].extra = (Object) m;
		}
		
		newLevel();
		
	}
	
	private void mainMenu() {
		haptron.setScreen(new MainMenu(haptron));
	}

	protected void newLevel() {
		final int width = Haptron.LEVEL_WIDTH;
		final int height = Haptron.LEVEL_HEIGHT;
		final byte[] data = new byte[width*height];
		LevelData _new = new LevelData(width, height, data, 0, new byte[0], 
				new double[0], new double[0], new boolean[0], 
				new boolean[0], 0.5, 0.5, (byte)80);
		setLevelData(_new);
	}

	public LevelBuilder(Haptron haptron, LevelData data) {
		this(haptron);
		setLevelData(data);
	}
	
	private void open() {
		final File file;
		
		JFileChooser fc = new JFileChooser();
		int result = fc.showOpenDialog(display);
		if(result == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
		} else return;
		
		try {
			this.setLevelData(LevelData.read(new FileInputStream(file)));
		} catch (Exception e) {
			Console.error(e);
		}
	}

	private void save() {
		final File file;
		
		JFileChooser fc = new JFileChooser();
		int result = fc.showSaveDialog(display);
		if(result == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
		} else return;
		
		try {
			if(!file.exists()) file.createNewFile();
			LevelData.write(new FileOutputStream(file), getLevelData());
		} catch (Exception e) {
			Console.error(e);
		}
	}

	private LevelData getLevelData() {
		
		final int w = width;
		final int h = height;
		final int nmonsters = monsters.size();
		
		
		final byte[] data = new byte[w*h];
		final byte[] mnst = new byte[nmonsters];
		final double[] mx = new double[nmonsters];
		final double[] my = new double[nmonsters];
		final boolean[] mdy = new boolean[nmonsters];
		final boolean[] mdx = new boolean[nmonsters];
		final double psx = player_spawn_x;
		final double psy = player_spawn_y;
		final byte threshold = (byte) this.threshold;
		
		for(int i = 0; i < data.length; i++) {
			data[i] = (byte) this.data[i].ordinal();
		}
		
		for(int i = 0; i < nmonsters; i++) {
			mnst[i] = (byte)monsters.get(i).type;
			mx[i] = monsters.get(i).x;
			my[i] = monsters.get(i).y;
			mdx[i] = monsters.get(i).dirX;
			mdy[i] = monsters.get(i).dirY;
		}
		
		return new LevelData(w, h, data, nmonsters, 
				mnst, mx, my, mdx, mdy, psx, psy, threshold);
	}

	public synchronized void setLevelData(LevelData data) {
		this.monsters = new ArrayList<Monster>();
		this.width = data.WIDTH;
		this.height = data.HEIGHT;
		this.data = new Tile[width * height];
		
		for(int i = 0; i < this.data.length; i++) {
			this.data[i] = Tile.values()[data.data(i)];
		}
		
		for(int i = 0; i < data.getMonsterCount(); i++) {
			Monster monster = new Monster(
					MonsterData.values()[(int)data.getMonster(i)], null);
			monster.x = data.getMonsterX(i);
			monster.y = data.getMonsterY(i);
			monster.dirX = data.getMonsterDirX(i);
			monster.dirY = data.getMonsterDirY(i);
			monsters.add(monster);
		}
		
		player_spawn_x = data.getPlayerSpawnX();
		player_spawn_y = data.getPlayerSpawnY();
		
		this.threshold = data.getThreshold();
	}

	private void pushMonster() {
		if(chosen_monster == null) return;
		final int X = display_width / 2 - ((width * Tile.SIZE) / 2);
		final int Y = display_height / 2 - ((height * Tile.SIZE) / 2);
		
		double x = (chosen_monster.getCenterX() - X) / Tile.SIZE;
		double y = (chosen_monster.getCenterY() - Y) / Tile.SIZE;
		
		if(x < 0 || x > width || y < 0 || y > height) 
			return;
		
		MonsterData d = (MonsterData) chosen_monster.extra;
		Monster monster = new Monster(d, null);
		monster.x = x;
		monster.y = y;
		monsters.add(monster);
	}
	
	@Override
	public void open(Display parent) {
		parent.addKeyListener(keyboard);
		parent.addMouseMotionListener(mouseListener);
		parent.addMouseListener(mouseListener);
		save.open(parent);
		open.open(parent);
		newLevel.open(parent);
		mainMenu.open(parent);
		test.open(parent);
	}

	@Override
	public void close(Display parent) {
		parent.removeKeyListener(keyboard);
		parent.removeMouseMotionListener(mouseListener);
		parent.removeMouseListener(mouseListener);
		save.close(parent);
		open.close(parent);
		newLevel.close(parent);
		mainMenu.close(parent);
		test.close(parent);
	}

	@Override
	public void resize(int width, int height) {
		display_width = width;
		display_height = height;
		save.resize(width, height);
		open.resize(width, height);
		
		save.setBounds(width - 100, height - 60, 80, 40);
		open.setBounds(width - 200, height - 60, 80, 40);
		newLevel.setBounds(width - 300, height - 60, 80, 40);
		mainMenu.setBounds(width - 450, height - 60, 130, 40);
		test.setBounds(width - 550, height - 60, 80, 40);
		
		int x = 10;
		for(ImageRectangle r: monsters_options) {
			r.setLocation(x, 60);
			x += (r.getWidth() + 10);
		}
	}
	
	@Override
	public synchronized boolean render(Graphics2D g) {
		final int X = display_width / 2 - ((width * Tile.SIZE) / 2);
		final int Y = display_height / 2 - ((height * Tile.SIZE) / 2);
		
		LevelArt.drawBackground(g, display_width, display_height, width, height);
		
		g.translate(X, Y);
		
		if(data != null)
		for(int i = 0; i < data.length; i++) {
			Tile v = data[i];
			int y = (i / width)*Tile.SIZE;
			int x = (i % width)*Tile.SIZE;
			
			v.render(g, x, y);
		}
		
		drawSelected(g);
		
		if(monsters != null)
		for(Monster m: monsters) {
			if(m == selected_monster) {
				g.setColor(Color.green);
				g.fillRect((int)((m.x - m.size)*Tile.SIZE), 
						(int)((m.y - m.size)*Tile.SIZE), 
						(int)(m.size*Tile.SIZE*2), 
						(int)(m.size*Tile.SIZE*2));
			}
			m.render(g);
		}
		
		if(editing_spawn) 
			g.drawImage(
				Player.getImage(),
				(int)((player_spawn_x-0.5)*Tile.SIZE),
				(int)((player_spawn_y-0.5)*Tile.SIZE), 
				Tile.SIZE, Tile.SIZE, null);
		
		g.translate(-X, -Y);

		save.render(g);
		open.render(g);
		newLevel.render(g);
		mainMenu.render(g);
		test.render(g);
		
		for(ImageRectangle r: monsters_options) r.render(g);
		
		if(chosen_monster != null)
			chosen_monster.render(g);
		
		String chosen_monster_info = "Chosen monster: dirX=" + 
		(selected_monster == null ? "null" : "" + (selected_monster.dirX ? "right" : "left")) + 
		", dirY=" +
		(selected_monster == null ? "null" : "" + (selected_monster.dirY ? "down" : "up"));
		
		String spawn_location = "Spawn point: [" + player_spawn_x + ',' + player_spawn_y + ']';
		
		String percent_filled = percent_solid + "% filled";
		
		String percent_threshold = "threshold: " + threshold + "%";
		
		g.setFont(console);
		g.setColor(Color.white);
		g.drawString(chosen_monster_info, 10, 20);
		g.drawString(spawn_location, 10, 40);
		g.drawString(percent_filled,
				display_width - g.getFontMetrics()
				.stringWidth(percent_filled) - 10, 20);
		g.drawString(percent_threshold,
				display_width - g.getFontMetrics()
				.stringWidth(percent_threshold) - 10, 40);
		return true;
	}
	
	private synchronized void drawSelected(Graphics2D g) {
		if(point1 == null || point2 == null) return;
		final int X = display_width / 2 - ((width * Tile.SIZE) / 2);
		final int Y = display_height / 2 - ((height * Tile.SIZE) / 2);
		
		int minX = (int) (Math.min(point1.x, point2.x) - X) / Tile.SIZE;
		int maxX = (int) (Math.max(point1.x, point2.x) - X) / Tile.SIZE;
		int minY = (int) (Math.min(point1.y, point2.y) - Y) / Tile.SIZE;
		int maxY = (int) (Math.max(point1.y, point2.y) - Y) / Tile.SIZE;
		
		if(maxX < 0 || minX >= width || maxY < 0 || minY >= height) return;

		minX = Math.max(Math.min(minX, width - 1), 0);
		minY = Math.max(Math.min(minY, height - 1), 0);
		maxX = Math.max(Math.min(maxX, width - 1), 0);
		maxY = Math.max(Math.min(maxY, height - 1), 0);
		
		int x;
		int y;
		for(int i = minX; i <= maxX; i++) {
			for(int j = minY; j <= maxY; j++) {
				x = i * Tile.SIZE;
				y = j * Tile.SIZE;
				(remove ? Tile.AIR : Tile.SELECTED).render(g, x, y);
			}
		}
	}
	
	private synchronized void fillRectangle(int minX, int minY, int maxX, int maxY) {
		if(data == null) return;
		final int X = display_width / 2 - ((width * Tile.SIZE) / 2);
		final int Y = display_height / 2 - ((height * Tile.SIZE) / 2);
		
		minX = (minX - X) / Tile.SIZE;
		minY = (minY - Y) / Tile.SIZE;
		maxX = (maxX - X) / Tile.SIZE;
		maxY = (maxY - Y) / Tile.SIZE;
		
		if(maxX < 0 || minX >= width || maxY < 0 || minY >= height) return;
		
		minX = Math.max(Math.min(minX, width - 1), 0);
		minY = Math.max(Math.min(minY, height - 1), 0);
		maxX = Math.max(Math.min(maxX, width - 1), 0);
		maxY = Math.max(Math.min(maxY, height - 1), 0);
		
		for(int i = minX; i <= maxX; i++) {
			for(int j = minY; j <= maxY; j++) {
				int index = j * width + i;
				if(data.length > index)
				data[j * width + i] = remove ? Tile.AIR : Tile.SOLID;
			}
		}
		
		this.percent_solid = LevelArt.getPercent(data);
	}
	
	public class KeyboardListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_S) editing_spawn = true;
			if(e.getKeyCode() == KeyEvent.VK_P) editing_threshold = true;
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if(editing_threshold) break;
				if(editing_spawn) {
					if(player_spawn_x - 1 >= 0)player_spawn_x--;
					break;
				}
				if(selected_monster != null) 
					selected_monster.dirX = false;
				break;
			case KeyEvent.VK_RIGHT:
				if(editing_threshold) break;
				if(editing_spawn) {
					if(player_spawn_x + 1 < width)player_spawn_x++;
					break;
				}
				if(selected_monster != null) 
					selected_monster.dirX = true;
				break;
			case KeyEvent.VK_UP:
				if(editing_threshold) {
					if(threshold < 95) threshold++;
					break;
				}
				if(editing_spawn) {
					if(player_spawn_y - 1 >= 0)player_spawn_y--;
					break;
				}
				if(selected_monster != null) 
					selected_monster.dirY = false;
				break;
			case KeyEvent.VK_DOWN:
				if(editing_threshold) {
					if(threshold > 20) threshold--;
					break;
				}
				if(editing_spawn) {
					if(player_spawn_y + 1 < height)player_spawn_y++;
					break;
				}
				if(selected_monster != null) 
					selected_monster.dirY = true;
				break;
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_S) editing_spawn = false;
			if(e.getKeyCode() == KeyEvent.VK_P) editing_threshold = false;
		}
		@Override
		public void keyTyped(KeyEvent e) {
			
		}
	}
	
	public class MouseInputListener extends MouseAdapter {
		@Override
		public void mouseDragged(MouseEvent e) {
			
			if(chosen_monster != null) {
				chosen_monster.setLocation(
						e.getX() - chosen_monster.width/2, 
						e.getY() - chosen_monster.height/2);
				return;
			}
			
			synchronized(LevelBuilder.this) {
				if(point2 != null) 
					point2.setLocation(e.getPoint());
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			final int X = display_width / 2 - ((width * Tile.SIZE) / 2);
			final int Y = display_height / 2 - ((height * Tile.SIZE) / 2);
			switch(e.getButton()) {
			case MouseEvent.BUTTON1:
				for(Monster m: monsters) {
					if(Math.abs(e.getX() - (m.x*Tile.SIZE + X)) < m.size*Tile.SIZE/2
					&& Math.abs(e.getY() - (m.y*Tile.SIZE + Y)) < m.size*Tile.SIZE/2) {
						selected_monster = m;
						return;
					}
				}
				selected_monster = null;
				for(ImageRectangle r: monsters_options) {
					if(r.contains(e.getPoint())) {
						chosen_monster = r.copy();
					}
				}
				if(chosen_monster != null) return;
				remove = false;
				break;
			case MouseEvent.BUTTON2:
			case MouseEvent.BUTTON3:
				for(Monster m: monsters) {
					if(Math.abs(e.getX() - (m.x*Tile.SIZE + X)) < m.size*Tile.SIZE/2
					&& Math.abs(e.getY() - (m.y*Tile.SIZE + Y)) < m.size*Tile.SIZE/2) {
						monsters.remove(m);
						return;
					}
				}
				remove = true;
				break;
			default:
				remove = false;
				break;
			}
			synchronized(LevelBuilder.this) {
				point1 = new Point(e.getPoint());
				point2 = new Point(e.getPoint());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			synchronized(LevelBuilder.this) {
				if(chosen_monster != null) {
					pushMonster();
					chosen_monster = null;
				}
				if(point1 == null || point2 == null) return;
				int minX = Math.min(point1.x, point2.x);
				int maxX = Math.max(point1.x, point2.x);
				int minY = Math.min(point1.y, point2.y);
				int maxY = Math.max(point1.y, point2.y);
				LevelBuilder.this.fillRectangle(minX, minY,
						maxX, maxY);
				point1 = null;
				point2 = null;
			}
		}
	
	}
	
	static {
		console = new Font("Arial", Font.PLAIN, 14);
	}
	
	public static void entry(String... args) {
		Haptron haptron = Haptron.create();
		LevelBuilder level_builder = new LevelBuilder(haptron);
		level_builder.newLevel();
		haptron.setScreen(level_builder);
	}
}
