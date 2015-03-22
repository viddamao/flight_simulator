package data;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

public class Vertex {

	private float X;
	private float Y;
	private float Z;
	private ArrayList<Face> myAdjacentFaces;

	public Vertex(float x, float y, float z) {
		setX(x);
		setY(y);
		setZ(z);
		setAdjacentFaces(new ArrayList<Face>());
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
		return myAdjacentFaces;
	}

	public void setAdjacentFaces(ArrayList<Face> myAdjacentFaces) {
		this.myAdjacentFaces = myAdjacentFaces;
	}
}
