package subclasses;

import gamelib.GameLib;

import org.andengine.entity.IEntity;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import scenes.GameScene;

import com.icegeo.witchesflightae.MainActivity;

public class Neuroi extends Enemy {

	public Neuroi(final float pX, final float pY, final ITiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager){
		
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
	}
	public Neuroi(final float pX, final float pY, final ITiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, Rectangle rect, int hp, float speed){
		
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager, rect, hp, speed);
	}
	
	float playerX, playerY;
	Rectangle playerRect;
	public int process(float playerX, float playerY, Rectangle playerRect){
		
		this.playerX = playerX;
		this.playerY = playerY;
		this.playerRect = playerRect;
		
		return process();
	}
	@Override
	public int process() {
		// TODO Auto-generated method stub
		if(this.getY() > MainActivity.SCREEN_HEIGHT + 50){
			
			return REMOVE;
		}
		if(GameLib.check(playerX, playerY, playerRect, getX(), getY(), rect)){
			
			return CRASH;
		}
		return super.process();
	}
	IEntity _parent;
	@Override
	public void onAttached() {
		// TODO Auto-generated method stub
		_parent = getParent();//상위 레이어를 확보
		super.onAttached();
	}
	@Override
	public void onDetached() {
		// TODO Auto-generated method stub
		GameScene pScene = (GameScene)_parent.getParent();//레이어가 얹혀진 Scene을 확보
		
		int itemkind = 0;
		if(GameLib.RAND(1, 20)==5){
			itemkind = 1;//빅골드
			if(pScene.getLevel()>=1 && GameLib.RAND(1, 100)<10)
				itemkind = 2;//빅골드 2
			if(pScene.getLevel()>=2 && GameLib.RAND(1, 100)<30)
				itemkind = 2;//빅골드 2
			else if(pScene.getLevel()>=4 && GameLib.RAND(1, 100)<20)
				itemkind = 3;//빅골드 3
		}else if(GameLib.RAND(1, 50)==10)
			itemkind = 5;//트윈샷
		else if(GameLib.RAND(1, 50)==10)
			itemkind = 4;//마그넷
		
		pScene.setItem(itemkind, getX() + this.getWidthScaled()/2, getY() + this.getHeightScaled()/2);
		
		super.onDetached();
	}

}
