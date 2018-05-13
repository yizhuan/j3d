package j3d;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Animate a 3D model, which supports drag and rotate.
 *
 * @author Yizhuan Yu
 */
public class Obj3dApplet extends Applet implements Runnable {

	private static final long serialVersionUID = 7210927153921359331L;

	boolean bAnimate = true;
	private Thread j3dThread = null;
	private ObjModel3D model3D;
	private boolean painted = true;
	private float xfac;
	private int prevx, prevy;

	private float scalefudge = 1;
	private long refreshRate = 80L;

	private Transformer3D transformer = new Transformer3D();
	private Transformer3D animator = new Transformer3D();
	private Transformer3D rotator = new Transformer3D();

	private String modelUrl = null;
	private String message = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		System.out.println("at init time, CM = " + getColorModel());

		try {
			modelUrl = getParameter("model");
			scalefudge = Float.valueOf(getParameter("scale")).floatValue();
			bAnimate = new Boolean(getParameter("animate"));

		} catch (Exception e) {
			new IllegalArgumentException("Bad parameters.", e);
		}

		animator.rotY(20);
		animator.rotX(20);

		if (modelUrl == null) {
			modelUrl = "model.obj";
		}

		Dimension size = getSize();

		resize(size.width <= 20 ? 400 : size.width, size.height <= 20 ? 400
				: size.height);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		while (j3dThread != null) {

			try {

				if (bAnimate) {

					repaint();

					Thread.sleep(refreshRate);

					rotator.reset();

					rotator.rotX(5.0f);
					rotator.rotY(5.0f);
					rotator.rotZ(5.0f);

					animator.mult(rotator);
					if (painted) {
						painted = false;
						repaint();
					}

				} else {
					Thread.sleep(refreshRate);
				}

			} catch (InterruptedException e) {
				stop();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		if (model3D == null && message == null) {
			if (j3dThread == null) {
				j3dThread = new Thread(this, "J3D");
				j3dThread.start();
			}
		}

		InputStream is = null;
		try {
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

			is = new URL(modelUrl).openStream();
			ObjModel3D m = new ObjModel3D();
			m.load(is);
			m.findBoundary();// find boundary
			m.compress();// compress model

			model3D = m;

			float xw = m.xmax - m.xmin;
			float yw = m.ymax - m.ymin;
			float zw = m.zmax - m.zmin;
			if (yw > xw)
				xw = yw;
			if (zw > xw)
				xw = zw;

			Dimension size = getSize();
			float f1 = size.width / xw;
			float f2 = size.height / xw;
			xfac = 0.7f * (f1 < f2 ? f1 : f2) * scalefudge;

		} catch (Exception e) {
			model3D = null;
			message = e.toString();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		repaint();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean mouseDown(Event e, int x, int y) {
		prevx = x;
		prevy = y;
		if (x < 10 && y < 10) {
			bAnimate = !bAnimate;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean mouseDrag(Event e, int x, int y) {
		rotator.reset();
		Dimension size = getSize();
		float xtheta = (prevy - y) * 360.0f / size.width;
		float ytheta = (x - prevx) * 360.0f / size.height;
		rotator.rotX(xtheta);
		rotator.rotY(ytheta);
		animator.mult(rotator);
		if (painted) {
			painted = false;
			repaint();
		}
		prevx = x;
		prevy = y;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(Graphics g) {

		Dimension size = getSize();

		g.draw3DRect(0, 0, 10, 10, true);
		g.drawString("Toggle animation", 20, 10);
		g.drawString("Try dragging the object...", size.width - 160, 10);

		if (model3D != null) {

			transformer.reset();
			transformer.translate(-(model3D.xmin + model3D.xmax) / 2,
					-(model3D.ymin + model3D.ymax) / 2,
					-(model3D.zmin + model3D.zmax) / 2);

			transformer.mult(animator);

			transformer.scale(xfac, -xfac, 16 * xfac / size.width);
			transformer.translate(size.width / 2, size.height / 2, 8);

			model3D.setTransformed(false);
			model3D.transform(transformer);

			new Paint3D().paint(g, model3D);

			setPainted();

		} else if (message != null) {
			g.drawString("Error in model:", 3, 20);
			g.drawString(message, 10, 40);
		}

	}

	private synchronized void setPainted() {
		painted = true;
		notifyAll();
	}

}
