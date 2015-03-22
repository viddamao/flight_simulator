package data;

import java.util.ArrayList;
import java.util.List;

import framework.Pixmap;

public class Terrain {

	private static Terrain _TERRAIN;
	private Pixmap myHeightMap;
	private List<List<Face>> myFaces;
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
	}

	public void build() {
		int width = myHeightMap.getSize().width;
		int height = myHeightMap.getSize().height;

		for (int X = 0; X < width; X += myStepSize) {
			
			List<Face> col = new ArrayList<Face>();
			
			for (int Y = 0; Y < width; Y+= myStepSize) {
				
				Face face = new Face();

				// CENTER
				float x0 = X - width / 2.0f;
				float y0 = Y - height / 2.0f;
				float z0 = myHeightMap.getColor(X, Y).getRed();
				Vertex v0 = new Vertex(x0, z0, y0);

				// MIDDLE TOP
				float x1 = x0;
				float y1 = y0 + myStepSize;
				float z1 = myHeightMap.getColor((int) x1, (int) y1).getRed();
				Vertex v1 = new Vertex(x1, y1, z1);

				// RIGHT TOP
				float x2 = x0 + myStepSize;
				float y2 = y0 + myStepSize;
				float z2 = myHeightMap.getColor((int) x2, (int) y2).getRed();
				Vertex v2 = new Vertex(x2, y2, z2);
				
				// RIGHT CENTER
				float x3 = x0 + myStepSize;
				float y3 = y0;
				float z3 = myHeightMap.getColor((int) x3, (int) y3).getRed();
				Vertex v3 = new Vertex(x3, y3, z3);
				
				/* ------------
				
				// RIGHT BOTTOM
				float x4 = x0 + myStepSize;
				float y4 = y0 - myStepSize;
				float z4 = myHeightMap.getColor((int) x2, (int) y2).getRed();
				Vertex v4 = new Vertex(x4, y4, z4);
				
				// MIDDLE BOTTOM
				float x5 = x0;
				float y5 = y0 - myStepSize;
				float z5 = myHeightMap.getColor((int) x2, (int) y2).getRed();
				Vertex v5 = new Vertex(x5, y5, z5);
				
				// LEFT BOTTOM
				float x6 = x0 - myStepSize;
				float y6 = y0 - myStepSize;
				float z6 = myHeightMap.getColor((int) x2, (int) y2).getRed();
				Vertex v6 = new Vertex(x6, y6, z6);
				
				// LEFT CENTER
				float x7 = x0 - myStepSize;
				float y7 = y0;
				float z7 = myHeightMap.getColor((int) x2, (int) y2).getRed();
				Vertex v7 = new Vertex(x7, y7, z7);
				
				// LEFT TOP
				float x8 = x0 - myStepSize;
				float y8 = y0 + myStepSize;
				float z8 = myHeightMap.getColor((int) x2, (int) y2).getRed();
				Vertex v8 = new Vertex(x8, y8, z8);
				
				-------------- */

				face.addVertex(v0);
				face.addVertex(v1);
				face.addVertex(v2);
				face.addVertex(v3);

				col.add(face);
			}
			myFaces.add(col);
		}
		buildFaceAdjacencies();
	}
	
	private void buildFaceAdjacencies() {
		for (int c = 0; c < myFaces.size(); c++) {
			for (int r = 0; r < myFaces.get(0).size(); r++) {
				Face current = myFaces.get(c).get(r);
				
				if (isInBounds(c+1, r)) {
					Face top = myFaces.get(c+1).get(r);
					current.addAdjacentFace(top);
				}
				if (isInBounds(c, r+1)) {
					Face right = myFaces.get(c).get(r+1);
					current.addAdjacentFace(right);
				}
				if (isInBounds(c-1, r)) {
					Face bottom = myFaces.get(c-1).get(r);
					current.addAdjacentFace(bottom);
				}
				if (isInBounds(c, r-1)) {
					Face left = myFaces.get(c).get(r-1);
					current.addAdjacentFace(left);
				}
				
			}
		}
	}
	
	private boolean isInBounds(int c, int r) {
		return (c > 0 && c < myFaces.size() && r > 0 && r < myFaces.get(0).size());
	}

	public void setStepSize(int step) {
		myStepSize = step;
	}
	
	public List<List<Face>> getFaces() {
		return myFaces;
	}

}
