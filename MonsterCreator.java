

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MonsterCreator {
	
	JTextField imgFileTxt, saveFileTxt, dmgTxt, speedTxt, sizeTxt;
	
	JFrame frame;
	
	public MonsterCreator() {
		/*
		File img_file = new File(args[1]);
		InputStream img_stream = new FileInputStream(img_file);
		byte[] image = read(img_stream);
		img_stream.close();
		
		generateMonsterData(
				new File(args[0]),
				image,
				new Integer(args[2]),
				new Double(args[3]),
				new Double(args[4]));
		*/
		frame = new JFrame();
		GridBagLayout layout = new GridBagLayout();
		frame.getContentPane().setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridy = 0;
		c.gridx = 0;
		frame.add(new JLabel("Image file:"), c);
		imgFileTxt = new JTextField(30);
		c.gridx = 1;
		frame.add(imgFileTxt, c);
		JButton imgFileBrowse = new JButton("Browse...");
		imgFileBrowse.addActionListener( e -> {
			imgFileTxt.setText(browseImageFile());
		});
		c.gridx = 2;
		frame.add(imgFileBrowse, c);

		c.gridy = 1;
		c.gridx = 0;
		frame.add(new JLabel("Save file:"), c);
		saveFileTxt = new JTextField(30);
		c.gridx = 1;
		frame.add(saveFileTxt, c);
		JButton saveFileBrowse = new JButton("Browse...");
		saveFileBrowse.addActionListener( e -> {
			saveFileTxt.setText(browseSaveFile());
		});
		c.gridx = 2;
		frame.add(saveFileBrowse, c);

		c.gridy = 2;
		c.gridx = 0;
		frame.add(new JLabel("Speed:"), c);
		speedTxt = new JTextField(5);
		speedTxt.addActionListener( e -> {
			try {
				Double.valueOf(speedTxt.getText());
			} catch(Exception ex) {
				speedTxt.setText("");
			}
		});
		speedTxt.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {}
			@Override
			public void focusLost(FocusEvent arg0) {
				try {
					Double.valueOf(speedTxt.getText());
				} catch(Exception ex) {
					speedTxt.setText("");
				}
			}
		});
		c.gridx = 1;
		frame.add(speedTxt, c);

		c.gridy = 3;
		c.gridx = 0;
		frame.add(new JLabel("Size:"), c);

		c.gridx = 1;
		sizeTxt = new JTextField(5);
		sizeTxt.addActionListener( e -> {
			try {
				Double.valueOf(sizeTxt.getText());
			} catch(Exception ex) {
				sizeTxt.setText("");
			}
		});
		sizeTxt.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {}
			@Override
			public void focusLost(FocusEvent arg0) {
				try {
					Double.valueOf(sizeTxt.getText());
				} catch(Exception ex) {
					sizeTxt.setText("");
				}
			}
		});
		frame.add(sizeTxt, c);

		c.gridy = 4;
		c.gridx = 0;
		frame.add(new JLabel("Damage:"), c);
		dmgTxt = new JTextField(5);
		dmgTxt.addActionListener( e -> {
			try {
				Integer.valueOf(dmgTxt.getText());
			} catch(Exception ex) {
				dmgTxt.setText("");
			}
		});
		dmgTxt.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {}
			@Override
			public void focusLost(FocusEvent arg0) {
				try {
					Integer.valueOf(dmgTxt.getText());
				} catch(Exception ex) {
					dmgTxt.setText("");
				}
			}
		});
		c.gridx = 1;
		frame.add(dmgTxt, c);

		c.gridy = 5;
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(e -> {
			if(checkMonster()) {
				try {
					saveMonster();
				} catch (IOException e1) {
					e1.printStackTrace();
					alertError(e1.toString());
				}
				System.exit(0);
			}
		});
		c.gridx = 0;
		frame.add(saveButton, c);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> {
			System.exit(0);
		});
		c.gridx = 1;
		frame.add(cancelButton, c);
		
		frame.pack();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private void saveMonster() throws IOException {
		File imageFile = new File(imgFileTxt.getText());
		File saveFile = new File(saveFileTxt.getText());
		double speed = Double.valueOf(speedTxt.getText());
		double size = Double.valueOf(sizeTxt.getText());
		int damage = Integer.valueOf(dmgTxt.getText());
		
		InputStream img_stream = new FileInputStream(imageFile);
		byte[] image = read(img_stream);
		img_stream.close();
		
		generateMonsterData(
				saveFile,
				image,
				damage,
				speed,
				size);
	}

	private boolean checkMonster() {
		try {
			File imageFile = new File(imgFileTxt.getText());
			@SuppressWarnings("unused")
			File saveFile = new File(imgFileTxt.getText());
			double speed = Double.valueOf(speedTxt.getText());
			double size = Double.valueOf(sizeTxt.getText());
			int damage = Integer.valueOf(dmgTxt.getText());
			if(!imageFile.exists()) {
				alertError("Can't find image file: " + imageFile);
				return false;
			}
			if(!imageFile.canRead()) {
				alertError("Can't read image file: " + imageFile);
				return false;
			}
			if(speed < 0 || size < 0 || damage < 0) {
				alertError("Speed/Size/Damage can not be negative");
				return false;
			}
		} catch(Exception e) {
			alertError(e.toString());
			return false;
		}
		return true;
	}

	private void alertError(String str) {
		JOptionPane.showMessageDialog(frame, str, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private String browseImageFile() {
		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new FileNameExtensionFilter("PNG (.png)", "png"));
		fc.addChoosableFileFilter(new FileNameExtensionFilter("All files (.*)", "*"));
		if(fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile().toString();
		}
		else return "";
	}

	private String browseSaveFile() {
		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new FileNameExtensionFilter("All files (.*)", "*"));
		if(fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile().toString();
		}
		else return "";
	}

	public void generateMonsterData(File file, byte[] image, 
			int damage, double speed, double size) throws IOException {
		if(!file.exists()) 
			file.createNewFile();
		
		
		System.out.println("SAVING FILE: " + file);
		OutputStream _out = new FileOutputStream(file);
		DataOutputStream out = new DataOutputStream(_out);
		
		out.writeInt(image.length);
		out.write(image);
		out.writeInt(damage);
		out.writeDouble(speed);
		out.writeDouble(size);
		out.close();
	}
	
	public byte[] read(InputStream in) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[1024];

		while ((nRead = in.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		
		return buffer.toByteArray();
	}
	
	/*
	 * Usage: <program name> <output file> <image file> <damage> <speed> <size>
	 * 
	 * Note: 
	 *  * if <damage> >= 1 then walls are destroyed on collision
	 *  * <speed> is in tiles/tick
	 *  * <size> is in tiles.
	 */
	public static void main(String[] args) throws IOException {
		
		new MonsterCreator();
	}
}

