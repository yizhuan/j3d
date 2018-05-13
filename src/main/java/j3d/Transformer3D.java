package j3d;

/**
 * 3D transformer. It can be used to move, scale and rotate a 3D object. It can rotate an object around
 * X axis, Y axis, Z axis and around a vector p1 -> p2.
 *
 * @author Yizhuan Yu
 *
 */
public class Transformer3D {

	private static final double pi = 3.14159265f;
	private double[][] m4x4 = new double[4][4];

	/**
	 * Constructs a new transformer.
	 */
	public Transformer3D() {
		int r = 0, c = 0;
		for (r = 0; r < 4; r++)
			for (c = 0; c < 4; c++)
				m4x4[r][c] = (r == c ? 1 : 0);
	}

	/**
	 * Resets the transformation matrix.
	 *
	 */
	public void reset() {
		reset(m4x4);
	}

	/**
	 * Resets the transformer matrix with the matrix provided.
	 *
	 * @param m
	 *            the transformer matrix
	 */
	private void reset(double[][] m) {
		int r = 0, c = 0;
		for (r = 0; r < 4; r++)
			for (c = 0; c < 4; c++)
				m[r][c] = (r == c ? 1 : 0);
	}

	/**
	 * Transforms by multiplying the provided transformer.
	 *
	 * @param t
	 *            the transformer
	 */
	public void mult(Transformer3D t) {
		double[][] tmp = new double[4][4];
		int r = 0, c = 0;

		for (r = 0; r < 4; r++)
			for (c = 0; c < 4; c++)
				tmp[r][c] = t.m4x4[r][0] * m4x4[0][c] + t.m4x4[r][1]
						* m4x4[1][c] + t.m4x4[r][2] * m4x4[2][c] + t.m4x4[r][3]
						* m4x4[3][c];
		for (r = 0; r < 4; r++)
			for (c = 0; c < 4; c++)
				m4x4[r][c] = tmp[r][c];
	}

	/**
	 * Transforms with the provided transformer.
	 *
	 * @param t
	 *            the transformer
	 */
	private void mult(double[][] t) {
		double[][] tmp = new double[4][4];
		int r = 0, c = 0;

		for (r = 0; r < 4; r++) {
			for (c = 0; c < 4; c++) {
				tmp[r][c] = t[r][0] * m4x4[0][c] + t[r][1] * m4x4[1][c]
						+ t[r][2] * m4x4[2][c] + t[r][3] * m4x4[3][c];
			}
		}
		for (r = 0; r < 4; r++)
			for (c = 0; c < 4; c++)
				m4x4[r][c] = tmp[r][c];
	}

	/**
	 * Translates the origin.
	 *
	 * @param tx x distance
	 * @param ty y distance
	 * @param tz z distance
	 */
	public void translate(double tx, double ty, double tz) {
		double[][] m = new double[4][4];
		reset(m);
		m[0][3] = tx;
		m[1][3] = ty;
		m[2][3] = tz;
		mult(m);
	}

	/**
	 * Scales based on the specified center.
	 *
	 * @param sx
	 *            x scale
	 * @param sy
	 *            y scale
	 * @param sz
	 *            z scale
	 * @param center
	 *            the base center
	 */
	public void scale(double sx, double sy, double sz, Point3D center) {
		double[][] m = new double[4][4];
		reset(m);
		m[0][0] = sx;
		m[0][3] = (1 - sx) * center.x;
		m[1][1] = sy;
		m[1][3] = (1 - sy) * center.y;
		m[2][2] = sz;
		m[2][3] = (1 - sz) * center.z;
		mult(m);
	}

	/**
	 * Scales based on the origin.
	 *
	 * @param sx
	 *            x scale
	 * @param sy
	 *            y scale
	 * @param sz
	 *            z scale
	 */
	public void scale(double sx, double sy, double sz) {
		double[][] m = new double[4][4];
		reset(m);
		m[0][0] = sx;
		m[1][1] = sy;
		m[2][2] = sz;
		mult(m);
	}

