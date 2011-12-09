package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import util.Geometry;
import util.Utils;


public class ImageWarping extends JPanel {
	private static final long serialVersionUID = 1L;

	private int gridBlockSize;
	private int pointSize;
	private Point[][] controlMatrix;
	private int evaluations;

	private Point currentPoint;

	private BufferedImage image;
	private WarpingResult squarePanel;

	public ImageWarping(final int gridSize, final int evaluations, final File imageFile)
	throws IOException {
		image = ImageIO.read(imageFile);
		final int height = image.getHeight();
		final int width = image.getWidth();
		if (height < width) {			
			gridBlockSize = height / gridSize;
		} else {
			gridBlockSize = width / gridSize;
		}
		pointSize = 5;
		controlMatrix = new Point[gridSize][gridSize];
		this.evaluations = evaluations;

		currentPoint = null;

		squarePanel = new WarpingResult(evaluations);

		//initializing square panel frame
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame resultFrame = new JFrame("Result");
				resultFrame.setContentPane(squarePanel);
				resultFrame.setResizable(false);
				resultFrame.pack();
				resultFrame.setVisible(true);
				resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);				
			}
		});

		this.fillControlMatrix(); //don`t forget to fill the matrix

		this.setSize(width, height);
		this.setPreferredSize(new Dimension(width, height));

		//adding events
		this.addMouseListener(new MouseAdapter() {
			//when pressing a point, keep it in currentPoint
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getX() < width && e.getY() < height) {
					currentPoint = findPoint(e.getX(), e.getY());
				}
			}

			//when releasing a point, guarantee it is inside the frame 
			@Override
			public void mouseReleased(MouseEvent e) {
				if (currentPoint != null) {						
					if (e.getX() >= width - pointSize) {						
						currentPoint.x = width  - pointSize;
					} else if (e.getX() < 0) {
						currentPoint.x = 0;
					}
					if (e.getY() >= height - pointSize) {
						currentPoint.y = height  - pointSize;						
					} else if (e.getY() < 0) {
						currentPoint.y = 0;
					}
					repaint();
				}
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {	
			//when dragging a point, don`t let it be dragged out of the frame
			@Override
			public void mouseDragged(MouseEvent e) {
				if (currentPoint != null) {						
					if (e.getX() < width - pointSize) {
						if (e.getX() >= 0) {
							currentPoint.x = e.getX();
						} else {						
							currentPoint.x = 0;
						}
					} else {						
						currentPoint.x = width  - pointSize;
					}
					if (e.getY() < height - pointSize) {
						if (e.getY() >= 0) {
							currentPoint.y = e.getY();
						} else {
							currentPoint.y = 0;						
						}
					} else {
						currentPoint.y = height  - pointSize;						
					}

					repaint();
				}
			}
		});
	}

	private void fillControlMatrix() {
		for (int i = 0; i < controlMatrix.length; ++i) {
			Point[] line = controlMatrix[i];

			for (int j = 0; j < line.length; ++j) {
				line[j] = new Point(i * gridBlockSize + gridBlockSize / 2,
						j * gridBlockSize + gridBlockSize / 2);
			}
		}
	}

	private Point findPoint(int x, int y) {
		for (Point[] line : controlMatrix) {
			for (Point p : line) {
				if (p != null && x >= p.x && x <= p.x + pointSize &&
						y >= p.y && y <= p.y + pointSize) {
					return p;
				}
			}
		}
		return null;
	}

	@Override
	protected void paintComponent(Graphics g) {	
		g.drawImage(image, 0, 0, null);

		g.setColor(Color.GREEN);

		/* Draw the grid using the Tensorial de Casteljau Algorithm for Surfaces
		 * Pseudocode: http://www.cs.mtu.edu/~shene/COURSES/cs3621/NOTES/surface/bezier-de-casteljau.html
		 */
		for (int j = 0; j < evaluations; ++j) {
			double u = j / ((double) (evaluations - 1));

			for (int i = 0; i < evaluations; ++i) {
				double v = i / ((double) (evaluations - 1));

				Point p = Geometry.deCasteljauTensorial(controlMatrix, evaluations, u, v);

				squarePanel.colors[i][j] = Utils.pixelColor(image, p);
				squarePanel.repaint();

				g.drawLine(p.x, p.y, p.x, p.y);
			}
		}

		//draw control points
		g.setColor(Color.RED);
		for (Point[] line : controlMatrix) {			
			for (Point p : line) {
				if (p != null) {
					g.fillRect(p.x, p.y, pointSize, pointSize);
				}
			}
		}
	}

	private static void createAndShowWarpingFrame(final JPanel mainPanel) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame warpingFrame = new JFrame("Warping");
				warpingFrame.setContentPane(mainPanel);
				warpingFrame.setResizable(false);
				warpingFrame.pack();
				warpingFrame.setLocationRelativeTo(null);
				warpingFrame.setVisible(true);
				warpingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		});
	}

	private static class ChooseImageButton extends JButton {
		private static final long serialVersionUID = 1L;
		
		public ChooseImageButton(final JTextField gridTextField, final JTextField evaluationsTextField) {
			this.addActionListener(new AbstractAction() {
				private static final long serialVersionUID = 1L;
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
					
					if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						try {
							ImageWarping mainPanel =
								new ImageWarping(Integer.parseInt(gridTextField.getText()),
										Integer.parseInt(evaluationsTextField.getText()),
										fileChooser.getSelectedFile());
							createAndShowWarpingFrame(mainPanel);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(ChooseImageButton.this, "Couldn`t open image file",
									"Image error", JOptionPane.ERROR_MESSAGE);
						} catch (Exception e2) {
							JOptionPane.showMessageDialog(ChooseImageButton.this, "Incorrect option parameters or invalid image format",
									"Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JPanel warpingOptions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
				warpingOptions.add(new JLabel("Number of grid points (per line):"));
				JTextField gridTextField = new JTextField(2);
				gridTextField.setText("3");
				warpingOptions.add(gridTextField);
				warpingOptions.add(new JLabel("Number of evaluations:"));
				JTextField evaluationsTextField = new JTextField(3);
				evaluationsTextField.setText("150");
				warpingOptions.add(evaluationsTextField);
				JButton chooseImageButton = new ChooseImageButton(gridTextField, evaluationsTextField);
				chooseImageButton.setText("Choose image and start warping");
				chooseImageButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
				warpingOptions.add(chooseImageButton);
				
				JFrame warpingOptionsFrame = new JFrame("Image Warping - Options");
				warpingOptionsFrame.setContentPane(warpingOptions);
				warpingOptionsFrame.setResizable(false);
				warpingOptionsFrame.pack();
				warpingOptionsFrame.setLocationRelativeTo(null);
				warpingOptionsFrame.setVisible(true);
				warpingOptionsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}
}
