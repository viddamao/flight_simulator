package main;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JOptionPane;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;

import data.Face;
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

    private static JOGLFrame myFrame;
    private static Scene myScene;
    private final String DEFAULT_MAP_FILE = "images/sierra_elev.jpg";
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
    private ArrayList<Face> myFaces;
    private Pixmap myHeightMap;

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
	myStepSize = 1;
	isCompiled = false;
	myFaces = new ArrayList<Face>();
	myRenderMode = GL2.GL_QUADS;

	createSkybox();
	initTerrain(gl, glu, glut);
	// make all normals unit length
	gl.glEnable(GL2.GL_NORMALIZE);
	// interpolate color on objects across polygons
	gl.glShadeModel(GL2.GL_SMOOTH);
    }

    private void createSkybox() {
	GLCanvas canvas = new GLCanvas();
	canvas.addGLEventListener(new Skybox());
	myFrame.add(canvas);
	myFrame.setSize(640, 480);
	final Animator animator = new Animator(canvas);
	myFrame.addWindowListener(new WindowAdapter() {

	    @Override
	    public void windowClosing(WindowEvent e) {
		// Run this on another thread than the AWT event queue to
		// make sure the call to Animator.stop() completes before
		// exiting
		new Thread(new Runnable() {

		    public void run() {
			animator.stop();
			System.exit(0);
		    }
		}).start();
	    }
	});
	// Center frame
	myFrame.setLocationRelativeTo(null);
	myFrame.setVisible(true);
	animator.start();

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
	    gl.glRotatef(0.25f, 0, 1, 0);
	    BANK_RIGHT = false;
	}
	if (BANK_LEFT) {
	    gl.glRotatef(-0.25f, 0, 1, 0);
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

    private void initTerrain(GL2 gl, GLU glu, GLUT glut) {
	int width = myHeightMap.getSize().width;
	int height = myHeightMap.getSize().height;
	for (int X = 0; X < width - myStepSize; X++) {
	    for (int Y = 0; Y < width - myStepSize; Y++) {
		Face face = new Face();

		// set (x, y, z) value for bottom left vertex
		float x0 = X - width / 2.0f;
		float y0 = myHeightMap.getColor(X, Y).getRed();
		float z0 = Y - height / 2.0f;
		face.addVertex(new Vertex(x0, y0, z0));

		// set (x, y, z) value for top left vertex
		float x1 = x0;
		float y1 = myHeightMap.getColor(X, Y + myStepSize).getRed();
		float z1 = z0 + myStepSize;
		face.addVertex(new Vertex(x1, y1, z1));

		// set (x, y, z) value for top right vertex
		float x2 = x0 + myStepSize;
		float y2 = myHeightMap.getColor(X + myStepSize, Y + myStepSize)
			.getRed();
		float z2 = z0 + myStepSize;
		face.addVertex(new Vertex(x2, y2, z2));

		// set (x, y, z) value for bottom right vertex
		float x3 = x0 + myStepSize;
		float y3 = myHeightMap.getColor(X + myStepSize, Y).getRed();
		float z3 = z0;
		face.addVertex(new Vertex(x3, y3, z3));

		myFaces.add(face);

		// face.printDiagnosticInfo();
	    }
	}
    }

    private void drawTerrain(GL2 gl, GLU glu, GLUT glut) {
	gl.glBegin(myRenderMode);
	{
	    for (Face f : myFaces) {
		f.calculateFaceNormal(gl, glu, glut);
		f.drawFace(gl, glu, glut);
	    }
	}
	gl.glEnd();
    }

    // */

    // allow program to be run from here
    public static void main(String[] args) {
	myScene = new FlightSim(args);
	myFrame = new JOGLFrame(myScene);
    }
}
