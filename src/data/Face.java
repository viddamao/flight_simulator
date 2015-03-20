package data;

import data.Vertex;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;


public class Face {
	
	private ArrayList<Face> myAdjacentFaces;
	private ArrayList<Vertex> myVertices;
	private float[] myNormalVector;
	
	public Face() {
		myAdjacentFaces = new ArrayList<Face>();
		myVertices = new ArrayList<Vertex>();
		myNormalVector = new float[3];
	}

	public void addAjacentFace(Face f) {
		myAdjacentFaces.add(f);
	}
	
	public void addVertex(Vertex v) {
		myVertices.add(v);
	}
	
	public boolean hasVertex(Vertex v) {
		for (Vertex t : myVertices) {
			if (v.getX() == t.getX() && v.getY() == t.getY() && v.getZ() == t.getZ()) {
				return true;
			}
		}
		return false;
	}
	
	public void calculateFaceNormal(GL2 gl, GLU glu, GLUT glut) {
		Vertex v0 = myVertices.get(0);
		Vertex v1 = myVertices.get(1);
		Vertex v2 = myVertices.get(2);
		Vertex v3 = myVertices.get(3);
		
		float nx = (v0.getY() - v1.getY()) * (v0.getZ() + v1.getZ()) + (v1.getY() - v2.getY()) * (v1.getZ() + v2.getZ())
                 + (v2.getY() - v3.getY()) * (v2.getZ() + v3.getZ()) + (v3.getY() - v0.getY()) * (v3.getZ() + v0.getZ());
        float ny = (v0.getZ() - v1.getZ()) * (v0.getX() + v1.getX()) + (v1.getZ() - v2.getZ()) * (v1.getX() + v2.getX())
                 + (v2.getZ() - v3.getZ()) * (v2.getX() + v3.getX()) + (v3.getZ() - v0.getZ()) * (v3.getX() + v0.getX());
        float nz = (v0.getX() - v1.getX()) * (v0.getY() + v1.getY()) + (v1.getX() - v2.getX()) * (v1.getY() + v2.getY())
                 + (v2.getX() - v3.getX()) * (v2.getY() + v3.getY()) + (v3.getX() - v0.getX()) * (v3.getY() + v0.getY());
        
        myNormalVector[0] = nx;
        myNormalVector[1] = ny;
        myNormalVector[2] = nz;
        
        gl.glNormal3f(nx, ny, nz);
	}
	
	public void printDiagnosticInfo() {
		System.out.print("---------------------------------\n");
		System.out.print("Added a new face with vertices:\n");
		for (Vertex v : myVertices) {
			System.out.printf("(%f, %f, %f)\n", v.getX(), v.getY(), v.getZ());
		}
	}

}