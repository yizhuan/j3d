package j3d;


/**
 * A 3D point.
 *
 * @author Yizhuan Yu
 *
 */
public class Point3D {
	public double x = 0;
	public double y = 0;
	public double z = 0;

	/**
	 * Constructs a new object.
	 */
	public Point3D() {
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
	}

	/**
	 * Constructs a new object.
	 *
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
