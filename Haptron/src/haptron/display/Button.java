package haptron.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class Button implements Screen, MouseListener, MouseMotionListener {
	private int x;
	private int y;
	private int width;
	private int height;
	private String text;
	private Font font;
	private Color background;
	private Color foreground;
	private boolean hover;
	private boolean press;
	
	public Button() {
		width = 100;
		height = 80;
		x = 10;
		y = 10;
	}
	
	public Button(String text) {
		this();
		setText(text);
	}
	
	@Override
	public synchronized boolean render(Graphics2D g) {
		final int x = this.x;
		final int y = this.y;
		final int width = this.width;
		final int height = this.height;
		final String text = this.text != null ? this.text : "";
		final Font font = this.font != null ? this.font : g.getFont();
		Color bg = this.background != null ? this.background : Color.gray;
		Color fg = this.foreground != null ? this.foreground : Color.white;
		final int border_width = 10;
		final int string_width;
		final int string_height;
		
		if(hover) {
			bg = bg.brighter();
		}
		
		g.setFont(font);
		g.setStroke(new BasicStroke(border_width));
		
		string_width = (int) font.getStringBounds(text, g.getFontRenderContext()).getWidth();
		string_height = (int) font.getStringBounds(text, g.getFontRenderContext()).getHeight();
		
		g.translate(x, y);
		g.setColor(bg);
		
		g.fillRect(0, 0, width, height);
		
		g.setColor(press ? bg : bg.brighter());
		g.drawLine(border_width / 2, border_width / 2, width - border_width / 2, border_width / 2);
		g.drawLine(border_width / 2, border_width / 2, border_width / 2, height - border_width / 2);
		
		g.setColor(press ? bg : bg.darker());
		g.drawLine(border_width / 2, height - border_width / 2, width - border_width / 2, height-border_width/2);
		g.drawLine(width-border_width/2, border_width / 2, width-border_width/2, height - border_width / 2);
		
		g.setColor(fg);
		
		g.drawString(text, width/2 - string_width/2, height/2 + string_height/3);
		
		g.translate(-x, -y);
		
		return true;
	}
	
	public synchronized void setBackground(Color c) {
		this.background = c;
	}
	
	public Color getBackground() {
		return background;
	}
	
	public synchronized void setForeground(Color c) {
		this.foreground = c;
	}
	
	public Color getForeground() {
		return foreground;
	}
	
	public int getX() {
		return x;
	}
	
	public synchronized void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public synchronized void setY(int y) {
		this.y = y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public synchronized void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}
	
	public Dimension getSize() {
		return new Dimension(width, height);
	}
	
	public synchronized void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public synchronized void setSize(Dimension size) {
		this.width = size.width;
		this.height = size.height;
	}
	
	public synchronized void setHeight(int height) {
		this.height = height;
	}

	public synchronized void setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public synchronized void setBounds(Rectangle r) {
		setBounds(r.x, r.y, r.width, r.height);
	}

	public synchronized void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public synchronized void setLocation(Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	public String getText() {
		return text;
	}

	public synchronized void setText(String text) {
		this.text = text;
	}
	
	public Font getFont() {
		return font;
	}
	
	public synchronized void setFont(Font font) {
		this.font = font;
	}
	
	public abstract void buttonClicked();

	@Override
	public void open(Display parent) {
		parent.addMouseListener(this);
		parent.addMouseMotionListener(this);
	}

	@Override
	public void close(Display parent) {
		parent.removeMouseListener(this);
		parent.removeMouseMotionListener(this);
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		hover = (x <= e.getX() && e.getX() <= x+width && y <= e.getY() && e.getY() <= y+height);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		hover = (x <= e.getX() && e.getX() <= x+width && y <= e.getY() && e.getY() <= y+height);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)
			press = hover;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(press && hover) buttonClicked();
			press = false;
		}
	}
}
