package data;

import data.Vertex;

import java.util.ArrayList;
import java.util.Collections;
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
	private Vertex myAnchor;
	
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
	
	public void setAnchor(Vertex v) {
		myAnchor = v;
	}
	
	public Vertex getAnchor() {
		return myAnchor;
	}
	
	public boolean hasVertex(Vertex v) {
		for (Vertex t : myVertices) {
			if (v.getX() == t.getX() && v.getY() == t.getY() && v.getZ() == t.getZ()) {
				System.out.printf("V:(%f, %f, %f)\n", v.getX(), v.getY(), v.getZ());
				System.out.printf("T:(%f, %f, %f)\n", t.getX(), t.getY(), t.getZ());
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
	
	public void buildVertexAdjacencies() {
			myAnchor.addSharedFace(this);
			for (Face f : myAdjacentFaces) {
				myAnchor.addSharedFace(f);
			}
			/*
			// Poll the queue for the next face to examine.
			Face adj = q.poll();
			// If we got a face...
			if(adj != null) { 
				// If it has the vertex we're looking for...
				if (adj.hasVertex(v)) {
					// ...and if we haven't already added this to our list of faces...
					if(!v.getAdjacentFaces().contains(adj)) {
						// ...add the shared face.
						v.addSharedFace(adj);
						// Then, for all of the faces touching that face...
						for (Face f : adj.myAdjacentFaces) {
							// ...if we haven't already added it to our queue...
							if (!q.contains(f)) {
								// add the 
								q.add(f);
							}
						}
					}
				}
				buildVertexAdjacencies(q, v);
			}
			*/

	}
	
	public void drawFace(GL2 gl, GLU glu, GLUT glut) {
		
			Collections.sort(myVertices);
			float[] normal = myAnchor.getVertexNormal(gl, glu, glut);
			gl.glNormal3f(normal[0], normal[1], normal[2]);
			gl.glVertex3f(myAnchor.getX(), myAnchor.getY(), myAnchor.getZ());
		
			/*for (Vertex v : myVertices) {
			
			v.addSharedFace(this);
			
			Queue<Face> seed = new PriorityQueue<Face>();
			for (Face f : myAdjacentFaces) {
				seed.add(f);
			}
			
			buildVertexAdjacencies(seed, myAnchor);
			
			float[] normal = new float[3];
			for (Face f : v.getAdjacentFaces()) {
				float[] fNormal = f.calculateFaceNormal(gl, glu, glut);
				normal[0] += fNormal[0];
				normal[1] += fNormal[1];
				normal[2] += fNormal[2];
			}
			
			gl.glNormal3f(normal[0]/4, normal[1]/4, normal[2]/4);
			//System.out.printf("Drawing vertex (%f, %f, %f)\n", v.getX(), v.getY(), v.getZ());
			gl.glVertex3f(v.getX(), v.getY(), v.getZ());
		}*/
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
		Face f = (Face) o;
		if (this.getAnchor().getY() > f.getAnchor().getY()) {
			return -1;
		}
		else if (this.getAnchor().getY() < f.getAnchor().getY()){
			return 1;
		}
		else {
			if (this.getAnchor().getX() > f.getAnchor().getX()) {
				return -1;
			}
			else if (this.getAnchor().getX() < f.getAnchor().getX()) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}

}