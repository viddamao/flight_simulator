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
		for (int X = 0; X < width - myStepSize; X += myStepSize) {
			List<Face> row = new ArrayList<Face>();
			myFaces.add(row);
			for (int Y = 0; Y < width - myStepSize; Y+= myStepSize) {
				Face face = new Face();

				// set (x, y, z) value for bottom left vertex
				float x0 = X - width / 2.0f;
				float y0 = myHeightMap.getColor(X, Y).getRed();
				float z0 = Y - height / 2.0f;

				// set (x, y, z) value for top left vertex
				float x1 = x0;
				float y1 = myHeightMap.getColor(X, Y + myStepSize).getRed();
				float z1 = z0 + myStepSize;

				// set (x, y, z) value for top right vertex
				float x2 = x0 + myStepSize;
				float y2 = myHeightMap.getColor(X + myStepSize, Y + myStepSize)
						.getRed();
				float z2 = z0 + myStepSize;

				// set (x, y, z) value for bottom right vertex
				float x3 = x0 + myStepSize;
				float y3 = myHeightMap.getColor(X + myStepSize, Y).getRed();
				float z3 = z0;

				Vertex v0 = new Vertex(x0, z0, y0);
				Vertex v1 = new Vertex(x1, z1, y1);
				Vertex v2 = new Vertex(x2, z2, y2);
				Vertex v3 = new Vertex(x3, z3, y3);

				face.addVertex(v0);
				face.addVertex(v1);
				face.addVertex(v2);
				face.addVertex(v3);

				row.add(face);
			}
			myFaces.add(row);
		}
	}

	public void setStepSize(int step) {
		myStepSize = step;
	}
	
	public List<List<Face>> getFaces() {
		return myFaces;
	}

}
