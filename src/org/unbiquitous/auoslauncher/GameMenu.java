package org.unbiquitous.auoslauncher;


import org.unbiquitous.uImpala.engine.asset.Text;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameScene;
import org.unbiquitous.uImpala.engine.io.MouseEvent;
import org.unbiquitous.uImpala.engine.io.MouseSource;
import org.unbiquitous.uImpala.engine.io.Screen;
import org.unbiquitous.uImpala.engine.io.ScreenManager;
import org.unbiquitous.uImpala.engine.time.DeltaTime;
import org.unbiquitous.uImpala.util.math.Rectangle;
import org.unbiquitous.uImpala.util.observer.Event;
import org.unbiquitous.uImpala.util.observer.Observation;
import org.unbiquitous.uImpala.util.observer.Subject;

import android.util.Log;

public class GameMenu extends GameScene {

	Screen localScreen;
	private Text playOption;
	private MouseSource localMouse;

	public GameMenu() {
		DeltaTime deltaTime = GameComponents.get(DeltaTime.class);
		deltaTime.setUPS(60);
		
		
		localScreen = GameComponents.get(ScreenManager.class).create();
		localScreen.open("uPong", 800, 600, false, null);
//		localScreen.initGL();

		GameComponents.put(Screen.class, localScreen);
		// TODO: a default font would be nice
		// TODO: should allow to reference internal resources (not only
		// filesystem)
		
		playOption = assets.newText("ttf/default.ttf", "Play");
//		playOption = new Text(assets, "ttf/default.ttf", "Play");
		localMouse = localScreen.getMouse();
		// TODO: Observer should accept a listener
		localMouse.connect(MouseSource.EVENT_BUTTON_DOWN, new Observation(this,
				"buttonDown"));
		playOption.options(null, 60f, null);
	}

	protected void render() {
		playOption.render(localScreen, 400f, 300f, null, 1.0f, 0f);
	}

	protected void buttonDown(Event event, Subject subject) {
		MouseEvent e = (MouseEvent) event;
		if (e.isInside(new Rectangle(400, 300, playOption.getWidth(),
				playOption.getHeight(), 0))) {
			 Log.d("echo","They've called me");
			 
		}
	}

	protected void destroy() {
	}

	protected void update() {
		if (localScreen.isCloseRequested()) {
			GameComponents.get(org.unbiquitous.uImpala.engine.core.Game.class).quit();
			System.exit(0);
		}
	}

	protected void wakeup(Object... arg0) {
	}
}
