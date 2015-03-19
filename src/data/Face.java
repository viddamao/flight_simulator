package data;
import java.util.ArrayList;


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