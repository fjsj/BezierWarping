package util;

import java.awt.Color;

public class ColorRGB {
	public int red, green, blue;

	public ColorRGB(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public Color toColor() {
		return new Color(red, green, blue);
	}
}
