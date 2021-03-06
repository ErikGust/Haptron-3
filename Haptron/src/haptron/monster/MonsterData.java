package haptron.monster;

import haptron.engine.Console;
import haptron.utils.ImageUtil;
import haptron.utils.Resources;

import java.awt.Color;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class MonsterData {
	//MONSTER1,MONSTER2,MONSTER3;
	
	public static final ArrayList<MonsterData> values;
	static {
		values = new ArrayList<MonsterData>();
		values.add(new MonsterData("monster1"));
		values.add(new MonsterData("monster2"));
		values.add(new MonsterData("monster3"));
		values.add(new MonsterData("trump"));
	}
	
	public final String name;
	public final int damage;
	public final double size;
	public final double speed;
	public final Image image;
	
	private MonsterData(String name) {
		this.name = name;
		Image image = null;
		int damage = 0;
		double speed = 0;
		double size = 0;
		try {
			InputStream _in = Resources.getResource("haptron." + name);
			DataInputStream in = new DataInputStream(_in);
			final int img_buffer_size = in.readInt(); 
			final byte[] image_buffer = new byte[img_buffer_size];
			in.read(image_buffer);
			ByteArrayInputStream image_stream = new ByteArrayInputStream(image_buffer);
			image = ImageUtil.removeBackground(ImageIO.read(image_stream), Color.green);
			damage = in.readInt();
			speed = in.readDouble();
			size = in.readDouble();
		} catch (Exception e) {
			Console.error(e);
			System.exit(-1);
		}
		this.damage = damage;
		this.size = size;
		this.speed = speed;
		this.image = image;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public int ordinal() {
		return values.indexOf(this);
	}
	
	public static void generateMonsterData(File file, byte[] image, 
			int damage, double speed, double size) throws IOException {
		if(!file.exists()) 
			file.createNewFile();
		
		OutputStream _out = new FileOutputStream(file);
		DataOutputStream out = new DataOutputStream(_out);
		
		out.writeInt(image.length);
		out.write(image);
		out.writeInt(damage);
		out.writeDouble(speed);
		out.writeDouble(size);
		out.close();
	}
	
	/*
	 * Usage: <program name> <output file> <image file> <damage> <speed> <size>
	 * 
	 * Note: 
	 *  * if <damage> >= 1 then walls are destroyed on collision
	 *  * <speed> is in tiles/tick
	 *  * <size> is in tiles.
	 *
	public static void main(String[] args) throws IOException {
		File img_file = new File(args[1]);
		InputStream img_stream = new FileInputStream(img_file);
		byte[] image = Resources.read(img_stream);
		img_stream.close();
		
		generateMonsterData(
				new File(args[0]),
				image,
				new Integer(args[2]),
				new Double(args[3]),
				new Double(args[4]));
	}
	*/
}
