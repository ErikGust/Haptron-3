package haptron.levels;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LevelData {
	public final int WIDTH;
	public final int HEIGHT;
	
	private final byte[] tiles;
	private final int n_monsters;
	private final byte[] monsters;
	private final double[] monster_x;
	private final double[] monster_y;
	private final boolean[] monster_dir_x;
	private final boolean[] monster_dir_y;
	private final double player_spawn_x;
	private final double player_spawn_y;
	private final byte threshold;
	
	public LevelData(int width, int height, byte[] tiles, int n_monsters, byte[] monsters, 
			double[] monster_x, double[] monster_y, boolean[] monster_dir_x, boolean[] monster_dir_y, 
			double player_spawn_x, double player_spawn_y, byte threshold) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.tiles = tiles;
		this.n_monsters = n_monsters;
		this.monsters = monsters;
		this.monster_x = monster_x;
		this.monster_y = monster_y;
		this.monster_dir_x = monster_dir_x;
		this.monster_dir_y = monster_dir_y;
		this.player_spawn_x = player_spawn_x;
		this.player_spawn_y = player_spawn_y;
		this.threshold = threshold;
	}
	
	public double getPlayerSpawnX() {
		return player_spawn_x;
	}
	
	public double getPlayerSpawnY() {
		return player_spawn_y;
	}
	
	public int getMonsterCount() {
		return n_monsters;
	}
	
	public byte getMonster(int m) {
		return monsters[m];
	}
	
	public double getMonsterX(int m) {
		return monster_x[m];
	}
	
	public double getMonsterY(int m) {
		return monster_y[m];
	}
	
	public boolean getMonsterDirX(int m) {
		return monster_dir_x[m];
	}
	
	public boolean getMonsterDirY(int m) {
		return monster_dir_y[m];
	}
	
	public byte data(int i) {
		return tiles[i];
	}
	
	public byte at(int x, int y) {
		final int X = x / 50;
		final int Y = y / 50;
		return tiles[X + Y * WIDTH];
	}

	public byte getThreshold() {
		return threshold;
	}
	
	@Override
	public String toString() {
		String string = getClass().getName() + "[";
		string += "width:" + WIDTH;
		string += ",height:" + HEIGHT;
		string += ",data:[";
		for(int i = 0; i < tiles.length; i++) {
			if(i != 0) string += ",";
			string += tiles[i];
		}
		string += "]]";
		return string;
	}
	
	public static void write(OutputStream _out, LevelData l) throws IOException {
		DataOutputStream out = new DataOutputStream(_out);
		out.writeInt(l.WIDTH);
		out.writeInt(l.HEIGHT);
		out.writeInt(l.n_monsters);
		
		for(int i = 0; i < l.tiles.length; i++) {
			out.writeByte(l.tiles[i]);
		}
		
		for(int i = 0; i < l.n_monsters; i++) {
			out.writeByte(l.monsters[i]);
		}
		
		for(int i = 0; i < l.n_monsters; i++) {
			out.writeDouble(l.monster_x[i]);
			out.writeDouble(l.monster_y[i]);
		}
		
		for(int i = 0; i < l.n_monsters; i++) {
			out.writeBoolean(l.monster_dir_x[i]);
			out.writeBoolean(l.monster_dir_y[i]);
		}
		
		out.writeDouble(l.player_spawn_x);
		out.writeDouble(l.player_spawn_y);
		
		out.writeByte(l.threshold);
	}
	
	public static LevelData read(InputStream _in) throws IOException {
		DataInputStream in = new DataInputStream(_in);
		
		final int width = in.readInt();
		final int height = in.readInt();
		final int nmonst = in.readInt();
		
		byte[] data = new byte[width * height];
		
		for(int i = 0; i < data.length; i++) {
			data[i] = in.readByte();
		}
		
		byte[] monsters = new byte[nmonst];
		
		for(int i = 0; i < monsters.length; i++) {
			monsters[i] = in.readByte();
		}
		
		double[] monster_x = new double[nmonst];
		double[] monster_y = new double[nmonst];
		
		for(int i = 0; i < nmonst; i++) {
			monster_x[i] = in.readDouble();
			monster_y[i] = in.readDouble();
		}
		
		boolean[] monster_dir_x = new boolean[nmonst];
		boolean[] monster_dir_y = new boolean[nmonst];
		
		for(int i = 0; i < nmonst; i++) {
			monster_dir_x[i] = in.readBoolean();
			monster_dir_y[i] = in.readBoolean();
		}
		
		double player_spawn_x = in.readDouble();
		double player_spawn_y = in.readDouble();
		
		byte threshold = in.readByte();
		
		return new LevelData(width, height, data, nmonst, 
				monsters, monster_x, monster_y, monster_dir_x, monster_dir_y,
				player_spawn_x, player_spawn_y, threshold);
	}
}
