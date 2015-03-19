package data;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;


public class Face {
	
	private ArrayList<Face> myAdjacentFaces;
	private ArrayList<Vertex> myVertices;
	
	public Face() {
		myAdjacentFaces = new ArrayList<Face>();
		myVertices = new ArrayList<Vertex>();
	}

	public void addAjacentFace(Face f) {
		myAdjacentFaces.add(f);
	}
	
	public void addVertex(Vertex v) {
		myVertices.add(v);
	}
	
	public boolean hasVertex(Vertex v) {
		for (Vertex t : myVertices) {
			if (v.X == t.X && v.Y == t.Y && v.Z == t.Z) {
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
		
		float nx = (v0.Y - v1.Y) * (v0.Z + v1.Z) + (v1.Y - v2.Y) * (v1.Z + v2.Z)
                 + (v2.Y - v3.Y) * (v2.Z + v3.Z) + (v3.Y - v0.Y) * (v3.Z + v0.Z);
        float ny = (v0.Z - v1.Z) * (v0.X + v1.X) + (v1.Z - v2.Z) * (v1.X + v2.X)
                 + (v2.Z - v3.Z) * (v2.X + v3.X) + (v3.Z - v0.Z) * (v3.X + v0.X);
        float nz = (v0.X - v1.X) * (v0.Y + v1.Y) + (v1.X - v2.X) * (v1.Y + v2.Y)
                 + (v2.X - v3.X) * (v2.Y + v3.Y) + (v3.X - v0.X) * (v3.Y + v0.Y);
        
        gl.glNormal3f(nx, ny, nz);
	}
	
	public class Vertex {
		
		private float X;
		private float Y;
		private float Z;
		
		public Vertex(float x, float y, float z) {
			X = x;
			Y = y;
			Z = z;
		}
		
		
	}

}