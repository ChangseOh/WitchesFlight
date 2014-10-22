package gamelib;

import java.util.Random;

import org.andengine.entity.sprite.TiledSprite;

import subclasses.Rectangle;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.icegeo.witchesflightae.MainActivity;

public class GameLib {

	static Random rnd;
	private GameLib(){
		rnd = new Random();
	}
	
	//숫자 스프라이트 정렬하기
	public static void setNum(TiledSprite _spr[], float x, float y, int digit, int value, boolean zero, int align){
		
		float _x, _add;
		
		for(int i=0;i<_spr.length;i++)
			if(_spr[i]==null)
				return;

		_add = _spr[0].getWidthScaled();//숫자 한 글자마다의 폭을 얻는다
		
		if(digit>_spr.length)
			digit = _spr.length;//자리수가 할당된 스프라이트 갯수보다 많으면 조정한다
		String valueStr = String.valueOf(value);
		if(valueStr.length()<digit && zero)
			valueStr = "0000000000".substring(0, digit-valueStr.length()) + valueStr;//빈 자리수에 0을 채워넣는다
		
		//ALIGN 값에 따라 숫자 표시 위치를 조정한다
		if(align==0){//ALIGN_LEFT
			_x = x;
		}else if(align==1){//ALIGN_CENTER
			_x = x- (valueStr.length() * _add)/2.0f; 
		}else{//ALIGN_RIGHT
			_x = x- (valueStr.length() * _add); 
		}
		
		for(int i=0;i<_spr.length;i++)
			_spr[i].setVisible(false);
		
		//문자열로 변환된 숫자를 스캔해, 순서대로 0~9 값으로 변환, 해당하는 숫자를 보이도록 한다 
		for(int i=0;i<digit;i++){
			if(i>=digit-valueStr.length()){
				_spr[i].setPosition(_x, y);
				_spr[i].setVisible(true);
				_spr[i].setCurrentTileIndex(valueStr.charAt(i-digit+valueStr.length())-'0');
				_x += _add;
			}
		}
	}
	
	static public boolean check(int x1, int y1, Rectangle rect1, int x2, int y2, Rectangle rect2){
		
		boolean ret = false;

		if(rect1==null)
			return false;
		
		if(rect2==null)
			return false;

		Rectangle _rect1 = new Rectangle(x1+rect1.x, y1+rect1.y, rect1.width, rect1.height);
		Rectangle _rect2 = new Rectangle(x2+rect2.x, y2+rect2.y, rect2.width, rect2.height);
		
		if(
		_rect1.x < (_rect2.x+_rect2.width) &&
		_rect2.x < (_rect1.x+_rect1.width) &&
		_rect1.y < (_rect2.y+_rect2.height) &&
		_rect2.y < (_rect1.y+_rect1.height)
				)
			ret = true;
		
		return ret;
	}

	static public int RAND(int startnum, int endnum){
		
		//startnum ~ endnum 사이의 정수 난수를 생성한다
		
		if(rnd==null)
			rnd = new Random();
		
		int a, b;
		if( startnum < endnum )
			b = endnum - startnum;
		else
			b = startnum - endnum;

		a = Math.abs(rnd.nextInt()%(b+1));

		return (a+startnum);
	}

	static public boolean check(float x1, float y1, Rectangle rect1, float x2, float y2, Rectangle rect2){
		
		boolean ret = false;

		if(rect1==null)
			return false;
		
		if(rect2==null)
			return false;

		Rectangle _rect1 = new Rectangle(x1+rect1.x, y1+rect1.y, rect1.width, rect1.height);
		Rectangle _rect2 = new Rectangle(x2+rect2.x, y2+rect2.y, rect2.width, rect2.height);
		
		if(
		_rect1.x < (_rect2.x+_rect2.width) &&
		_rect2.x < (_rect1.x+_rect1.width) &&
		_rect1.y < (_rect2.y+_rect2.height) &&
		_rect2.y < (_rect1.y+_rect1.height)
				)
			ret = true;
		
		return ret;
	}
	
	static public int filter(int raw){
		
		String _codekey = "로또1등되게해주세요"; 
		byte codeKey[] = _codekey.getBytes(); 
		
		byte temp[] = new byte[4];
		temp[0] = (byte)(raw>>24);
		temp[1] = (byte)(raw>>16);
		temp[2] = (byte)(raw>>8);
		temp[3] = (byte)(raw);

		for(int i=0;i<4;i++)
			temp[i]	^= codeKey[i%codeKey.length];

		return (
				((temp[0]&0xff)<<24)+
				((temp[1]&0xff)<<16)+
				((temp[2]&0xff)<<8)+
				(temp[3]&0xff)
				);
	}

	static public float getRange(float x1, float y1, float x2, float y2){
		
		return Math.abs((y2-y1)*(y2-y1)+(x2-x1)*(x2-x1));
	}

	//게임 데이타 저장/불러오기 
	static public boolean LoadGame(MainActivity mContext, String fname){

		SharedPreferences _share = mContext.getSharedPreferences(fname, Context.MODE_PRIVATE);
		
		try{
			mContext.setScore(GameLib.filter(_share.getInt("highscore", GameLib.filter(1000))));
			mContext.setGold(GameLib.filter(_share.getInt("gold", GameLib.filter(0))));
			mContext.setPower(GameLib.filter(_share.getInt("power", GameLib.filter(0))));
		}catch(Exception e){
			mContext.setScore(1000);
			mContext.setGold(0);
			mContext.setPower(0);
			return false;
		}
		
		return true;
	}

	static public boolean SaveGame(MainActivity mContext, String fname){
		
		try{
			SharedPreferences _share = mContext.getSharedPreferences(fname, Context.MODE_PRIVATE);
			Editor _edit = _share.edit();
			
			_edit.clear();
			_edit.putInt("highscore", GameLib.filter(mContext.getScore()));
			_edit.putInt("gold", GameLib.filter(mContext.getGold()));
			_edit.putInt("power", GameLib.filter(mContext.getPower()));
			_edit.commit();
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
}
