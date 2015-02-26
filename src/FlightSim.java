import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

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
    private final float HEIGHT_RATIO = 1.5f;
    private final int TERRAIN_ID = 1;
    // animation state
    private float myAngle;
    private float myScale;
    private int myStepSize;
    private int myRenderMode;
    private boolean isCompiled;
    private Pixmap myHeightMap;


    public FlightSim (String[] args) {
        super("Flight Simulator");
        String name = (args.length > 1) ? args[0] : DEFAULT_MAP_FILE;
        try {
            myHeightMap = new Pixmap((args.length > 1) ? args[0] : DEFAULT_MAP_FILE);
        } catch (IOException e) {
            System.err.println("Unable to load texture image: " + name);
            System.exit(1);
        }
    }

    /**
     * Initialize general OpenGL values once (in place of constructor).
     */
    @Override
    public void init (GL2 gl, GLU glu, GLUT glut) {
        myAngle = 0.0f;
        myScale = 0.05f;
        myStepSize = 16;
        isCompiled = false;
        myRenderMode = GL2.GL_QUADS;
        // make all normals unit length
        gl.glEnable(GL2.GL_NORMALIZE);
        // interpolate color on objects across polygons
        gl.glShadeModel(GL2.GL_SMOOTH);
    }

    /**
     * Draw all of the objects to display.
     */
    @Override
    public void display (GL2 gl, GLU glu, GLUT glut) {
        if (!isCompiled) {
            gl.glDeleteLists(TERRAIN_ID, 1);
            gl.glNewList(TERRAIN_ID, GL2.GL_COMPILE);
            drawTerrain(gl, glu, glut);
            gl.glEndList();
            isCompiled = true;
        }
        gl.glRotatef(myAngle, 1.0f, 0.0f, 0.0f);
        gl.glScalef(myScale, myScale * HEIGHT_RATIO, myScale);
        gl.glCallList(TERRAIN_ID);
    }

    /**
     * Animate the scene by changing its state slightly.
     */
    @Override
    public void animate (GL2 gl, GLU glu, GLUT glut) {
        myAngle += 0.25;
    }

    /**
     * Set the camera's view of the scene.
     */
    @Override
    public void setCamera (GL2 gl, GLU glu, GLUT glut) {
        glu.gluLookAt(0, 150, 5, // from position
                      0, 0, 0,   // to position
                      0, 1, 0);  // up direction
    }

    /**
     * Establish lights in the scene.
     */
    @Override
    public void setLighting (GL2 gl, GLU glu, GLUT glut) {
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
    public void keyPressed (int keyCode) {
        switch (keyCode) {
        case KeyEvent.VK_A:
            myRenderMode = ((myRenderMode == GL2.GL_QUADS) ? GL2.GL_LINES : GL2.GL_QUADS);
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
        }
    }

    private void drawTerrain (GL2 gl, GLU glu, GLUT glut) {
        int width = myHeightMap.getSize().width;
        int height = myHeightMap.getSize().height;
        gl.glBegin(myRenderMode); {
            for (int X = 0; X < width; X += myStepSize) {
                for (int Y = 0; Y < height; Y += myStepSize) {
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
                    float y2 = myHeightMap.getColor(X + myStepSize, Y + myStepSize).getRed();
                    float z2 = z0 + myStepSize;
                    // set (x, y, z) value for bottom right vertex
                    float x3 = x0 + myStepSize;
                    float y3 = myHeightMap.getColor(X + myStepSize, Y).getRed();
                    float z3 = z0;
                    // set normal vector for face
                    float nx = (y0 - y1) * (z0 + z1) + (y1 - y2) * (z1 + z2) + (y2 - y3) * (z2 + z3) + (y3 - y0) * (z3 + z0);
                    float ny = (z0 - z1) * (x0 + x1) + (z1 - z2) * (x1 + x2) + (z2 - z3) * (x2 + x3) + (z3 - z0) * (x3 + x0);
                    float nz = (x0 - x1) * (y0 + y1) + (x1 - x2) * (y1 + y2) + (x2 - x3) * (y2 + y3) + (x3 - x0) * (y3 + y0);
                    gl.glNormal3f(nx, ny, nz);
                    // set vertices
                    gl.glVertex3f(x0, y0, z0);
                    gl.glVertex3f(x1, y1, z1);
                    if (myRenderMode == GL2.GL_LINES) {
                        gl.glVertex3f(x1, y1, z1);
                    }
                    gl.glVertex3f(x2, y2, z2);
                    if (myRenderMode == GL2.GL_LINES) {
                        gl.glVertex3f(x2, y2, z2);
                    }
                    gl.glVertex3f(x3, y3, z3);
                    if (myRenderMode == GL2.GL_LINES) {
                        gl.glVertex3f(x3, y3, z3);
                        gl.glVertex3f(x0, y0, z0);
                    }
                }
            }
        }
        gl.glEnd();
    }


    // allow program to be run from here
    public static void main (String[] args) {
        new JOGLFrame(new FlightSim(args));
    }
}
