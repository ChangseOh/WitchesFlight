package subclasses;

import java.util.Random;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import scenes.GameScene;
import android.graphics.Point;

import com.icegeo.witchesflightae.MainActivity;

public class Item extends AnimatedSprite {

	static public final int GREEN = 0;
	
	protected Rectangle rect;//충돌체크 대상이 되는 사각형
	protected int cnt;//처리 카운터
	
	private int kind;
	private PhysicsHandler _ph;
	protected float velX, velY;
	
	private Random rnd;
	private boolean isTracked;
	
	public void _Item(final float pX, final float pY, final ITiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, int kind){
		
		rnd = new Random();
		
		if(pX<MainActivity.SCREEN_WIDTH/3)
			velX = rnd.nextFloat()*150.0f;
		else if(pX<MainActivity.SCREEN_WIDTH/2)
			velX = rnd.nextFloat()*75.0f;
		else if(pX>MainActivity.SCREEN_WIDTH*2/3)
			velX = -rnd.nextFloat()*150.0f;
		else
			velX = -rnd.nextFloat()*75.0f;
		
		this.kind = kind;
		_ph = new PhysicsHandler(this);
		this.registerUpdateHandler(_ph);
		this._ph.setVelocity(velX, -pY);
		this._ph.setAccelerationY(1000.0f);
		cnt = 0;
		
		isTracked = false;
	}
	
	public Item(final float pX, final float pY, final ITiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, Rectangle rect, int kind){
		
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		
		this.rect = rect;
		_Item(pX, pY, pTextureRegion, pVertexBufferObjectManager, kind);
		
		if(this.getTileCount()==1){//프레임이 하나뿐이라면 자체적으로 회전을 준다
			if(this._ph.getVelocityX()<0)//등장 위치에 따라 회전 방향을 다르게 준다
				this._ph.setAngularVelocity(-360.0f);
			else
				this._ph.setAngularVelocity(360.0f);
		}else//아니라면 무한반복 애니메이션을 준다
			this.animate(80, true);
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		// TODO Auto-generated method stub

		//좌우 벽에 부딪히면 튕긴다
		if(!isTracked){
			if(this.getX()<=0){
				this.setX(0);
				this._ph.setVelocityX(-this._ph.getVelocityX());
			}else if(this.getX()>MainActivity.SCREEN_WIDTH-32){
				this.setX(MainActivity.SCREEN_WIDTH-32);
				this._ph.setVelocityX(-this._ph.getVelocityX());
			}
		}else{//마그넷에 끌려들어가고 있는 중
			GameScene _temp = (GameScene)getParent();
			Point _point = _temp.getPlayer();

			float trackingX = (this.getX() - _point.x) / 5.0f;
			float trackingY = (this.getY() - _point.y) / 5.0f;
			
			this.setPosition( this.getX() - trackingX, this.getY() - trackingY);
		}
		
		super.onManagedUpdate(pSecondsElapsed);
	}

	public Rectangle getRect(){
		
		return rect;
	}
	public int getKind(){
		
		return kind;
	}
	
	public void setTracked(){

		//한 번 마그넷에 끌려들어가는 상태로 전환되면 다시 바뀌지 않는다
		isTracked = true;
		
		//자율이동 정보를 소실시킨다
		this._ph.setVelocity(0, 0);
		this._ph.setAcceleration(0);
	}
}
