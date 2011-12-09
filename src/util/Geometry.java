package util;

import java.awt.Point;


public class Geometry {

	public static Point interpolate(Point a, Point b, double t) {
		Point dest = new Point();
		dest.x = a.x + (int) ((b.x - a.x) * t);
		dest.y = a.y + (int) ((b.y - a.y) * t);
		return dest;
	}
	
	public static ColorRGB interpolate3D(ColorRGB a, ColorRGB b, double t) {
		int red = a.red + (int) ((b.red - a.red) * t);
		int green = a.green + (int) ((b.green - a.green) * t);
		int blue = a.blue + (int) ((b.blue - a.blue) * t);
				
		return new ColorRGB(red, green, blue);
	}

	public static Point deCasteljau(Point[] pointsToInterpolate, double t) {
		int length = pointsToInterpolate.length;
		
		for (int i = 0; length > 1;) {
			pointsToInterpolate[i] = interpolate(pointsToInterpolate[i],
					pointsToInterpolate[i + 1], t);
			i++;
			if (i == length - 1) {
				pointsToInterpolate[i] = null;
				length = length - 1;
				i = 0;
			}
		}
		return pointsToInterpolate[0];
	}
	
	public static ColorRGB deCasteljau(ColorRGB[] pointsToInterpolate, double t) {
		int length = pointsToInterpolate.length;
		
		for (int i = 0; length > 1;) {
			pointsToInterpolate[i] = interpolate3D(pointsToInterpolate[i],
					pointsToInterpolate[i + 1], t);
			++i;
			if (i == length - 1) {
				pointsToInterpolate[i] = null;
				length = length - 1;
				i = 0;
			}
		}
		return pointsToInterpolate[0];
	}

	public static Point deCasteljauTensorial(Point[][] controlMatrix, int evaluations, double u, double v) {
		Point[] column = new Point[controlMatrix[0].length];
		
		for (int k = 0, m = 0; k < controlMatrix.length; ++k) {
			Point[] currentLine = controlMatrix[k];
			
			Point newPoint = deCasteljau(currentLine.clone(), u);
			column[m] = newPoint;
			++m;
		}

		return deCasteljau(column, v);
	}

}
