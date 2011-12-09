package util;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;


public class Utils {
	
	public static Color pixelColor(BufferedImage image, int x, int y) {
		int corDoPixel = image.getRGB(x, y);
		return new Color(corDoPixel);
	}
	
	public static Color pixelColor(BufferedImage image, Point p) {
		return pixelColor(image, p.x, p.y);
	}
	
}
