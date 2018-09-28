package com.lkn.ai.neural_network;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * @author likangning
 * @since 2018/9/11 下午6:00
 */
public class DrawSmoothCurve extends JPanel {
	private Point[] points = {
			new Point(0, 0),
			new Point(100, 100),
			new Point(200, -100),
			new Point(300, 100),
			new Point(330, -80),
			new Point(350, -70)
	};

	private GeneralPath path = new GeneralPath();

	public DrawSmoothCurve() {
		path.moveTo(points[0].x, points[0].y);

		for (int i = 0; i < points.length - 1; ++i) {
			Point sp = points[i];
			Point ep = points[i + 1];
			Point c1 = new Point((sp.x + ep.x) / 2, sp.y);
			Point c2 = new Point((sp.x + ep.x) / 2, ep.y);

			path.curveTo(c1.x, c1.y, c2.x, c2.y, ep.x, ep.y);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setStroke(new BasicStroke(2));
		g2d.translate(40, 130);
		g2d.draw(path);

		for (int i = 0; i < points.length; ++i) {
			g2d.setColor(Color.GRAY);
			g2d.fillOval(points[i].x - 4, points[i].y - 4, 8, 8);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(points[i].x - 4, points[i].y - 4, 8, 8);
		}
	}

	private static void createAndShowGui() {
		JFrame frame = new JFrame("Smooth Curve");
		frame.setContentPane(new DrawSmoothCurve());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(420, 280);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(DrawSmoothCurve::createAndShowGui);
	}
}
