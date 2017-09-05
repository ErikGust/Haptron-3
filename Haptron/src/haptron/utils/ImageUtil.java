package haptron.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

public class ImageUtil {
	
	public static BufferedImage imageToBufferedImage(Image image) {
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
				image.getHeight(null),BufferedImage.TYPE_INT_ARGB);
		Graphics g = bufferedImage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bufferedImage;
	}
	
	public static Image removeBackgrounnd(Image image, 
			Color background) {
		return removeBackground(imageToBufferedImage(image),background);
	}
	
	public static Image makeShadow(Image image) {
		return makeShadow(imageToBufferedImage(image));
	}
	
	public static Image makeShadow(BufferedImage image) {
		ImageFilter filter = new RGBImageFilter() {
    		public final int filterRGB(int x, int y, int rgb) {
    			if(rgb != 0) return 0xA0000000;
    			else return rgb;
    		}
    	};

    	ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
    	return Toolkit.getDefaultToolkit().createImage(ip);
	}

	public static Image removeBackground(BufferedImage image, 
			final Color background) {
		ImageFilter filter = new RGBImageFilter() {
    		public int markerRGB = background.getRGB() | 0xFF000000;
    		public final int filterRGB(int x, int y, int rgb) {
    			if ((rgb | 0xFF000000) == markerRGB) {
    				return 0x00FFFFFF & rgb;
    			} else {
    				return rgb;
    			}
    		}
    	};

    	ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
    	return Toolkit.getDefaultToolkit().createImage(ip);
	}
}
