package j3d;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;

/**
 * A 3D model in OBJ format.
 *
 * @author Yizhuan Yu
 */
public class ObjModel3D {

	/**
	 * The original vertices.
	 */
	private Point3D orgVert[];

	/**
	 * The transformed vertices.
	 */
	private Point3D transformedVert[];

	/**
	 * Number of vertices.
	 */
	private int nvert;

	/**
	 * Maximum number of vertices.
	 */
	private int maxvert;

	/**
	 * Connections. Each point is held in 16 bits with starting point on left.
	 */
	private int con[];

	/**
	 * Number of connections.
	 */
	private int ncon;

	/**
	 * Maximum number of connections.
	 */
	private int maxcon;

	/**
	 * Is the model transformed?.
	 */
	private boolean transformed;

	/**
	 * Boundaries.
	 */
	public float xmin, xmax, ymin, ymax, zmin, zmax;

	/**
	 * Constructs a new model.
	 */
	public ObjModel3D() {

	}

	/**
	 * Returns number of connections.
	 *
	 * @return number of connections
	 */
	public int getNumberOfConnections() {
		return ncon;
	}

	/**
	 * Returns number of vertices.
	 *
	 * @return number of vertices
	 */
	public int getNumberOfVertices() {
		return nvert;
	}

	/**
	 * Returns connections.
	 *
	 * @return connections
	 */
	public int[] getConnections() {
		return con;
	}

	/**
	 * Returns transformed vertices.
	 *
	 * @return transformed vertices
	 */
	public Point3D[] getVertices() {
		return transformedVert;
	}

	/**
	 * Loads an OBJ model.
	 *
	 * @param is
	 *            the OBJ model
	 * @throws IOException
	 *             thrown when I/O access failed.
	 * @throws ObjFileFormatException
	 *             thrown when the OBJ file format is incorrect.
	 */
	public void load(InputStream is) throws IOException, ObjFileFormatException {

		Reader r = new BufferedReader(new InputStreamReader(is));
		StreamTokenizer st = new StreamTokenizer(r);
		st.eolIsSignificant(true);
		st.commentChar('#');
		scan: while (true) {
			switch (st.nextToken()) {
			default:
				break scan;
			case StreamTokenizer.TT_EOL:
				break;
			case StreamTokenizer.TT_WORD:
				if ("v".equals(st.sval)) {
					double x = 0, y = 0, z = 0;
					if (st.nextToken() == StreamTokenizer.TT_NUMBER) {
						x = st.nval;
						if (st.nextToken() == StreamTokenizer.TT_NUMBER) {
							y = st.nval;
							if (st.nextToken() == StreamTokenizer.TT_NUMBER) {
								z = st.nval;
							}
						}
					}
					addVert(new Point3D((float) x, (float) y, (float) z));
					while (st.ttype != StreamTokenizer.TT_EOL
							&& st.ttype != StreamTokenizer.TT_EOF) {
						st.nextToken();
					}
				} else if ("f".equals(st.sval) || "fo".equals(st.sval)
						|| "l".equals(st.sval)) {
					int start = -1;
					int prev = -1;
					int n = -1;
					while (true) {
						if (st.nextToken() == StreamTokenizer.TT_NUMBER) {
							n = (int) st.nval;
							if (prev >= 0)
								add(prev - 1, n - 1);
							if (start < 0)
								start = n;
							prev = n;
						} else if (st.ttype == '/') {
							st.nextToken();
						} else {
							break;
						}
					}
					if (start >= 0) {
						add(start - 1, prev - 1);
					}
					if (st.ttype != StreamTokenizer.TT_EOL) {
						break scan;
					}
				} else {
					while (st.nextToken() != StreamTokenizer.TT_EOL
							&& st.ttype != StreamTokenizer.TT_EOF)
						;
				}
			}
		}

		if (st.ttype != StreamTokenizer.TT_EOF) {
			throw new ObjFileFormatException(st.toString());
		}
	}

