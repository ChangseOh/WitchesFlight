package subclasses;

import gamelib.GameLib;

import org.andengine.entity.IEntity;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import scenes.GameScene;

import com.icegeo.witchesflightae.MainActivity;

public class NeuroiV2 extends Enemy {

	public NeuroiV2(final float pX, final float pY, final ITiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager){
		
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
	}
	
	public NeuroiV2(final float pX, final float pY, final ITiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, Rectangle rect, int hp, float speed){
		
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager, rect, hp, speed);
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		// TODO Auto-generated method stub
		
		cnt++;
		
		if(cnt==5)
			activity.gamesound[7].play();
		
		if(cnt<150){//자체 카운트가 150에 도달하기 전까지는 경고만 나오므로 이동이 없다
			this.setCurrentTileIndex(0);
			this.setY(30);
			if(cnt%4<=2)
				this.setAlpha(0.2f);
			else
				this.setAlpha(1.0f);
			return;//경고만 보여지고 있는 동안에는 이동도 충돌도 하지 않는다
		}else if(cnt==150){
			this.setCurrentTileIndex(1);
			this.setY(-360);
			this.setAlpha(1.0f);
			activity.gamesound[6].play();
		}
		
		super.onManagedUpdate(pSecondsElapsed);
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
		if(cnt>=150 && GameLib.check(playerX, playerY, playerRect, getX(), getY(), rect)){
			
			return CRASH;
		}
		return super.process();
	}
	public Rectangle getRect(){
		
		if(cnt<150)
			return null;
		
		return rect;
	}
	MainActivity activity;
	public void setActivity(MainActivity activity){
		this.activity = activity;
	}
}
