package main;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;

import data.Face;
import data.Terrain;
import framework.JOGLFrame;
import framework.OBJModel;
import framework.Pixmap;
import framework.Scene;
import framework.Spline;

/**
 * Display a simple scene to demonstrate OpenGL.
 *
 * @author Robert C. Duvall
 */
public class FlightSim extends Scene {

    private float[] CONTROL_POINTS = { -1, 0, 5, 0, -2, 10, 3, 0, 15, 0, 4, 20,
	    5, 0, 20, 0, 2, 25, 6, 3, 30, 5, 5, 25, 3, 3, 20, 1, 2, 10, 5, 0,
	    12, 1, 4, 13, 3, 0, 15, 1, -2, 10, 1, 0, 5 };

    private Spline myCurve;

    private float myTime;
    private float myResolution;

    private static JFrame myFrame;
    private static Scene myScene;
    private final String DEFAULT_MAP_FILE = "images/sierra_elev.jpg";
    private final float HEIGHT_RATIO = 0.25f;
    private final int TERRAIN_ID = 1;

    // camera controls state
    private static final float SPEED_INCREMENT = 0.02f;
    private final float DEFAULT_FLIGHT_SPEED = 0.01f;
    private float FLIGHT_SPEED = 0.01f;
    private float CURRENT_SPEED = 0.01f;
    private boolean TILT_RIGHT = false;
    private boolean TILT_LEFT = false;
    private boolean OBJECT_ASCEND = false;
    private boolean OBJECT_DESCEND = false;
    private boolean BANK_RIGHT = false;
    private boolean BANK_LEFT = false;
    private boolean RESET_VIEW = false;
    private boolean INIT_DONE = false;
    private boolean INTERACTIVE_MODE = false;
    private boolean SHOW_CONTROL = false;
    private boolean SHOW_PATH = false;
    private boolean DISPLAY_MODEL = true;

    // animation state
    private float myAngle;
    private float myScale;
    private int myStepSize;
    private int myRenderMode;
    private boolean isCompiled;
    private ArrayList<List<Face>> myFaces;
    private Pixmap myHeightMap;

    private String myModelFile = "models/vp_data/vp5444-SpaceShuttle.obj";
    private OBJModel myModel;

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
    /*
     * (non-Javadoc)
     *
     * @see framework.Scene#init(javax.media.opengl.GL2,
     * javax.media.opengl.glu.GLU, com.jogamp.opengl.util.gl2.GLUT)
     */
    @Override
    public void init(GL2 gl, GLU glu, GLUT glut) {
	myFaces = new ArrayList<List<Face>>();
	myRenderMode = GL2GL3.GL_QUADS;
	myAngle = -25.0f;
	myScale = 0.05f;
	myStepSize = 1;
	isCompiled = false;
	//myModel = new OBJModel(myModelFile);

	myTime = 0;

	myRenderMode = GL2GL3.GL_QUADS;

	myTerrain = Terrain.getTerrain();
	myTerrain.init(myHeightMap, myStepSize);
	myTerrain.build();

	myCurve = new Spline(CONTROL_POINTS);

	// createSkybox();
	// make all normals unit length
	gl.glEnable(GLLightingFunc.GL_NORMALIZE);

	gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
	// interpolate color on objects across polygons
	gl.glShadeModel(GLLightingFunc.GL_SMOOTH);

	enableFog(gl);

    }

    /**
     * Set up fog and clear color
     *
     * @param gl
     */
    private void enableFog(GL2 gl) {
	float[] fogColor = new float[] { 0.8f, 0.8f, 0.8f, 1.0f };
	gl.glFogfv(GL2ES1.GL_FOG_COLOR, fogColor, 0);
	gl.glFogi(GL2ES1.GL_FOG_MODE, GL2ES1.GL_EXP);
	gl.glFogf(GL2ES1.GL_FOG_START, 10.0f);
	gl.glFogf(GL2ES1.GL_FOG_END, 80.0f);
	gl.glFogf(GL2ES1.GL_FOG_DENSITY, 0.05f);
	gl.glEnable(GL2ES1.GL_FOG);
	gl.glClearColor(fogColor[0], fogColor[1], fogColor[2], 0.0f);
    }

