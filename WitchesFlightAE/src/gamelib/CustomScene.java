package gamelib;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import com.icegeo.witchesflightae.MainActivity;

abstract public class CustomScene extends Scene implements IOnSceneTouchListener{

	protected MainActivity activity;
	protected Engine engine;
	protected boolean isStarted;
	
	public CustomScene(MainActivity activity, Engine engine){
		
		isStarted = false;
		this.activity = activity;
		this.engine = engine;
		loadResources();
		setOnSceneTouchListener(this);
	}

	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		
		return callTouchEvent(pSceneTouchEvent);
	}

	abstract public void loadResources();
	abstract public void releaseResource();
	abstract public boolean callTouchEvent(TouchEvent pSceneTouchEvent);
	abstract protected void startScene();
}