	/**
	 * Adds one vertex to the original model.
	 *
	 * @param p
	 *            the vertex
	 * @return number of vertices
	 */
	private int addVert(Point3D p) {
		int i = nvert;
		if (i >= maxvert) {
			if (orgVert == null) {
				maxvert = 100;
				orgVert = new Point3D[maxvert];
			} else {
				maxvert *= 2;
				Point3D nv[] = new Point3D[maxvert];
				System.arraycopy(orgVert, 0, nv, 0, orgVert.length);
				orgVert = nv;
			}
		}

		orgVert[i] = p;

		return nvert++;
	}

	/**
	 * Adds a connection from p1 to p2 to the model.
	 *
	 * @param p1
	 *            the starting point
	 * @param p2
	 *            the end point
	 */
	private void add(int p1, int p2) {
		int i = ncon;
		if (p1 >= nvert || p2 >= nvert) {
			return;// illegal index
		}
		if (i >= maxcon) {
			if (con == null) {
				maxcon = 100;
				con = new int[maxcon];
			} else {
				maxcon *= 2;
				int nv[] = new int[maxcon];
				System.arraycopy(con, 0, nv, 0, con.length);
				con = nv;
			}
		}
		if (p1 > p2) {
			int t = p1;
			p1 = p2;
			p2 = t;
		}
		con[i] = (p1 << 16) | p2;// 16 bits for one point index, smaller one on
									// left
		ncon = i + 1;
	}

	/**
	 * Transform all vertices of the model.
	 *
	 * @param transformer
	 *            the transformation matrix
	 */
	public void transform(Transformer3D transformer) {
		if (transformed || nvert <= 0)
			return;
		if (transformedVert == null || transformedVert.length < nvert)
			transformedVert = new Point3D[nvert];
		transformer.transform(orgVert, transformedVert, nvert);
		transformed = true;
	}

	/**
	 * Checks whether the model is transformed.
	 *
	 * @return true if the model is transformed, false otherwise.
	 */
	public boolean isTransformed() {
		return this.transformed;
	}

	/**
	 * Sets whether the model is transformed.
	 *
	 * @param transformed
	 *            true if the model is transformed, false otherwise.
	 */
	public void setTransformed(boolean transformed) {
		this.transformed = transformed;
	}

	/**
	 * Sorts the connections.
	 *
	 * @param lo0
	 *            lo
	 * @param hi0
	 *            hi
	 */
	private void sort(int lo0, int hi0) {
		int a[] = con;
		int lo = lo0;
		int hi = hi0;
		if (lo >= hi)
			return;
		int mid = a[(lo + hi) / 2];
		while (lo < hi) {
			while (lo < hi && a[lo] < mid) {
				lo++;
			}
			while (lo < hi && a[hi] >= mid) {
				hi--;
			}
			if (lo < hi) {
				int T = a[lo];
				a[lo] = a[hi];
				a[hi] = T;
			}
		}
		if (hi < lo) {
			int T = hi;
			hi = lo;
			lo = T;
		}
		sort(lo0, lo);
		sort(lo == lo0 ? lo + 1 : lo, hi0);
	}

	/**
	 * Eliminates duplicates connections.
	 */
	public void compress() {
		int limit = ncon;
		int c[] = con;
		sort(0, ncon - 1);
		int d = 0;
		int pp1 = -1;
		for (int i = 0; i < limit; i++) {
			int p1 = c[i];
			if (pp1 != p1) {
				c[d] = p1;
				d++;
			}
			pp1 = p1;
		}
		ncon = d;
	}

	/**
	 * Finds boundaries.
	 */
	public void findBoundary() {
		if (nvert <= 0)
			return;
		Point3D v[] = orgVert;
		float xmin = (float) v[0].x, xmax = xmin;
		float ymin = (float) v[0].y, ymax = ymin;
		float zmin = (float) v[0].z, zmax = zmin;
		for (int i = nvert; (i--) > 0;) {
			float x = (float) v[i].x;
			if (x < xmin)
				xmin = x;
			if (x > xmax)
				xmax = x;
			float y = (float) v[i].y;
			if (y < ymin)
				ymin = y;
			if (y > ymax)
				ymax = y;
			float z = (float) v[i].z;
			if (z < zmin)
				zmin = z;
			if (z > zmax)
				zmax = z;
		}
		this.xmax = xmax;
		this.xmin = xmin;
		this.ymax = ymax;
		this.ymin = ymin;
		this.zmax = zmax;
		this.zmin = zmin;
	}
}
