package subclasses;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Enemy extends AnimatedSprite {

	protected Rectangle rect;//충돌체크 대상이 되는 사각형
	protected int hp;//내구			//3-7.(2)
	protected float speed;//아래로 내려오는 속도
	protected int cnt;//적 캐릭터 처리 카운터 20131211
	
	static public final int GREEN = 0;
	static public final int REMOVE = 1;
	static public final int CRASH = 2;

	public Enemy(final float pX, final float pY, final ITiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager){
		
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		cnt = 0;
	}
	
	public Enemy(final float pX, final float pY, final ITiledTextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, Rectangle rect, int hp, float speed){

		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		this.rect = rect;
		this.hp = hp;
		this.speed = speed;
		
		cnt = 0;
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		// TODO Auto-generated method stub
		
		setY( getY()+speed );
		super.onManagedUpdate(pSecondsElapsed);
	}
	
	public Rectangle getRect(){
		
		return rect;
	}
	
	public int process(){
		
		return GREEN;
	}
	public int process(float playerX, float playerY, Rectangle playerRect){
		
		return GREEN;
	}
	public boolean setDamage(int damage){
		
		hp-=damage;
		if(hp<0)
			hp = 0;
		
		return (hp==0);
	}
}
