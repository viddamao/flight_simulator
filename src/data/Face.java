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
	private double[][] myGrid=new double[5][5];
	private ImprovedNoise myNoise=new ImprovedNoise();
	
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
	
	
	public void drawPreprocessedGrid(){
	    
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
	
	@SuppressWarnings("static-access")
	private void preprocess(){
	    myGrid[0][0]= myVertices.get(0).getY();
	    myGrid[4][0]= myVertices.get(1).getY();
	    myGrid[4][4]= myVertices.get(2).getY();
	    myGrid[0][4]= myVertices.get(3).getY();
	    
	    //1 pass
	    double xAvg=(myVertices.get(0).getX()+myVertices.get(2).getX())/2;
	    double yAvg=(myVertices.get(0).getZ()+myVertices.get(4).getZ())/2;
	    double zAvg=(myGrid[0][0]+myGrid[0][4]+myGrid[4][0]+myGrid[4][4])/4;
	    myGrid[2][2]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    //2 pass
	    xAvg=(myVertices.get(0).getX()+myVertices.get(2).getX())/2;
	    yAvg=(myVertices.get(0).getZ()+myVertices.get(2).getZ())/2;
	    zAvg=(myGrid[0][0]+myGrid[2][2]+myGrid[4][0]+myGrid[2][2])/4;
	    myGrid[2][0]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=(myVertices.get(0).getX()+myVertices.get(4).getX())/2;
	    yAvg=(myVertices.get(0).getZ()+myVertices.get(4).getZ())/2;
	    zAvg=(myGrid[0][0]+myGrid[2][2]+myGrid[0][4]+myGrid[2][2])/4;
	    myGrid[0][2]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=(myVertices.get(3).getX()+myVertices.get(4).getX())/2;
	    yAvg=(myVertices.get(3).getZ()+myVertices.get(4).getZ())/2;
	    zAvg=(myGrid[0][4]+myGrid[2][2]+myGrid[4][4]+myGrid[2][2])/4;
	    myGrid[2][4]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=(myVertices.get(2).getX()+myVertices.get(3).getX())/2;
	    yAvg=(myVertices.get(2).getZ()+myVertices.get(3).getZ())/2;
	    zAvg=(myGrid[4][0]+myGrid[2][2]+myGrid[4][4]+myGrid[2][2])/4;
	    myGrid[4][2]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    //3 pass
	    xAvg=(myVertices.get(0).getX()+myVertices.get(0).getX()+
		    myVertices.get(0).getX()+myVertices.get(2).getX())/4;
	    yAvg=(myVertices.get(0).getZ()+myVertices.get(0).getZ()+
		    myVertices.get(0).getZ()+myVertices.get(4).getZ())/4;
	    zAvg=(myGrid[0][0]+myGrid[2][0]+myGrid[0][2]+myGrid[2][2])/4;
	    myGrid[1][1]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=(myVertices.get(0).getX()+myVertices.get(2).getX()+
		    myVertices.get(2).getX()+myVertices.get(2).getX())/4;
	    yAvg=(myVertices.get(0).getZ()+myVertices.get(0).getZ()+
		    myVertices.get(0).getZ()+myVertices.get(4).getZ())/4;
	    zAvg=(myGrid[2][0]+myGrid[2][2]+myGrid[4][2]+myGrid[4][0])/4;
	    myGrid[3][1]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=(myVertices.get(0).getX()+myVertices.get(0).getX()+
		    myVertices.get(0).getX()+myVertices.get(2).getX())/4;
	    yAvg=(myVertices.get(0).getZ()+myVertices.get(4).getZ()+
		    myVertices.get(4).getZ()+myVertices.get(4).getZ())/4;
	    zAvg=(myGrid[0][2]+myGrid[2][2]+myGrid[0][4]+myGrid[2][4])/4;
	    myGrid[1][3]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=(myVertices.get(0).getX()+myVertices.get(2).getX()+
		    myVertices.get(2).getX()+myVertices.get(2).getX())/4;
	    yAvg=(myVertices.get(0).getZ()+myVertices.get(4).getZ()+
		    myVertices.get(4).getZ()+myVertices.get(4).getZ())/4;
	    zAvg=(myGrid[2][2]+myGrid[2][4]+myGrid[4][2]+myGrid[4][4])/4;
	    myGrid[3][3]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    
	    //4 pass
	    
	    xAvg=myVertices.get(0).getX();
	    yAvg=(myVertices.get(0).getZ()+myVertices.get(0).getZ()+
		    myVertices.get(0).getZ()+myVertices.get(4).getZ())/4;
	    zAvg=(myGrid[0][0]+myGrid[0][2]+myGrid[1][1]+myGrid[1][1])/4;
	    myGrid[0][1]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=myVertices.get(0).getX();
	    yAvg=(myVertices.get(0).getZ()+myVertices.get(4).getZ()+
		    myVertices.get(4).getZ()+myVertices.get(4).getZ())/4;
	    zAvg=(myGrid[0][4]+myGrid[0][2]+myGrid[1][3]+myGrid[1][3])/4;
	    myGrid[0][3]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=(myVertices.get(0).getX()+myVertices.get(0).getX()+
		    myVertices.get(0).getX()+myVertices.get(2).getX())/4;
	    yAvg=myVertices.get(0).getZ();
	    zAvg=(myGrid[0][0]+myGrid[2][0]+myGrid[1][1]+myGrid[1][1])/4;
	    myGrid[1][0]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=(myVertices.get(0).getX()+myVertices.get(0).getX()+
		    myVertices.get(0).getX()+myVertices.get(2).getX())/4;
	    yAvg=(myVertices.get(0).getZ()+myVertices.get(4).getZ())/2;
	    zAvg=(myGrid[1][1]+myGrid[2][0]+myGrid[2][2]+myGrid[1][3])/4;
	    myGrid[1][2]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=(myVertices.get(0).getX()+myVertices.get(0).getX()+
		    myVertices.get(0).getX()+myVertices.get(2).getX())/4;
	    yAvg=myVertices.get(4).getZ();
	    zAvg=(myGrid[0][4]+myGrid[2][4]+myGrid[1][3]+myGrid[1][3])/4;
	    myGrid[1][4]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=(myVertices.get(0).getX()+myVertices.get(2).getX())/2;
	    yAvg=(myVertices.get(0).getZ()+myVertices.get(0).getZ()+
		    myVertices.get(0).getZ()+myVertices.get(4).getZ())/4;
	    zAvg=(myGrid[1][1]+myGrid[3][1]+myGrid[2][2]+myGrid[2][0])/4;
	    myGrid[2][1]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=(myVertices.get(0).getX()+myVertices.get(2).getX())/2;
	    yAvg=(myVertices.get(0).getZ()+myVertices.get(4).getZ()+
		    myVertices.get(4).getZ()+myVertices.get(4).getZ())/4;
	    zAvg=(myGrid[2][2]+myGrid[1][3]+myGrid[3][3]+myGrid[2][4])/4;
	    myGrid[2][3]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=(myVertices.get(0).getX()+myVertices.get(2).getX()+
		    myVertices.get(2).getX()+myVertices.get(2).getX())/4;
	    yAvg=myVertices.get(0).getZ();
	    zAvg=(myGrid[2][0]+myGrid[4][0]+myGrid[3][1]+myGrid[3][1])/4;
	    myGrid[3][0]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=(myVertices.get(0).getX()+myVertices.get(2).getX()+
		    myVertices.get(2).getX()+myVertices.get(2).getX())/4;
	    yAvg=(myVertices.get(0).getZ()+myVertices.get(4).getZ())/2;
	    zAvg=(myGrid[2][2]+myGrid[4][2]+myGrid[3][1]+myGrid[3][3])/4;
	    myGrid[3][2]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=(myVertices.get(0).getX()+myVertices.get(2).getX()+
		    myVertices.get(2).getX()+myVertices.get(2).getX())/4;
	    yAvg=myVertices.get(4).getZ();
	    zAvg=(myGrid[2][4]+myGrid[4][4]+myGrid[3][3]+myGrid[3][3])/4;
	    myGrid[3][4]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=myVertices.get(2).getX();
	    yAvg=(myVertices.get(0).getZ()+myVertices.get(0).getZ()+
		    myVertices.get(0).getZ()+myVertices.get(4).getZ())/4;
	    zAvg=(myGrid[4][2]+myGrid[4][0]+myGrid[3][1]+myGrid[3][1])/4;
	    myGrid[4][1]=myNoise.noise(xAvg,yAvg,zAvg);
	    
	    xAvg=myVertices.get(2).getX();
	    yAvg=(myVertices.get(0).getZ()+myVertices.get(4).getZ()+
		    myVertices.get(4).getZ()+myVertices.get(4).getZ())/4;
	    zAvg=(myGrid[4][2]+myGrid[4][4]+myGrid[3][3]+myGrid[3][3])/4;
	    myGrid[4][3]=myNoise.noise(xAvg,yAvg,zAvg);
	    
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