    private void createSkybox() {
	GLProfile glprofile = GLProfile.getDefault();
	GLCapabilities glcapabilities = new GLCapabilities(glprofile);
	final GLCanvas glcanvas = new GLCanvas(glcapabilities);
	glcanvas.addGLEventListener(new Skybox());
	myFrame.getContentPane().add(glcanvas);
	final Animator animator = new Animator(glcanvas);
	myFrame.setSize(400, 200);
	myFrame.addWindowListener(new WindowAdapter() {

	    @Override
	    public void windowClosing(WindowEvent e) {
		new Thread(new Runnable() {

		    @Override
		    public void run() {

			animator.stop();
			System.exit(0);
		    }
		}).start();
	    }
	});
	// myFrame.setVisible(true);
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

	// draw spline
	if (SHOW_PATH) {
	    //myCurve.draw(gl, myResolution);
	}

	if (SHOW_CONTROL) {
	    gl.glPointSize(5);
	    gl.glColor3f(1, 0, 0);
	    myCurve.drawControlPoints(gl);

	}

	if (INTERACTIVE_MODE) {
	    float[] pt = myCurve.evaluateAt(myTime);
	    gl.glTranslatef(-pt[0], -pt[1], -pt[2]);
	    gl.glTranslatef(0, -5, -8);

	    gl.glPushMatrix();
	    gl.glTranslatef(pt[0], pt[1], pt[2]);
	    gl.glTranslatef(0, 8, -5);
	    if (DISPLAY_MODEL)
	    glut.glutSolidCube(1);
	    else{
		gl.glEnable(GL2.GL_MAP1_VERTEX_3);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, myRenderMode);
	    	gl.glScalef(2, 2, 2);
	    	myModel.render(gl);
	    	gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
	    	gl.glDisable(GL2.GL_MAP1_VERTEX_3);
	    }
	    gl.glPopMatrix();
	}

