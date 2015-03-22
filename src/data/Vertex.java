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
	private ArrayList<Face> myAdjacentFaces;
	private float[] myVertexNormal;

	public Vertex(float x, float y, float z) {
		setX(x);
		setY(y);
		setZ(z);
		setAdjacentFaces(new ArrayList<Face>());
		myVertexNormal = new float[3];
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

	public int compareTo(Object o) {
		Vertex v = (Vertex) o;
		if (this.getY() < v.getY()) {
			return -1;
		}
		else if (this.getY() > v.getY()) {
			return 1;
		}
		else {
			if (this.getX() < v.getX()) {
				return -1;
			}
			else if (this.getX() > v.getX()) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}
	
	public float[] getVertexNormal(GL2 gl, GLU glu, GLUT glut) {
		float[] normal = new float[3];
		for (Face f : myAdjacentFaces) {
			float[] fNormal = f.calculateFaceNormal(gl, glu, glut);
			normal[0] += fNormal[0];
			normal[1] += fNormal[1];
			normal[2] += fNormal[2];
		}
		myVertexNormal = normal;
		return myVertexNormal;
	}

}
