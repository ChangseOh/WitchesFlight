package subclasses;

import gamelib.GameLib;

import java.util.Vector;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Bullet extends Sprite{

	private Rectangle rect;//총알의 충돌체크 대상이 되는 사각형
	private int power;//총알의 파괴력
	
	public Bullet(final float pX, final float pY, final ITextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager){
		
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
	}
	public Bullet(final float pX, final float pY, final ITextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, Rectangle rect, int power){
		
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		this.rect = rect;
		this.power = power;
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		// TODO Auto-generated method stub
		
		setY( getY()-16 );
		
		super.onManagedUpdate(pSecondsElapsed);
	}
	
	public int checkHit(Vector<Enemy> enemies){
		
		for(int i=enemies.size()-1; i>=0; i--){
			Enemy _buff = enemies.elementAt(i);
			
			if(GameLib.check(this.getX(), this.getY(), this.rect, _buff.getX(), _buff.getY(), _buff.getRect())){
				return i;
			}
		}
		
		return -1;
	}
	
	public int getPower(){
		
		return power;
	}
}