	gl.glScalef(myScale, myScale * HEIGHT_RATIO, myScale);
	gl.glCallList(TERRAIN_ID);
    }

    /**
     * Animate the scene by changing its state slightly.
     */
    @Override
    public void animate(GL2 gl, GLU glu, GLUT glut) {
	if (!INIT_DONE) {
	    gl.glPushMatrix();
	    INIT_DONE = true;
	}
	if (RESET_VIEW) {
	    gl.glPopMatrix();
	    myTime = 0;
	    RESET_VIEW = false;
	    INIT_DONE = false;
	}

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

	if (!INTERACTIVE_MODE) {
	    gl.glTranslatef(0, 0, FLIGHT_SPEED);
	} else {

	    myTime += CURRENT_SPEED;
	}
    }

    /**
     * Set the camera's view of the scene.
     */
    @Override
    public void setCamera(GL2 gl, GLU glu, GLUT glut) {
	glu.gluLookAt(0, 7, -33, // from position
		0, 5, 20, // to position
		0, 0, 1); // up direction
    }

    /**
     * Establish lights in the scene.
     */
    @Override
    public void setLighting(GL2 gl, GLU glu, GLUT glut) {
	float[] light0pos = { 0, 150, 0, 1 };
	float[] light0dir = { 0, -1, 0, 0 };
	gl.glEnable(GLLightingFunc.GL_LIGHTING);
	gl.glEnable(GLLightingFunc.GL_LIGHT0);
	gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION,
		light0pos, 0);
	gl.glLightfv(GLLightingFunc.GL_LIGHT0,
		GLLightingFunc.GL_SPOT_DIRECTION, light0dir, 0);
	gl.glLightf(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPOT_CUTOFF, 20);
    }

    /**
     * Called when any key is pressed within the canvas.
     */
    @Override
    public void keyPressed(int keyCode) {
	if (!INTERACTIVE_MODE) {
	    switch (keyCode) {
	    case KeyEvent.VK_O:
		INTERACTIVE_MODE = true;
		break;
	    case KeyEvent.VK_A:
		myRenderMode = ((myRenderMode == GL2GL3.GL_QUADS) ? GL.GL_LINES
			: GL2GL3.GL_QUADS);
		isCompiled = false;
		break;
	    case KeyEvent.VK_PERIOD:
		myScale += 0.01f;
		break;
	    case KeyEvent.VK_COMMA:
		myScale -= 0.01f;
		break;
	    case KeyEvent.VK_OPEN_BRACKET:
		if (myStepSize > 4) {
		    myStepSize /= 2;
		}
		isCompiled = false;
		break;
	    case KeyEvent.VK_CLOSE_BRACKET:
		if (myStepSize < (myHeightMap.getSize().width / 2)) {
		    myStepSize *= 2;
		}
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
		RESET_VIEW = true;
		FLIGHT_SPEED = DEFAULT_FLIGHT_SPEED;
		break;
	    case KeyEvent.VK_Q:
		JOptionPane.showMessageDialog(null,
			"Thanks for using the FlightSim", "Message",
			JOptionPane.INFORMATION_MESSAGE);
		System.exit(1);
		break;
	    }
	} else {
	    switch (keyCode) {
	    case KeyEvent.VK_A:
		myRenderMode = ((myRenderMode == GL2GL3.GL_QUADS) ? GL.GL_LINES
			: GL2GL3.GL_QUADS);
		isCompiled = false;
		break;
	    case KeyEvent.VK_PERIOD:
		myScale += 0.01f;
		break;
	    case KeyEvent.VK_COMMA:
		myScale -= 0.01f;
		break;
	    case KeyEvent.VK_OPEN_BRACKET:
		if (myStepSize > 4) {
		    myStepSize /= 2;
		}
		isCompiled = false;
		break;
	    case KeyEvent.VK_CLOSE_BRACKET:
		if (myStepSize < (myHeightMap.getSize().width / 2)) {
		    myStepSize *= 2;
		}
		isCompiled = false;
		break;
	    case KeyEvent.VK_UP:
		CURRENT_SPEED += SPEED_INCREMENT;
		break;
	    case KeyEvent.VK_DOWN:
		CURRENT_SPEED -= SPEED_INCREMENT;
		break;
	    case KeyEvent.VK_X:
		TILT_RIGHT = true;
		break;
	    case KeyEvent.VK_Z:
		TILT_LEFT = true;
		break;
	    case KeyEvent.VK_O:
		INTERACTIVE_MODE = false;
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
		RESET_VIEW = true;
		FLIGHT_SPEED = DEFAULT_FLIGHT_SPEED;
		break;
	    case KeyEvent.VK_K:
		SHOW_CONTROL = !SHOW_CONTROL;

		break;
	    case KeyEvent.VK_J:
		SHOW_PATH = !SHOW_PATH;

		break;
	    case KeyEvent.VK_H:
		DISPLAY_MODEL = !DISPLAY_MODEL;

		break;
	    case KeyEvent.VK_Q:
		JOptionPane.showMessageDialog(null,
			"Thanks for using the FlightSim", "Message",
			JOptionPane.INFORMATION_MESSAGE);
		System.exit(1);
		break;
	    }
	}
    }

    private void drawTerrain(GL2 gl, GLU glu, GLUT glut) {
	gl.glBegin(myRenderMode);
	{
	    for (List<Face> faces : myTerrain.getFaces()) {
		for (Face f : faces) {
		    f.drawFace(gl, glu, glut);
		}
	    }
	}
	gl.glEnd();
    }

    public static void main(String[] args) {
	myScene = new FlightSim(args);
	myFrame = new JOGLFrame(myScene);
	myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
