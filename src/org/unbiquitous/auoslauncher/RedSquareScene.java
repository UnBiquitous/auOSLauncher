package org.unbiquitous.auoslauncher;

import java.util.Date;

import javax.microedition.khronos.opengles.GL10;

import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameScene;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.engine.io.ScreenManager;

public class RedSquareScene extends GameScene {

	Screen localScreen;

	public RedSquareScene() {
		localScreen = GameComponents.get(Screen.class);
	}

	long count = 0;
	public void render() {
		GL10 gl = GameComponents.get(GL10.class);
		
		if (gl ==null) return;
		
		gl.glColor4f(1f, 0, 0, 1f);
		new SquareGL1().draw(gl);
		
	}
	@Override
	protected void update() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void wakeup(Object... args) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void destroy() {
		// TODO Auto-generated method stub
		
	}

}
