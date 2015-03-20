package data;

public class Vertex {
	
	private float X;
	private float Y;
	private float Z;
	
	public Vertex(float x, float y, float z) {
		setX(x);
		setY(y);
		setZ(z);
	}

	public float getX() {
		return X;
	}

	public void setX(float x) {
		X = x;
	}

	public float getY() {
		return Y;
	}

	public void setY(float y) {
		Y = y;
	}

	public float getZ() {
		return Z;
	}

	public void setZ(float z) {
		Z = z;
	}
	
}
