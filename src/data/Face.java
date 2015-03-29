package data;

import data.Vertex;
import framework.ImprovedNoise;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

public class Face implements Comparable {

	private ArrayList<Face> myAdjacentFaces;
	private ArrayList<Vertex> myVertices;
	private float[] myFaceNormal;
	private int myCol;
	private int myRow;
	private Vertex myAnchor;
	private Vertex[][] myGrid;
	private ImprovedNoise myNoise=new ImprovedNoise();
	
	public Face() {
		myAdjacentFaces = new ArrayList<Face>();
		myVertices = new ArrayList<Vertex>();
		myFaceNormal = new float[3];
		myGrid=new Vertex[5][5];
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

	public float[] calculateFaceNormal(GL2 gl, GLU glu, GLUT glut) {
		Vertex v0 = myVertices.get(0);
		Vertex v1 = myVertices.get(1);
		Vertex v2 = myVertices.get(2);
		Vertex v3 = myVertices.get(3);

		float nx = (v0.getY() - v1.getY()) * (v0.getZ() + v1.getZ())
				+ (v1.getY() - v2.getY()) * (v1.getZ() + v2.getZ())
				+ (v2.getY() - v3.getY()) * (v2.getZ() + v3.getZ())
				+ (v3.getY() - v0.getY()) * (v3.getZ() + v0.getZ());
		float ny = (v0.getZ() - v1.getZ()) * (v0.getX() + v1.getX())
				+ (v1.getZ() - v2.getZ()) * (v1.getX() + v2.getX())
				+ (v2.getZ() - v3.getZ()) * (v2.getX() + v3.getX())
				+ (v3.getZ() - v0.getZ()) * (v3.getX() + v0.getX());
		float nz = (v0.getX() - v1.getX()) * (v0.getY() + v1.getY())
				+ (v1.getX() - v2.getX()) * (v1.getY() + v2.getY())
				+ (v2.getX() - v3.getX()) * (v2.getY() + v3.getY())
				+ (v3.getX() - v0.getX()) * (v3.getY() + v0.getY());

		myFaceNormal[0] = nx;
		myFaceNormal[1] = ny;
		myFaceNormal[2] = nz;

		return myFaceNormal;
	}
	
	
	
	public void drawFace(GL2 gl, GLU glu, GLUT glut) {
	    	int drawDetail=0;
		// System.out.print("--------------\nDRAWING FACE:\n");
		for (Vertex v : myVertices) {
		    	if (v.getDetailed()||drawDetail==4){ 
			float[] normal = v.getVertexNormal(gl, glu, glut);
			// System.out.printf("Normal: {%f, %f, %f}  ", normal[0], normal[1],
			// normal[2]);
			gl.glNormal3f(normal[0], normal[1], normal[2]);
			// System.out.printf("Coordinates: (%f, %f, %f)\n", v.getX(),
			// v.getY(), v.getZ());
			gl.glVertex3f(v.getX(), v.getY(), v.getZ());
		    	drawDetail=0;
		    	}
		    	else drawDetail+=1;
		}
	}
	
	public void drawFacePre(GL2 gl, GLU glu, GLUT glut) {
	   	
	    	for (int i=0;i<5;i++) {
		    for (int j=0;j<5;j++){
			float[] normal = myGrid[i][j].getVertexNormal(gl, glu, glut);
			
			gl.glNormal3f(normal[0], normal[1], normal[2]);
			gl.glVertex3f(myGrid[i][j].getX(), myGrid[i][j].getY(), myGrid[i][j].getZ());
		    }
	   	}
	}
	
	
	public int compareTo(Object o) {
		Face f = (Face) o;
		if (this.getAnchor().getY() > f.getAnchor().getY()) {
			return -1;
		} else if (this.getAnchor().getY() < f.getAnchor().getY()) {
			return 1;
		} else {
			if (this.getAnchor().getX() > f.getAnchor().getX()) {
				return -1;
			} else if (this.getAnchor().getX() < f.getAnchor().getX()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	public void preprocess(){
	    myGrid[0][0]=myVertices.get(0);
	    myGrid[4][0]=myVertices.get(1);
	    myGrid[4][4]=myVertices.get(2);
	    myGrid[0][4]=myVertices.get(3);
	    
	    //1 pass
	    diamondSquare(0,0,4,0,4,4,0,4,2,2);
	    
	    //2 pass
	    diamondSquare(0,0,2,2,0,4,2,2,0,2);
	    diamondSquare(0,0,2,2,4,0,2,2,2,0);
	    diamondSquare(4,0,2,2,4,4,2,2,4,2);
	    diamondSquare(4,4,2,2,0,4,2,2,2,4);
	    
	    //3 pass
	    diamondSquare(0,0,2,0,2,2,0,2,1,1);
	    diamondSquare(2,0,4,0,4,2,2,2,3,1);
	    diamondSquare(2,2,4,2,4,4,2,4,3,3);
	    diamondSquare(0,2,2,2,2,4,0,4,1,3);
	    
	    //4 pass
	   
	    diamondSquare(0,0,1,1,2,0,1,1,1,0);
	    diamondSquare(2,0,3,1,4,0,3,1,3,0);
	    
	    diamondSquare(0,0,1,1,0,2,1,1,0,1);
	    diamondSquare(2,0,3,1,2,2,1,1,2,1);
	    diamondSquare(4,0,3,1,4,2,3,1,4,1);
	   
	    diamondSquare(1,1,2,2,1,3,0,2,1,2);
	    diamondSquare(3,1,4,2,3,3,2,2,3,2);
	    
	    diamondSquare(0,2,1,3,0,4,1,3,0,3);
	    diamondSquare(2,2,3,3,2,4,1,3,2,3);
	    diamondSquare(4,2,3,3,4,4,3,3,4,3);
	    
	    diamondSquare(1,3,2,4,1,3,0,4,1,4);
	    diamondSquare(3,3,4,4,3,3,2,4,3,4);
	    
	    
	}

	/**
	 * 
	 */
	private void diamondSquare(int a1,int a2,int b1,int b2,int c1,int c2,int d1,int d2,int x,int y) {
	    float xAvg=(myGrid[a1][a2].getX()+myGrid[b1][b2].getX())/2;
	    float yAvg=(myGrid[a1][a2].getY()+myGrid[b1][b2].getY()+myGrid[c1][c2].getY()+myGrid[d1][d2].getY())/4;
	    float zAvg=(myGrid[a1][a2].getZ()+myGrid[d1][d2].getZ())/2;
	  
		myGrid[x][y]=new Vertex(xAvg, yAvg, zAvg);
		myGrid[x][y].setY((float) myNoise.noise(xAvg,zAvg,yAvg));
	   
	}
	public int getMyCol() {
		return myCol;
	}

	public void setMyCol(int myCol) {
		this.myCol = myCol;
	}

	public int getMyRow() {
		return myRow;
	}

	public void setMyRow(int myRow) {
		this.myRow = myRow;
	}

	public ArrayList<Vertex> getMyVertices() {
		return myVertices;
	}

}