	/**
	 * Rotate around the X axis.
	 *
	 * @param angle
	 *            the angle to rotate.
	 */
	public void rotX(double angle) {
		double radianAngle = angle * (pi / 180);
		double sinA = (double) Math.sin(radianAngle);
		double cosA = (double) Math.cos(radianAngle);
		double[][] m = new double[4][4];
		reset(m);
		m[1][1] = cosA;
		m[1][2] = -sinA;
		m[2][2] = cosA;
		m[2][1] = sinA;
		mult(m);
	}

	/**
	 * Rotate around the Y axis.
	 *
	 * @param angle
	 *            the angle to rotate.
	 */
	public void rotY(double angle) {
		double radianAngle = angle * (pi / 180);
		double sinA = (double) Math.sin(radianAngle);
		double cosA = (double) Math.cos(radianAngle);
		double[][] m = new double[4][4];
		reset(m);
		m[0][0] = cosA;
		m[2][0] = -sinA;
		m[2][2] = cosA;
		m[0][2] = sinA;
		mult(m);
	}

	/**
	 * Rotate around the Z axis.
	 *
	 * @param angle
	 *            the angle to rotate.
	 */
	public void rotZ(double angle) {
		double radianAngle = angle * (pi / 180);
		double sinA = (double) Math.sin(radianAngle);
		double cosA = (double) Math.cos(radianAngle);
		double[][] m = new double[4][4];
		reset(m);
		m[0][0] = cosA;
		m[0][1] = -sinA;
		m[1][1] = cosA;
		m[1][0] = sinA;
		mult(m);
	}

	/**
	 * Rotate around the vector p1->p2.
	 *
	 * @param p1
	 *            the starting point
	 * @param p2
	 *            the end point
	 * @param angle
	 *            the angle to rotate.
	 */
	public void rotate(Point3D p1, Point3D p2, double angle) {
		double radianAngle = angle * (pi / 180.0f);
		double length = (double) Math
				.sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y)
						* (p2.y - p1.y) + (p2.z - p1.z) * (p2.z - p1.z));
		double cosA2 = (double) Math.cos(radianAngle / 2.0f);
		double sinA2 = (double) Math.sin(radianAngle / 2.0f);
		double a = sinA2 * (p2.x - p1.x) / length;
		double b = sinA2 * (p2.y - p1.y) / length;
		double c = sinA2 * (p2.z - p1.z) / length;
		double[][] m = new double[4][4];

		translate(-p1.x, -p1.y, -p1.z);
		reset(m);
		m[0][0] = 1.0f - 2 * b * b - 2 * c * c;
		m[0][1] = 2 * a * b - 2 * cosA2 * c;
		m[0][2] = 2 * a * c + 2 * cosA2 * b;
		m[1][0] = 2 * a * b + 2 * cosA2 * c;
		m[1][1] = 1.0f - 2 * a * a - 2 * c * c;
		m[1][2] = 2 * b * c - 2 * cosA2 * a;
		m[2][0] = 2 * a * c - 2 * cosA2 * b;
		m[2][1] = 2 * b * c + 2 * cosA2 * a;
		m[2][2] = 1.0f - 2 * a * a - 2 * b * b;
		mult(m);
		translate(p1.x, p1.y, p1.z);
	}

	/**
	 * Transforms vertices.
	 *
	 * @param v
	 *            the vertices to be transformed
	 * @param tv
	 *            the result - the transformed vertices
	 * @param n
	 *            number of vertices to be transformed
	 */
	public void transform(Point3D v[], Point3D tv[], int n) {
		int k, j;
		double[] tmp = new double[3];
		for (k = 0; k < n; k++) {
			for (j = 0; j < 3; j++) {
				tmp[j] = m4x4[j][0] * v[k].x + m4x4[j][1] * v[k].y + m4x4[j][2]
						* v[k].z + m4x4[j][3];
			}
			Point3D pt = new Point3D(tmp[0], tmp[1], tmp[2]);
			tv[k] = pt;
		}
	}

}
