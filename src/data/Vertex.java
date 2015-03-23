package data;

import java.util.ArrayList;
import java.util.Collections;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

public class Vertex implements Comparable {

	private float X;
	private float Y;
	private float Z;
	private int col;
	private int row;
	private ArrayList<Face> myAdjacentFaces;
	private float[] myVertexNormal;

	public Vertex(float x, float y, float z) {
		setX(x);
		setY(y);
		setZ(z);
		setAdjacentFaces(new ArrayList<Face>());
		myVertexNormal = new float[3];
	}

	public float[] getVertexNormal(GL2 gl, GLU glu, GLUT glut) {
		float[] result = new float[3];
		ArrayList<Face> adjacentFaces = Terrain.getTerrain().vfQuery(this);
		for (Face f : adjacentFaces) {
			float[] normal = f.calculateFaceNormal(gl, glu, glut);
			result[0] += normal[0];
			result[1] += normal[2];
			result[2] += normal[2];
		}
		return result;
	}

	public float getX() {
		return X;
	}

	public void setX(float x) {
		X = x;
	}

	public float getY() {
		return Y;
	}

	public void setY(float y) {
		Y = y;
	}

	public float getZ() {
		return Z;
	}

	public void setZ(float z) {
		Z = z;
	}

	public void addSharedFace(Face f) {
		getAdjacentFaces().add(f);
	}

	public ArrayList<Face> getAdjacentFaces() {
		Collections.sort(myAdjacentFaces);
		return myAdjacentFaces;
	}

	public void setAdjacentFaces(ArrayList<Face> myAdjacentFaces) {
		this.myAdjacentFaces = myAdjacentFaces;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int compareTo(Object o) {
		Vertex v = (Vertex) o;
		if (this.getY() < v.getY()) {
			return -1;
		} else if (this.getY() > v.getY()) {
			return 1;
		} else {
			if (this.getX() < v.getX()) {
				return -1;
			} else if (this.getX() > v.getX()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	public String printCoordinates() {
		return "(" + col + "," + row + ") : {" + X + "," + Y + "," + Z + "}\n";
	}

}