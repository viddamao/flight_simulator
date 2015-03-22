import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.swing.JOptionPane;

import com.jogamp.opengl.util.gl2.GLUT;

import data.Face;
import data.Terrain;
import data.Vertex;
import framework.JOGLFrame;
import framework.Pixmap;
import framework.Scene;

/**
 * Display a simple scene to demonstrate OpenGL.
 * 
 * @author Robert C. Duvall
 */
public class FlightSim extends Scene {

	private final String DEFAULT_MAP_FILE = "images/austrailia_topo.jpg";
	private final float HEIGHT_RATIO = 0.25f;
	private final int TERRAIN_ID = 1;

	// camera controls state
	private static final float SPEED_INCREMENT = 0.02f;
	private final float DEFAULT_FLIGHT_SPEED = 0.01f;
	private float FLIGHT_SPEED = 0.01f;
	private boolean TILT_RIGHT = false;
	private boolean TILT_LEFT = false;
	private boolean OBJECT_ASCEND = false;
	private boolean OBJECT_DESCEND = false;
	private boolean BANK_RIGHT = false;
	private boolean BANK_LEFT = false;

	// animation state
	private float myAngle;
	private float myScale;
	private int myStepSize;
	private int myRenderMode;
	private boolean isCompiled;
	private List<List<Face>> myFaces;
	private Pixmap myHeightMap;
	
	// terrain
	private Terrain myTerrain;

	public FlightSim(String[] args) {
		super("Flight Simulator");
		String name = (args.length > 1) ? args[0] : DEFAULT_MAP_FILE;
		try {
			myHeightMap = new Pixmap((args.length > 1) ? args[0]
					: DEFAULT_MAP_FILE);
		} catch (IOException e) {
			System.err.println("Unable to load texture image: " + name);
			System.exit(1);
		}
	}

	/**
	 * Initialize general OpenGL values once (in place of constructor).
	 */
	@Override
	public void init(GL2 gl, GLU glu, GLUT glut) {
		myAngle = -25.0f;
		myScale = 0.05f;
		myStepSize = 25;
		isCompiled = false;
		myFaces = new ArrayList<List<Face>>();
		myRenderMode = GL2.GL_QUADS;
		
		myTerrain = Terrain.getTerrain();
		myTerrain.init(myHeightMap, myStepSize);
		myTerrain.build();
		
		// make all normals unit length
		gl.glEnable(GL2.GL_NORMALIZE);
		// interpolate color on objects across polygons
		gl.glShadeModel(GL2.GL_SMOOTH);
	}

	/**
	 * Draw all of the objects to display.
	 */
	@Override
	public void display(GL2 gl, GLU glu, GLUT glut) {
		if (!isCompiled) {
			gl.glDeleteLists(TERRAIN_ID, 1);
			gl.glNewList(TERRAIN_ID, GL2.GL_COMPILE);
			drawTerrain(gl, glu, glut);
			gl.glEndList();
			isCompiled = true;
		}
		gl.glScalef(myScale, myScale * HEIGHT_RATIO, myScale);
		gl.glCallList(TERRAIN_ID);
	}

	/**
	 * Animate the scene by changing its state slightly.
	 */
	@Override
	public void animate(GL2 gl, GLU glu, GLUT glut) {
		gl.glTranslatef(0, 0, FLIGHT_SPEED);
		if (BANK_RIGHT) {
			gl.glRotatef(0.5f, 0, 1, 0);
			BANK_RIGHT = false;
		}
		if (BANK_LEFT) {
			gl.glRotatef(-0.5f, 0, 1, 0);
			BANK_LEFT = false;
		}
		if (OBJECT_ASCEND) {
			gl.glRotatef(-0.25f, 1, 0, 0);
			OBJECT_ASCEND = false;
		}
		if (OBJECT_DESCEND) {
			gl.glRotatef(0.25f, 1, 0, 0);
			OBJECT_DESCEND = false;
		}
		if (TILT_RIGHT) {
			gl.glRotatef(0.25f, 0, 0, 1);
			TILT_RIGHT = false;
		}
		if (TILT_LEFT) {
			gl.glRotatef(-0.25f, 0, 0, 1);
			TILT_LEFT = false;
		}
	}

	/**
	 * Set the camera's view of the scene.
	 */
	@Override
	public void setCamera(GL2 gl, GLU glu, GLUT glut) {
		glu.gluLookAt(0, 7, -33, // from position
				0, 5, 20, // to position
				0, 1, 0); // up direction
	}

	/**
	 * Establish lights in the scene.
	 */
	@Override
	public void setLighting(GL2 gl, GLU glu, GLUT glut) {
		float[] light0pos = { 0, 150, 0, 1 };
		float[] light0dir = { 0, -1, 0, 0 };
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0pos, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPOT_DIRECTION, light0dir, 0);
		gl.glLightf(GL2.GL_LIGHT0, GL2.GL_SPOT_CUTOFF, 20);
	}

	/**
	 * Called when any key is pressed within the canvas.
	 */
	@Override
	public void keyPressed(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_A:
			myRenderMode = ((myRenderMode == GL2.GL_QUADS) ? GL2.GL_LINES
					: GL2.GL_QUADS);
			isCompiled = false;
			break;
		case KeyEvent.VK_PERIOD:
			myScale += 0.01f;
			break;
		case KeyEvent.VK_COMMA:
			myScale -= 0.01f;
			break;
		case KeyEvent.VK_OPEN_BRACKET:
			if (myStepSize > 4)
				myStepSize /= 2;
			isCompiled = false;
			break;
		case KeyEvent.VK_CLOSE_BRACKET:
			if (myStepSize < (myHeightMap.getSize().width / 2))
				myStepSize *= 2;
			isCompiled = false;
			break;
		case KeyEvent.VK_UP:
			FLIGHT_SPEED += SPEED_INCREMENT;
			break;
		case KeyEvent.VK_DOWN:
			FLIGHT_SPEED -= SPEED_INCREMENT;
			break;
		case KeyEvent.VK_X:
			TILT_RIGHT = true;
			break;
		case KeyEvent.VK_Z:
			TILT_LEFT = true;
			break;
		case KeyEvent.VK_U:
			OBJECT_ASCEND = true;
			break;
		case KeyEvent.VK_I:
			OBJECT_DESCEND = true;
			break;
		case KeyEvent.VK_RIGHT:
			BANK_RIGHT = true;
			break;
		case KeyEvent.VK_LEFT:
			BANK_LEFT = true;
			break;
		case KeyEvent.VK_R:
			FLIGHT_SPEED = DEFAULT_FLIGHT_SPEED;
			break;
		case KeyEvent.VK_Q:
			JOptionPane.showMessageDialog(null,
					"Thanks for using the FlightSim", "Message",
					JOptionPane.INFORMATION_MESSAGE);
			System.exit(1);
			break;
		}
	}

	private void drawTerrain(GL2 gl, GLU glu, GLUT glut) {
		gl.glBegin(myRenderMode);
		{
			/*for (List<Face> faces : myTerrain.getFaces()) {
				for (Face f : faces) {
					f.drawFace(gl, glu, glut);
				}
			}*/
		}
		gl.glEnd();
	}

	// allow program to be run from here
	public static void main(String[] args) {
		new JOGLFrame(new FlightSim(args));
	}
}
