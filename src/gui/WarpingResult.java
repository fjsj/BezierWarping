package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;


public class WarpingResult extends JPanel {

	private static final long serialVersionUID = 1L;
	Color[][] colors;
	
	WarpingResult(int evaluations) {
		this.setSize(evaluations, evaluations);
		this.setPreferredSize(new Dimension(evaluations, evaluations));
		colors = new Color[evaluations][evaluations];
		
		for (int i = 0; i < colors.length; ++i) {
			for (int j = 0; j < colors[0].length; ++j) {
				colors[i][j] = Color.BLACK;
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		for (int i = 0; i < colors.length; ++i) {
			for (int j = 0; j < colors[0].length; ++j) {
				g.setColor(colors[i][j]);
				g.drawLine(i, j, i, j);
			}
		}
	}
}
