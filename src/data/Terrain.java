package data;

import java.util.ArrayList;
import java.util.List;

import framework.Pixmap;

public class Terrain {

	private static Terrain _TERRAIN;
	private Pixmap myHeightMap;
	private List<List<Face>> myFaces;
	private List<List<Vertex>> myVertices;
	private int myStepSize;

	protected Terrain() {}
	
	public static Terrain getTerrain() {
		if (_TERRAIN == null) {
			_TERRAIN = new Terrain();
		}
		return _TERRAIN;
	}
	
	public void init(Pixmap map, int step) {
		myHeightMap = map;
		myStepSize = step;
		myFaces = new ArrayList<List<Face>>();
		myVertices = new ArrayList<List<Vertex>>();
	}

	public void build() {
		int width = myHeightMap.getSize().width;
		int height = myHeightMap.getSize().height;
		buildVertexMap(width, height);
		buildFaceMap(width, height);
	}
	
	private void buildVertexMap(int width, int height) {
		
		int col = -1;
		
		for (int X = 0; X < width; X += myStepSize) {
			
			col++;
			int row = -1;
			
			List<Vertex> vCol = new ArrayList<Vertex>();
			
			for (int Y = 0; Y < height; Y+= myStepSize) {
				
				row++;
				
				float x = X - width / 2.0f;
				float y = Y - height / 2.0f;
				float z = myHeightMap.getColor(X, Y).getRed();
				Vertex v = new Vertex(x, z, y);
				
				v.setCol(col);
				v.setRow(row);
				
				vCol.add(v);
			}
			myVertices.add(vCol);
		}
		for (List<Vertex> vertices : myVertices) {
			System.out.print(vertices.size());
		}
		System.out.print("Vertex map is " + myVertices.size() + " columns by " + myVertices.get(0).size() + " rows.\n");
	}
	
	private void buildFaceMap(int width, int height) {
		for (int X = 0; X < myVertices.size() -  1; X++) {
			List<Face> fCol = new ArrayList<Face>();
			for (int Y = 0; Y < myVertices.get(0).size() - 1; Y++) {
				Face face = new Face();
				
				boolean inBoundsX = (X+1 < myVertices.size());
				boolean inBoundsY = (Y+1 < myVertices.get(0).size());
				
				if (inBoundsX && inBoundsY) {				
					face.addVertex(myVertices.get(X).get(Y));
					face.addVertex(myVertices.get(X+1).get(Y));
					face.addVertex(myVertices.get(X+1).get(Y+1));
					face.addVertex(myVertices.get(X).get(Y+1));	
				}
				fCol.add(face);
			}
			myFaces.add(fCol);
		}
		System.out.print("Face map is " + myFaces.size() + " columns by " + myFaces.get(0).size() + " rows.\n");
	}
	
	public ArrayList<Face> vfQuery(Vertex v) {

		ArrayList<Face> adjacentFaces = new ArrayList<Face>();
		
		int c = v.getCol();
		int r = v.getRow();
		
		//System.out.print("vfQuery for point: (" + c + ", " + r + ")\n");
		
		if (c >= 0 && c < myFaces.size() && r >=0 && r < myFaces.get(0).size()) {
			adjacentFaces.add(myFaces.get(c).get(r));
		}
		if (c-1 >= 0 && c-1 < myFaces.size() && r >=0 && r < myFaces.get(0).size()) {
			adjacentFaces.add(myFaces.get(c-1).get(r));
		}	
		if (c-1 >= 0 && c-1 < myFaces.size() && r-1 >=0 && r-1 < myFaces.get(0).size()) {
			adjacentFaces.add(myFaces.get(c-1).get(r-1));
		}
		if (c >= 0 && c < myFaces.size() && r-1 >=0 && r-1 < myFaces.get(0).size()) {
			adjacentFaces.add(myFaces.get(c).get(r-1));
		}
		
		//System.out.print("vfQuery returned " + adjacentFaces.size() + " adjacent faces.\n");
		return adjacentFaces;

	}
	
	public List<List<Face>> getFaces() {
		return myFaces;
	}

}