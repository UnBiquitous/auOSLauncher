package org.unbiquitous.auoslauncher;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Date;

import javax.microedition.khronos.opengles.GL10;

import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameScene;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.engine.io.ScreenManager;

import android.util.Log;

public class GameMenu extends GameScene {

	Screen localScreen;

	public GameMenu() {
		localScreen = GameComponents.get(ScreenManager.class).create();
		localScreen.open(null, 0, 0, true, null);
		GameComponents.put(Screen.class, localScreen);
		start = new Date(); 
	}

	long count = 0;
	private Date start;
	public void render() {
		GL10 gl = GameComponents.get(GL10.class);
		
		if (gl ==null) return;
		
		gl.glColor4f(1f, ((float)count%256)/256, 0, 1f);
		new SquareGL1().draw(gl);
		
		gl.glColor4f(0.5f, 0.5f, 1.0f, 1.0f);
		new TriangleGL1().draw(gl);
	}

	protected void destroy() {
	}

	protected void update() {
		count ++;
//		System.out.println("Count "+count);
		Date now = new Date();
		if (now.getTime() - start.getTime() > 30*1000){
			GameComponents.get(org.unbiquitous.uImpala.engine.core.Game.class).change(new RedSquareScene());
		}else{
			Log.i("debug", "not yet" + 
					localScreen.getMouse().getX()+","+localScreen.getMouse().getY());
		}
	}

	protected void wakeup(Object... arg0) {
	}
}

class SquareGL1 {
	
	/** The buffer holding the vertices */
	private FloatBuffer vertexBuffer;
	
	/** The initial vertex definition */
//	private float vertices[] = { 
//			-1.0f, -1.0f, 0.0f, //Bottom Left
//			1.0f, -1.0f, 0.0f, 	//Bottom Right
//			-1.0f, 1.0f, 0.0f, 	//Top Left
//			1.0f, 1.0f, 0.0f 	//Top Right
//	};
//	private float vertices[] = { 
//			-.5f, -.5f, 0.0f, //Bottom Left
//			 .5f, -.5f, 0.0f, 	//Bottom Right
//			-.5f,  .5f, 0.0f, 	//Top Left
//			 .5f,  .5f, 0.0f 	//Top Right
//	};
	
	private float vertices[] = { 
			0.0f, 0.0f, 0.0f, //Bottom Left
			0.0f, 100.0f, 0.0f, 	//Bottom Right
			100.0f, 0.0f, 0.0f, 	//Top Right
			100.0f, 100.0f, 0.0f, 	//Top Left
	};
	
	
	/**
	 * The Triangle constructor.
	 * 
	 * Initiate the buffers.
	 */
	public SquareGL1() {
		//
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuf.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
	}

	/**
	 * The object own drawing function.
	 * Called from the renderer to redraw this instance
	 * with possible changes in values.
	 * 
	 * @param gl - The GL context
	 */
	public void draw(GL10 gl) {
		//Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		
		//Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		
		//Enable vertex buffer
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		//Draw the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
		
		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}

class TriangleGL1 {
	
	/** The buffer holding the vertices */
	private FloatBuffer vertexBuffer;
	
	/** The initial vertex definition */
	private float vertices[] = { 
								100.0f, 200.0f, 0.0f, 	//Top
								.0f, .0f, 0.0f, //Bottom Left
								200.0f, 0.0f, 0.0f 	//Bottom Right
												};
	
	/**
	 * The Triangle constructor.
	 * 
	 * Initiate the buffers.
	 */
	public TriangleGL1() {
		//
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuf.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
	}

	/**
	 * The object own drawing function.
	 * Called from the renderer to redraw this instance
	 * with possible changes in values.
	 * 
	 * @param gl - The GL context
	 */
	public void draw(GL10 gl) {
		//Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		
		//Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		
		//Enable vertex buffer
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		//Draw the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
		
		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}