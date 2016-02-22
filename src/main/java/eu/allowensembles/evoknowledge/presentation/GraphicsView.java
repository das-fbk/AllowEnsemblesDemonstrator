package eu.allowensembles.evoknowledge.presentation;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GraphicsView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4792895199668616125L;
	private BufferedImage img;

	public GraphicsView(BufferedImage img) {
		this.img = img;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (img != null) {
			g.drawImage(img, 0, 0, this);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		if (img != null) {
			return new Dimension(img.getWidth(), img.getHeight());
		}
		return super.getPreferredSize();
	}
}
