package haptron.engine;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;

import haptron.display.Display;
import haptron.display.Screen;
import haptron.utils.ImageUtil;
import haptron.utils.Resources;

public class Haptron {
	public static final Image logo;
	public static final boolean debug;
	public static final int LEVEL_WIDTH = 40;
	public static final int LEVEL_HEIGHT = 28;
	static {
		debug = true;
	}
	
	private volatile boolean screen_changed;
	private volatile boolean closing;
	private volatile boolean resized;
	
	private final Display display;
	private Screen screen;
	private Thread thread;
	
	private volatile int height;
	private volatile int width;
	public volatile int fps;
	
	public Haptron(Display display) {
		this.screen_changed = false;
		this.closing = false;
		this.resized = false;
		this.display = display;
		this.closing = false;
		this.width = display.getWidth();
		this.height = display.getHeight();
		this.fps = 60;
		
		display.addComponentListener(new ResizeListener());
	}
	
	public Screen getScreen() {
		return screen;
	}
	
	public synchronized void setScreen(Screen screen) {
		Console.log("Changing screen, current Screen: ", this.screen, ", new Screen: ", screen);
		if(this.screen != null) {
			this.screen.close(display);
		}
		this.screen = null;
		System.gc();
		
		this.screen = screen;
		screen_changed = true;
	}
	
	private void run() {
		long lctms;
		@SuppressWarnings("unused")
		int fps;
		
		resize();
		
		while(!closing) {
			
			lctms = System.currentTimeMillis();
			
			synchronized(this) {
			
			if(screen_changed) {
				resized = true;
				screen.open(display);
				screen_changed = false;
			}
			
			if(resized) {
				if(screen != null) {
					screen.resize(width, height);
				}
				resized = false;
			}
			
			if(screen != null) {
				synchronized(display) {
					screen.render(display.getContext());
				}
			}
			
			}
			
			display.swapBuffers();
			display.requestFocus();
			
			try {
				Thread.sleep(1000/this.fps - (System.currentTimeMillis() - lctms));
			} catch (Exception e) {
				
			}
			
			fps = 1000 / (int) Math.max((System.currentTimeMillis() - lctms),1);
		}
		
		setScreen(null);
		
		Console.log("Thread terminated");
	}
	
	public synchronized void start() {
		if(thread != null) return;
		Console.log("Starting thread...");
		closing = false;
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Haptron.this.run();
			}
		});
		thread.start();
		Console.log("Thread started successfully");
	}
	
	public synchronized void stop() {
		if(thread == null) return;
		Console.log("Terminating thread");
		closing = true;
		
		thread.interrupt();
		
		thread = null;
	}
	
	public void join() throws InterruptedException {
		if(thread == null) return;
		thread.join();
	}

	public void resize() {
		final int width = display.getWidth();
		final int height = display.getHeight();
		this.resized = true;
		this.width = width;
		this.height = height;
	}
	
	private class ResizeListener extends ComponentAdapter {
		@Override
		public void componentShown(ComponentEvent e) {
			Haptron.this.resize();
		}
		
		@Override
		public void componentResized(ComponentEvent e) {
			Haptron.this.resize();
		}
	}
	
	public static Haptron create() {
		final Window window = new Window();
		window.setTitle("Haptron 2");
		Display display = new Display();
		final Haptron haptron = new Haptron(display);
		
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				haptron.stop();
				window.dispose();
			}
		});
		
		window.add(display);
		window.setVisible(true);
		
		haptron.start();
		
		return haptron;
	}

	public static Haptron create(Screen screen) {
		Haptron haptron = create();
		haptron.setScreen(screen);
		return haptron;
	}
	
	static {
		Image logo_l;
		try {
			logo_l = ImageUtil.removeBackground(
					ImageIO.read(Resources.getResource("haptron.haptron_logo")),
					Color.green);
		} catch (IOException e) {
			Console.error(e);
			System.exit(-1);
			logo_l = null;
		}
		logo = logo_l;
	}
}
