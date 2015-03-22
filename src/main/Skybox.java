package main;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class Skybox implements GLEventListener {

    private Texture starTexture;

    @Override
    public void init(GLAutoDrawable drawable) {

	GL2 gl = (GL2) drawable.getGL();
	gl.glShadeModel(GL2.GL_SMOOTH); 
	try {
	    GLProfile myProfile = drawable.getGLProfile();
	    InputStream stream = getClass().getResourceAsStream(
		    "../data/skybox_up.rgb");
	    TextureData data = TextureIO.newTextureData(myProfile, stream,
		    false, "rgb");
	    starTexture = TextureIO.newTexture(data);
	} catch (IOException exc) {
	    exc.printStackTrace();
	    System.exit(1);
	}
	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S,
		GL2.GL_CLAMP);
	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T,
		GL2.GL_CLAMP);
	// Enable VSync
	gl.setSwapInterval(1);

	// Setup the drawing area and shading mode
	gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
	    int height) {
	GL2 gl = (GL2) drawable.getGL();
	GLU glu = new GLU();

	if (height <= 0) { 

	    height = 1;
	}
	final float h = (float) width / (float) height;
	gl.glViewport(0, 0, width, height);
	gl.glMatrixMode(GL2.GL_PROJECTION);
	gl.glLoadIdentity();
	glu.gluPerspective(45.0f, h, 1.0, 20.0);
	gl.glMatrixMode(GL2.GL_MODELVIEW);
	gl.glLoadIdentity();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
	GLU glu = new GLU();
	GL2 gl = (GL2) drawable.getGL();
	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
	gl.glLoadIdentity();
	gl.glPushMatrix();
	// Reset and transform the matrix.
	gl.glLoadIdentity();
	glu.gluLookAt(0, 0, 1, // from position
		0, 0, 0, // to position
		0, 1, 0); // up direction
	// Enable/Disable features
	gl.glPushAttrib(GL2.GL_ENABLE_BIT);
	gl.glEnable(GL.GL_TEXTURE_2D);
	gl.glDisable(GL.GL_DEPTH_TEST);
	gl.glDisable(GLLightingFunc.GL_LIGHTING);
	gl.glDisable(GL.GL_BLEND);
	gl.glColor4f(1, 1, 1, 1);
	starTexture.enable(drawable.getGL());
	starTexture.bind(drawable.getGL());
	// right
	gl.glBegin(GL2.GL_QUADS);
	gl.glTexCoord2f(0, 0);
	gl.glVertex3f(0.5f, -0.5f, -0.5f);
	gl.glTexCoord2f(1, 0);
	gl.glVertex3f(-0.5f, -0.5f, -0.5f);
	gl.glTexCoord2f(1, 1);
	gl.glVertex3f(-0.5f, 0.5f, -0.5f);
	gl.glTexCoord2f(0, 1);
	gl.glVertex3f(0.5f, 0.5f, -0.5f);
	gl.glEnd();

	// left
	gl.glBegin(GL2.GL_QUADS);
	gl.glTexCoord2f(0, 0);
	gl.glVertex3f(-0.5f, -0.5f, 0.5f);
	gl.glTexCoord2f(1, 0);
	gl.glVertex3f(0.5f, -0.5f, 0.5f);
	gl.glTexCoord2f(1, 1);
	gl.glVertex3f(0.5f, 0.5f, 0.5f);
	gl.glTexCoord2f(0, 1);
	gl.glVertex3f(-0.5f, 0.5f, 0.5f);
	gl.glEnd();
	//back
	gl.glBegin(GL2.GL_QUADS);
	gl.glTexCoord2f(0, 0);
	gl.glVertex3f(-0.5f, -0.5f, -0.5f);
	gl.glTexCoord2f(1, 0);
	gl.glVertex3f(-0.5f, -0.5f, 0.5f);
	gl.glTexCoord2f(1, 1);
	gl.glVertex3f(-0.5f, 0.5f, 0.5f);
	gl.glTexCoord2f(0, 1);
	gl.glVertex3f(-0.5f, 0.5f, -0.5f);
	gl.glEnd();
	//up
	gl.glBegin(GL2.GL_QUADS);
	gl.glTexCoord2f(0, 0);
	gl.glVertex3f(-0.5f, -0.5f, -0.5f);
	gl.glTexCoord2f(0, 1);
	gl.glVertex3f(-0.5f, -0.5f, 0.5f);
	gl.glTexCoord2f(1, 1);
	gl.glVertex3f(0.5f, -0.5f, 0.5f);
	gl.glTexCoord2f(1, 0);
	gl.glVertex3f(0.5f, -0.5f, -0.5f);
	gl.glEnd();
	//down
	gl.glBegin(GL2.GL_QUADS);
	gl.glTexCoord2f(0, 1);
	gl.glVertex3f(-0.5f, 0.5f, -0.5f);
	gl.glTexCoord2f(0, 0);
	gl.glVertex3f(-0.5f, 0.5f, 0.5f);
	gl.glTexCoord2f(1, 0);
	gl.glVertex3f(0.5f, 0.5f, 0.5f);
	gl.glTexCoord2f(1, 1);
	gl.glVertex3f(0.5f, 0.5f, -0.5f);
	gl.glEnd();
	// front
	gl.glBegin(GL2.GL_QUADS);
	gl.glTexCoord2f(0, 0);
	gl.glVertex3f(0.5f, -0.5f, 0.5f);
	gl.glTexCoord2f(1, 0);
	gl.glVertex3f(0.5f, -0.5f, -0.5f);
	gl.glTexCoord2f(1, 1);
	gl.glVertex3f(0.5f, 0.5f, -0.5f);
	gl.glTexCoord2f(0, 1);
	gl.glVertex3f(0.5f, 0.5f, 0.5f);
	gl.glEnd();
	gl.glPopAttrib();
	gl.glPopMatrix();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
	// TODO Auto-generated method stub

    }

}