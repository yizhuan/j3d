package j3d;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Draws 3D object on screen.
 *
 * @author Yizhuan Yu
 *
 */
public class Paint3D {

	private static Color gr[];

	/**
	 * Constructs a new object.
	 */
	public Paint3D(){

	}

	/**
	 * Draws a grey scale image of the model.
	 *
	 * @param g the graphics context
	 * @param model the model to draw
	 */
	public void paint(Graphics g, ObjModel3D model) {

		if (gr == null) {
			gr = new Color[16];
			for (int i = 0; i < 16; i++) {
				int grey = (int) (192 * (1 - Math.pow(i / 15.0, 2.3)));
				gr[i] = new Color(grey, grey, grey);
			}
		}
		int lg = 0;
		int lim = model.getNumberOfConnections();
		int c[] = model.getConnections();

		Point3D vertices[] = model.getVertices();
		if (lim <= 0 || model.getNumberOfVertices() <= 0)
			return;

		for (int i = 0; i < lim; i++) {
			int T = c[i];
			int p1 = ((T >> 16) & 0xFFFF);
			int p2 = (T & 0xFFFF);

			int grey = (int) vertices[p1].z + (int) vertices[p2].z;
			if (grey < 0) {
				grey = 0;
			}
			if (grey > 15)
				grey = 15;
			if (grey != lg) {
				lg = grey;
				g.setColor(gr[grey]);
			}

			g.drawLine((int) vertices[p1].x, (int) vertices[p1].y,
					(int) vertices[p2].x, (int) vertices[p2].y);

		}

	}

}
