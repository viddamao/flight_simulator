package data;

import data.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;


public class Face implements Comparable {
	
	private ArrayList<Face> myAdjacentFaces;
	private ArrayList<Vertex> myVertices;
	private float[] myFaceNormal;
	
	public Face() {
		myAdjacentFaces = new ArrayList<Face>();
		myVertices = new ArrayList<Vertex>();
		myFaceNormal = new float[3];
	}

	public void addAdjacentFace(Face f) {
		if (f != null) {
			myAdjacentFaces.add(f);
		}
	}
	
	public void addVertex(Vertex v) {
		myVertices.add(v);
	}
	
	public boolean hasVertex(Vertex v) {
		for (Vertex t : myVertices) {
			System.out.printf("V:(%f, %f, %f)\n", v.getX(), v.getY(), v.getZ());
			System.out.printf("T:(%f, %f, %f)\n", t.getX(), t.getY(), t.getZ());
			if (v.getX() == t.getX() && v.getY() == t.getY()) {
				return true;
			}
		}
		return false;
	}
	
	public float[] calculateFaceNormal(GL2 gl, GLU glu, GLUT glut) {
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
        
        myFaceNormal[0] = nx;
        myFaceNormal[1] = ny;
        myFaceNormal[2] = nz;
       
        return myFaceNormal;
        
	}
	
	public void buildVertexAdjacencies(Queue<Face> q, Vertex v) {
			Face adj = q.poll();
			if(adj != null) { 
				if (adj.hasVertex(v)) {
					v.addSharedFace(adj);
					for (Face f : adj.myAdjacentFaces) {
						q.add(f);
					}
				}
				buildVertexAdjacencies(q, v);
			}
	}
	
	public void drawFace(GL2 gl, GLU glu, GLUT glut) {
		for (Vertex v : myVertices) {
			float[] normal = new float[3];
			
			Queue<Face> seed = new PriorityQueue<Face>();
			for (Face f : myAdjacentFaces) {
				seed.add(f);
			}
			
			buildVertexAdjacencies(seed, v);
			
			for (Face f : v.getAdjacentFaces()) {
				float[] fNormal = f.calculateFaceNormal(gl, glu, glut);
				normal[0] += fNormal[0];
				normal[1] += fNormal[1];
				normal[2] += fNormal[2];
			}
			
			float[] n = normalize(normal);
			gl.glNormal3f(n[0], n[1], n[2]);
			
			//System.out.printf("Drawing vertex (%f, %f, %f)\n", v.getX(), v.getY(), v.getZ());
			gl.glVertex3f(v.getX(), v.getY(), v.getZ());
		}
	}
	
	public float[] normalize(float[] normal) {
		float[] result = new float[3];
		float length = (float) Math.sqrt(Math.pow(normal[0], 2) + Math.pow(normal[1], 2) + Math.pow(normal[2], 2));
		result[0] = normal[0]/length;
		result[1] = normal[1]/length;
		result[2] = normal[2]/length;
		return result;
	}
	
	public void printDiagnosticInfo() {
		System.out.print("---------------------------------\n");
		System.out.print("Adjacent with vertices:\n");
		for (Vertex v : myVertices) {
			System.out.printf("(%f, %f, %f)\n", v.getX(), v.getY(), v.getZ());
		}
	}

	public int compareTo(Object o) {
		return 0;
	}

}