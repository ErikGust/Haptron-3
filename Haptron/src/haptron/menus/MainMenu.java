package haptron.menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import haptron.display.Button;
import haptron.display.Display;
import haptron.display.Screen;
import haptron.engine.Haptron;
import haptron.levels.LevelArt;
import haptron.levels.LevelBuilder;
import haptron.levels.Levels;

public class MainMenu implements Screen {
	private final Haptron haptron;
	private final Button new_game;
	private final Button create_level;
	private final Button options;
	private final Button help;
	private int display_width;
	private int display_height;
	
	public MainMenu(Haptron haptron) {
		this.haptron = haptron;
		Color color = new Color(0x102040);
		Font font = new Font("Arial", Font.BOLD, 20);
		
		this.new_game = new Button("New Game") {
			@Override
			public void buttonClicked() {
				newGame();
			}
		};
		
		this.create_level = new Button("Create Level") {
			@Override
			public void buttonClicked() {
				createLevel();
			}
		};
		
		this.options = new Button("Options") {
			@Override
			public void buttonClicked() {
				
			}
		};
		
		this.help = new Button("About") {
			@Override
			public void buttonClicked() {
				
			}
		};
		
		this.new_game.setBackground(color);
		this.new_game.setFont(font);
		this.new_game.setSize(250, 60);
		this.create_level.setBackground(color);
		this.create_level.setFont(font);
		this.create_level.setSize(250, 60);
		this.options.setBackground(color);
		this.options.setFont(font);
		this.options.setSize(120, 60);
		this.help.setBackground(color);
		this.help.setFont(font);
		this.help.setSize(120, 60);
	}
	
	private void createLevel() {
		haptron.setScreen(new LevelBuilder(haptron));
	}
	
	private void newGame() {
		haptron.setScreen(Levels.newGame(haptron));
	}

	@Override
	public void open(Display parent) {
		new_game.open(parent);
		create_level.open(parent);
		options.open(parent);
		help.open(parent);
	}

	@Override
	public void close(Display parent) {
		new_game.close(parent);
		create_level.close(parent);
		options.close(parent);
		help.close(parent);
	}

	@Override
	public void resize(int width, int height) {
		display_width = width;
		display_height = height;
		new_game.setLocation(width/2 - new_game.getWidth()/2, 
				height/2 - new_game.getHeight()/2);
		create_level.setLocation(new_game.getX(), 
				new_game.getY() + new_game.getHeight() + 10);
		options.setLocation(new_game.getX(), 
				create_level.getY() + create_level.getHeight() + 10);
		help.setLocation(new_game.getX() + new_game.getWidth() - help.getWidth(),
				create_level.getY() + create_level.getHeight() + 10);
	}

	@Override
	public boolean render(Graphics2D g) {
		LevelArt.drawBackground(g, display_width, display_height);
		
		final int logo_width = Haptron.logo.getWidth(null);
		final int logo_heigth = Haptron.logo.getHeight(null);
		
		g.drawImage(Haptron.logo, 
				display_width/2 - (int)(logo_width*1.5)/2,
				(int)(display_height * 0.2), (int)(logo_width * 1.5), (int)(logo_heigth * 1.5), null);
		
		new_game.render(g);
		create_level.render(g);
		options.render(g);
		help.render(g);
		return false;
	}

}
