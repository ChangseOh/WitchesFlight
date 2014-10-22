package scenes;

import gamelib.CustomScene;
import gamelib.GameLib;

import java.util.Vector;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.ITextureAtlas;
import org.andengine.opengl.texture.atlas.ITextureAtlas.ITextureAtlasStateListener;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import subclasses.Bullet;
import subclasses.Enemy;
import subclasses.Item;
import subclasses.Neuroi;
import subclasses.NeuroiV2;
import subclasses.Rectangle;
import android.graphics.Point;
import android.view.MotionEvent;

import com.icegeo.witchesflightae.MainActivity;
import com.icegeo.witchesflightae.R;

public class GameScene extends CustomScene {
	
	final int STATUS_PLAYON = 0; 
	final int STATUS_FALL = 1; 

	boolean isLock;
	float scrollSpeed;
	int cnt;
	
	int gameLevel;//게임 진행으로 증가하는 레벨
	int getScore;//이번 게임에서 획득한 스코어
	int getGold;//이번 게임에서 획득한 골드량
	int getRange;//전진한 거리
	int regen;//적 캐릭터 생성 카운터
	int viewScore;//현재 HUD에 보여주고 있는 점수
	int viewGold;//현재 HUD에 보여주고 있는 골드

	float myX, preX;//플레이어 캐릭터 위치
	float moveX;//터치로 조작시 이동값
	int playerWidth;//플레이어 캐릭터 스프라이트(1 프레임)의 가로 폭
	int myFrame;//현재 보여주는 플레이어 캐릭터의 프레임
	int keyTime;//터치를 일정 방향으로 유지하고 있는 시간 카운터
	int status;//플레이어 캐릭터의 상태
	int infinite;//플레이어 캐릭터의 무적 상태
	boolean isTwin;//트윈샷
	boolean isMagnet;//마그넷
	int twinTime;//트윈샷 유지시간
	int magnetTime;//마그넷 유지시간
	final static int MAXTIME_TWIN = 600;
	final static int MAXTIME_MAGNET = 400;
	
	boolean isTouched;//터치중인지, 터치에서 손을 뗐는지 체크한다

	Vector<Bullet> bullets;// 총알 관리. 총알의 갯수를 예상할 수 없기 때문에 가변적으로 관리한다.
	Vector<Enemy> enemies;// 적 캐릭터 관리.
	Vector<AnimatedSprite> effects;// 이펙트 관리 //3-8.
	Vector<Item> items;//아이템 관리
	
	HUD hud;
	
	public GameScene(MainActivity activity, Engine engine){
		super(activity, engine);
		
		isLock = true;
		scrollSpeed = 5.0f;

		getScore = GameLib.filter(0);
		getGold = GameLib.filter(0);
		getRange = GameLib.filter(0);
		viewScore = 0;
		viewGold = 0;

		myFrame = 2;
		keyTime = 0;
		
		isTwin = false;
		isMagnet = false;
		infinite = 60;
		
		cnt = 0;
		
		isTouched = false;

		bullets = new Vector<Bullet>();
		enemies = new Vector<Enemy>();
		effects = new Vector<AnimatedSprite>();
		items = new Vector<Item>();
		
		bullets.clear();
		enemies.clear();
		effects.clear();
		items.clear();

		activity.scoreBuff = 0;
		activity.goldBuff = 0;
		activity.rangeBuff = 0;
		activity.isNewrecord = false;
		
	}
	
	
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	private ITextureRegion _bg_TR;//배경
	private ITextureRegion _hud1;//상단 점수판 배경
	private ITextureRegion _hud2;//하단 골드수입 배경
	private ITextureRegion _bullet;//총알 그림
	
	private TiledTextureRegion _effect_boom;//폭발 이펙트
	private TiledTextureRegion _effect_fire;//크랙 이펙트
	private TiledTextureRegion _effect_star;//별가루 이펙트

	private TiledTextureRegion _lyne;//플레이어 캐릭터
	private TiledTextureRegion _neuroi[];//네우로이
	private TiledTextureRegion _v2;//V2 네우로이

	private TiledTextureRegion _items[];//아이템
	
	private ITextureRegion _twinGuage;//트윈샷게이지 바탕
	private ITextureRegion _twinGuage2;//트윈샷게이지
	private ITextureRegion _magnetGuage;//마그넷게이지 바탕
	private ITextureRegion _magnetGuage2;//마그넷게이지
	
	private TiledSprite scoreSpr[];
	private TiledSprite goldSpr[];
	private TiledSprite rangeSpr[];
	
	private Entity _layerBullet;
	private Entity _layerEnemy;
	private Entity _layerEffect;
	
	@Override
	public void loadResources() {
		// TODO Auto-generated method stub
		setBackground(new Background( 0.9804f, 0.6274f, 0.8784f ));

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("game/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048,2048, TextureOptions.BILINEAR);
		_bg_TR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "ground.png");
		_hud1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "gameui_01.png");
		_hud2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "gameui_02.png");
		_lyne = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "lyne.png", 5,1);
		_bullet = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "mybullet"+String.format("%02d", activity.getPower()+1)+ ".png");
		
		_effect_boom = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "explode.png", 4,4); 
		_effect_fire = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "effect_fire.png", 2,1); 
		_effect_star = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "effect_star.png", 6,1); 

		_neuroi = new TiledTextureRegion[6];//네우로이
		for(int i=0;i<6;i++)
			_neuroi[i] = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, String.format("neuroi_%02d.png", i+1), 1,1);
		
		_v2 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "neuroiv2.png", 2,1); 
	
		_items = new TiledTextureRegion[6];
		_items[0] = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "coin.png", 7,1);
		_items[1] = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "jewel1.png", 1,1);
		_items[2] = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "jewel2.png", 1,1);
		_items[3] = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "jewel3.png", 1,1);
		_items[4] = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "magnet_.png", 1,1);
		_items[5] = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "twinshot.png", 7,1);

		_twinGuage = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "twinguage.png");
		_twinGuage2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "twinguage2.png");
		_magnetGuage = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "magnetguage.png");
		_magnetGuage2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "magnetguage2.png");
		
		try{
			
			gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(1, 1, 1));
			gameTextureAtlas.load();
			gameTextureAtlas.setTextureAtlasStateListener(new ITextureAtlasStateListener<IBitmapTextureAtlasSource>() {
				@Override
				public void onUnloadedFromHardware(ITexture pTexture) {
					// TODO Auto-generated method stub
				}
				@Override
				public void onLoadedToHardware(ITexture pTexture) {
					// TODO Auto-generated method stub
					startScene();
				}
				@Override
				public void onTextureAtlasSourceLoaded(
						ITextureAtlas<IBitmapTextureAtlasSource> pTextureAtlas,
						IBitmapTextureAtlasSource pTextureAtlasSource) {
					// TODO Auto-generated method stub
				}
				@Override
				public void onTextureAtlasSourceLoadExeption(
						ITextureAtlas<IBitmapTextureAtlasSource> pTextureAtlas,
						IBitmapTextureAtlasSource pTextureAtlasSource, Throwable pThrowable) {
					// TODO Auto-generated method stub
				}
			});
		}catch(Exception e){	}
		
	}

	@Override
	public void releaseResource() {
		// TODO Auto-generated method stub
		
		//scene 상의 모든 추가 처리를 중지/제거
		clearEntityModifiers();
		clearTouchAreas();
		clearUpdateHandlers();

		activity.camera.setHUD(null);//카메라에서 HUD를 삭제한다
		detachChildren();//scene에서 Entity를 제거한다
		
		//비트맵을 비사용상태로
		_bg_TR = null;//배경
		_hud1 = null;//상단 점수판 배경
		_hud2 = null;//하단 골드수입 배경
		_bullet = null;//총알 그림
		
		_effect_boom = null;//폭발 이펙트
		_effect_fire = null;//크랙 이펙트
		_effect_star = null;//별가루 이펙트

		_lyne = null;//플레이어 캐릭터
		for(int i=0;i<_neuroi.length;i++)
			_neuroi[i] = null;//네우로이
		_v2 = null;//V2 네우로이

		for(int i=0;i<_items.length;i++)
			_items[i] = null;//아이템

		_twinGuage = null;//트윈샷게이지 바탕
		_twinGuage2 = null;//트윈샷게이지
		_magnetGuage = null;//마그넷게이지 바탕
		_magnetGuage2 = null;//마그넷게이지
		
		//텍스처 언로드
		gameTextureAtlas.unload();
	}

	@Override
	public boolean callTouchEvent(
			TouchEvent event) {
		// TODO Auto-generated method stub
		
		if(isLock)
			return false;
		
		if(event.getAction()==MotionEvent.ACTION_DOWN ){

			if(status!=STATUS_PLAYON)
				return false;
			
			//myX = event.getX() - playerWidth/2;
			preX = myX;
			keyTime = 0;
			isTouched = true;
			moveX = event.getX();
		}
		if(event.getAction()==MotionEvent.ACTION_MOVE){
			
			if(status!=STATUS_PLAYON)
				return false;
			
			myX += (event.getX() - moveX);//최초의 터치점에서 이동한 만큼만 이동한다
			
			//화면을 벗어나지 않게
			if(myX<-playerWidth/2)
				myX = -playerWidth/2;
			if(myX>MainActivity.SCREEN_WIDTH-playerWidth/2)
				myX = MainActivity.SCREEN_WIDTH-playerWidth/2;

			moveX = event.getX();//터치기준점 변경
			
			if(preX>myX){//화면 왼쪽으로 움직이고 있는 중이다
				
				if (keyTime > 1 && keyTime % 7 == 0 && myFrame > 0)
					myFrame--;// 캐릭터의 왼쪽 기울어짐을 묘사한다

			}else if(preX<myX){//화면 오른쪽으로 움직이고 있는 중이다

				if (keyTime > 1 && keyTime % 7 == 0 && myFrame < 4)
					myFrame++;// 캐릭터의 오른쪽 기울어짐을 묘사한다
			}
			
			preX = myX;
			
			((TiledSprite)getChildByTag(1)).setCurrentTileIndex(myFrame);
			getChildByTag(1).setPosition(myX, getChildByTag(1).getY());

			//플레이어 위치에 맞춰 게이지 위치를 갱신 
			getChildByTag(3).setPosition(myX + playerWidth/2 - 36, 500.0f);
			getChildByTag(2).setPosition(myX + playerWidth/2 - 36, 525.0f);
		}
		
		if(event.getAction()==MotionEvent.ACTION_UP){//손을 떼면 중립으로 되돌아오도록 한다
			
			isTouched = false;

//			int _X = (int)event.getX();
//			int _Y = (int)event.getY();
			
		}
		return false;
	}
	
	void update(){
		
		
		cnt++;
		
		setRange(GameLib.filter((int)(scrollSpeed / 2.0f)) );//getRange += (int)(scrollSpeed / 2.0f);
		updateHUD();
		
		if(!myProcess())//게임오버가 되면 데이타가 릴리즈되므로 스레드 프로세스를 종료
			return;
		bulletProcess();
		enemyProcess();
		itemProcess();
		effectProcess();
	}

	boolean myProcess(){
		
		if(status==STATUS_FALL){
			
			float _myY = getChildByTag(1).getY();
			
			_myY+=3.5f;
			if(cnt%5==0)
				setEffect(0, getChildByTag(1).getX() + playerWidth/2 + GameLib.RAND(-50, 50), _myY + GameLib.RAND(-10, 30), GameLib.RAND(20, 30));
			
			if(_myY>MainActivity.SCREEN_HEIGHT + 100){

				activity.scoreBuff = GameLib.filter(getScore);
				activity.rangeBuff =GameLib.filter(getRange)/10;
				activity.goldBuff = GameLib.filter(getGold);
				
				if(activity.getScore() < GameLib.filter(getScore) + GameLib.filter(getRange)/10){
					//최고 점수 갱신
					activity.setScore(GameLib.filter(getScore) + GameLib.filter(getRange)/10);
					activity.isNewrecord = true;
				}
				activity.setGold(activity.getGold() + GameLib.filter(getGold));
				
				GameLib.SaveGame(activity, "gamedat");
				
				activity.stopMusic();
				activity.changeScene(new ResultScene(activity, engine));
				return false;
			}
			
			getChildByTag(1).setY(_myY);
			
			return true;
		}
		
		keyTime++;
		
		if (!isTouched && keyTime > 1 && keyTime % 7 == 0) {
			if (myFrame < 2)
				myFrame++;
			else if (myFrame > 2)
				myFrame--;
			// 키에서 손을 놓았으면 캐릭터를 다시 중립 상태로 되돌린다.

			((TiledSprite)getChildByTag(1)).setCurrentTileIndex(myFrame);
		}

		//무적상태일 때 점멸 처리
		if(infinite>0){
			if(cnt%4<=2)
				((TiledSprite)getChildByTag(1)).setAlpha(0.3f);
			else
				((TiledSprite)getChildByTag(1)).setAlpha(1.0f);
			infinite--;
		}else if(infinite==0){
			((TiledSprite)getChildByTag(1)).setAlpha(1.0f);
		}
		
		//마그넷, 트윈샷 타임 처리
		if(isMagnet){
			if(magnetTime--==0){
				isMagnet = false;
				
				//게이지를 감춘다
				getChildByTag(3).setVisible(false);
			}else{
				//내부 게이지의 길이를 줄인다
				getChildByTag(3).getChildByTag(1).setScaleX((float)magnetTime  / (float)MAXTIME_MAGNET);
				Sprite _spr = (Sprite)(getChildByTag(3).getChildByTag(1));
				_spr.setX(2 + (_spr.getWidthScaled() - 68)/2);
			}
		}
		if(isTwin){
			if(twinTime--==0){
				isTwin = false;
				
				//게이지를 감춘다
				getChildByTag(2).setVisible(false);
			}else{
				//내부 게이지의 길이를 줄인다
				getChildByTag(2).getChildByTag(1).setScaleX((float)twinTime / (float)MAXTIME_TWIN);
				Sprite _spr = (Sprite)(getChildByTag(2).getChildByTag(1));
				_spr.setX(2 + (_spr.getWidthScaled() - 68)/2);
			}
		}
		
		if(cnt%7==0){
			activity.gamesound[3].play();
			if(isTwin){
				fireBullet(myX - 20 + playerWidth/2, 560);
				fireBullet(myX + 20 + playerWidth/2, 560);
			}else
				fireBullet(myX + playerWidth/2, 560);
		}
		
		
		return true;
	}
	void enemyProcess() {

		// V2로켓을 생성합니다. 파괴가 불가능할 정도로 HP가 높고 덩치가 큽니다. 단, 본체 등장 전에는 잠시 경고 표지가 나옵니다 
		if(cnt > 200 && GameLib.RAND(0,1000) < 5*gameLevel){//어느 정도는 레벨이 높아질수록 나오기 쉬워지는 편

			//System.out.println("V2네우로이 생성");
			NeuroiV2 _newV2 = new NeuroiV2(GameLib.RAND(-12,MainActivity.SCREEN_WIDTH+12-125), -360, _v2, engine.getVertexBufferObjectManager(), new Rectangle(39,49, 44,258), 10000000, (6 + scrollSpeed)*1.5f);
			_layerEnemy.attachChild(_newV2);
			enemies.add(_newV2);
			_newV2.setActivity(activity);
		}
		
		// 네우로이를 생성합니다. 한꺼번에 일렬로 5대를 생성합니다.
		if (cnt > 100 && (cnt % 90 == 0 || (GameLib.RAND(0, 10) == 5 && cnt % 45 == 0))) {
			 //기본적으로는 일정 시간마다 생성하지만, 게임에 변화를 주기 위해 10% 확률로 그 반 간격으로도 생성합니다.

			int localLevel = (regen%20) / 5;
			
			int neuroiLevel[] = { 0, 0, 0, 0, 0 };
			int setCnt = 0;
			while (setCnt < localLevel) {
				int idx = GameLib.RAND(0, 4);
				if (neuroiLevel[idx] == 0) {
					neuroiLevel[idx] = 1;
					setCnt++;
				}
			}
			
			for (int i = 0; i < 5; i++) {

				int imsiLevel = gameLevel + neuroiLevel[i] + (1<=gameLevel&&gameLevel<=3&&GameLib.RAND(1,20)==5&&neuroiLevel[i]==1?1:0);
				if(imsiLevel>5)
					imsiLevel = 5;

				Neuroi _enemy = new Neuroi(i * 96 - 23, -80, _neuroi[imsiLevel], engine.getVertexBufferObjectManager(), new Rectangle(33, 6, 76, 81), 6 * (imsiLevel*2), 6 + scrollSpeed);
				_layerEnemy.attachChild(_enemy);
				enemies.add(_enemy);
			}
			regen++;
			
			if(regen%20==0)
				levelup();
			
		}

		Enemy _buff;
		for (int i = enemies.size()-1; i >=0; i--) {
			_buff = enemies.elementAt(i);
			switch(_buff.process(myX, 550, new Rectangle(12, 20, 55, 50))){
			case Enemy.REMOVE:
				_layerEnemy.detachChild(_buff);
				enemies.remove(_buff);
				break;
			case Enemy.CRASH:
				if(status==STATUS_PLAYON && infinite==0){
					
					status = STATUS_FALL;//3-7.(1)
					isTwin = false;
					isMagnet = false;
				}
				break;
			}
			if(_buff.getY() > MainActivity.SCREEN_HEIGHT+30){
				continue;
			}
		}

	}
	
	void bulletProcess(){
		
		Bullet _buff;
		for (int i = bullets.size()-1; i >=0 ; i--) {
			_buff = bullets.elementAt(i);
			
			int _checker = _buff.checkHit(enemies); 
			switch(_checker){
			case -1://아무것도 안 맞았음
				break;
			default://뭔가 맞았음
				setEffect(2, _buff.getX() + _buff.getWidthScaled()/2, _buff.getY());
				setScore(GameLib.filter(1 + gameLevel));//getScore += (1 + gameLevel);
				
				Enemy _enemyTemp = enemies.elementAt(_checker);
				if(_enemyTemp.setDamage(_buff.getPower())){//에너지가 0이 되어 파괴되는가 확인
					for(int j=0;j<3;j++)
						setEffect(0, _enemyTemp.getX()+_enemyTemp.getWidth()/2+GameLib.RAND(-20,20), _enemyTemp.getY()+_enemyTemp.getHeight()/2+GameLib.RAND(-20,20), GameLib.RAND(10, 50));
					
					setScore(GameLib.filter(50 + gameLevel * 10));//getScore += (50 + gameLevel * 10);
					
					//파괴된 적을 제거
					_layerEnemy.detachChild(_enemyTemp);
					enemies.remove(_enemyTemp);
					
					activity.gamesound[1].play();
				}
				_layerBullet.detachChild(_buff);
				bullets.remove(_buff);
				break;
			}
			
			if(_buff.getY()<-20){//화면 상단 밖으로 벗어났다면
				_layerBullet.detachChild(_buff);
				bullets.remove(_buff);
			}
		}
	}
	
	void itemProcess(){
		
		Item _buff;
		for(int i=items.size()-1; i>=0;i--){
			_buff = items.elementAt(i);
			
			if(status == STATUS_PLAYON && GameLib.check(myX, 550, new Rectangle(12, 20, 55, 50), _buff.getX(), _buff.getY(), _buff.getRect())){
				int effectEa = 1;
				//플레이어와 충돌했는가
				switch(_buff.getKind()){
				case 0://코인
				case 1://빅골드1
				case 2://빅골드2
				case 3://빅골드3
					effectEa += _buff.getKind();
					setGold(GameLib.filter(_buff.getKind() * 10 + 1));//getGold += (_buff.getKind() * 10 + 1);
					break;
				case 4://마그넷
					effectEa = 4;
					isMagnet = true;
					magnetTime = MAXTIME_MAGNET;
					//게이지를 보인다
					getChildByTag(3).setVisible(true);
					getChildByTag(3).getChildByTag(1).setPosition(2, 14);
					break;
				case 5://트윈샷
					effectEa = 3;
					isTwin = true;
					twinTime = MAXTIME_TWIN; 
					//게이지를 보인다
					getChildByTag(2).setVisible(true);
					getChildByTag(2).getChildByTag(1).setPosition(2, 14);
					break;
				}
				
				//습득 이펙트
				float myX = getChildByTag(1).getX();
				float myY = getChildByTag(1).getY() + 30;
				for(int j=0;j<effectEa;j++)
					setEffect(3, myX + playerWidth/2 + GameLib.RAND(-10, 10), myY + GameLib.RAND(-10, 10), 50);
				
				//제거
				detachChild(_buff);
				items.remove(_buff);
				
				activity.gamesound[0].play();
			}
			
			if(isMagnet && true){
				//마그넷 상태에서 일정 거리 이내에 있다면 빨아들인다
				if(GameLib.getRange(_buff.getX(), _buff.getY(), myX, 580)<=30000){
					_buff.setTracked();
				}
			}

			
			if(_buff.getY()>MainActivity.SCREEN_HEIGHT + 30){//화면 아래로 사라졌으면 제거
				detachChild(_buff);
				items.remove(_buff);
			}
		}
	}

	void effectProcess(){
		
		AnimatedSprite _buff;
		for (int i = effects.size()-1; i >=0 ; i--) {
			_buff = effects.elementAt(i);
			
			if(!_buff.isAnimationRunning()){//애니메이션이 종료되었다면
				_layerEffect.detachChild(_buff);
				effects.remove(_buff);
			}
		}
	}
	
	void fireBullet(float x, float y){
		
		int powRange = activity.getPower()/6;//총알 레벨에 따라 영향받는 범위도 넓혀준다

		Bullet _bulletSpr = new Bullet( x - _bullet.getWidth()/2, y, _bullet, engine.getVertexBufferObjectManager(), new Rectangle(5-powRange, 1, 10+powRange*2, 33), 1+activity.getPower());
		_layerBullet.attachChild(_bulletSpr);
		bullets.add(_bulletSpr);
		setEffect(1, x+GameLib.RAND(-5, 5), y+GameLib.RAND(-2, 2));
		
	}
	
	void setEffect(int kind, float x, float y){

		setEffect(kind, x, y, 100);
	}
	void setEffect(int kind, float x, float y, long frameTime){
		
		AnimatedSprite _aniEffect = null;
		switch(kind){
		case 0:
			_aniEffect = new AnimatedSprite(x, y, _effect_boom, engine.getVertexBufferObjectManager());
			break;
		case 1:
			_aniEffect = new AnimatedSprite(x, y, _effect_fire, engine.getVertexBufferObjectManager());
			_aniEffect.setScale(0.7f);
			_aniEffect.setAlpha(0.6f);
			break;
		case 2:
			_aniEffect = new AnimatedSprite(x, y, _effect_fire, engine.getVertexBufferObjectManager());
			break;
		case 3:
			_aniEffect = new AnimatedSprite(x, y, _effect_star, engine.getVertexBufferObjectManager());
			break;
		}
		if(_aniEffect!=null){
			_aniEffect.setPosition(x - _aniEffect.getWidthScaled()/2, y - _aniEffect.getHeightScaled()/2);
			_aniEffect.animate(frameTime, false);
			_layerEffect.attachChild(_aniEffect);
			effects.add(_aniEffect);
		}
	}
	
	public void setItem(int kind, float x, float y){
		
		Item _newitem = new Item(x, y, _items[kind], activity.getVertexBufferObjectManager(), new Rectangle(0,0, 72,72), kind);
		attachChild(_newitem);
		items.add(_newitem);
		if(kind==2)
			_newitem.setScale(1.1f);
		if(kind==3)
			_newitem.setScale(1.2f);
	}
	
	void levelup(){
		
		if (gameLevel < 5)
			gameLevel++;// 레벨은 5까지

		if (scrollSpeed < 30)
			scrollSpeed += 2;
	}
	
	public int getLevel(){
		
		return gameLevel;
	}
	
	public void updateHUD(){
		
		GameLib.setNum(rangeSpr, 464, 39, 6, GameLib.filter(getRange)/10, true, 2);
		
		if(viewScore!=GameLib.filter(getScore)){
			int dec = String.valueOf(Math.abs(viewScore-GameLib.filter(getScore))).length();//갭을 문자열로 환산하면 자릿수를 얻을 수 있다.
			if(viewScore>GameLib.filter(getScore))
				viewScore-=Math.pow(10, dec-2<0?0:dec-2);
			else
				viewScore+=Math.pow(10, dec-2<0?0:dec-2);
			GameLib.setNum(scoreSpr, 197, 39, 8, viewScore, true, 2);
		}

		if(viewGold!=GameLib.filter(getGold)){
			int dec = String.valueOf(Math.abs(viewGold-GameLib.filter(getGold))).length();
			if(viewGold>GameLib.filter(getGold))
				viewGold-=Math.pow(10, dec-2<0?0:dec-2);
			else
				viewGold+=Math.pow(10, dec-2<0?0:dec-2);
			GameLib.setNum(goldSpr, 147, 766, 6, viewGold, false, 2);
		}
	}
	
	void setScore(int value){
		
		getScore = GameLib.filter( GameLib.filter(getScore) + GameLib.filter(value) );
	}
	
	void setRange(int range){
		
		getRange = GameLib.filter( GameLib.filter(getRange) + GameLib.filter(range) );
	}

	void setGold(int gold){
		
		getGold = GameLib.filter( GameLib.filter(getGold) + GameLib.filter(gold) );
	}
	
	public Point getPlayer(){
		
		return new Point((int)getChildByTag(1).getX(), (int)getChildByTag(1).getY());
	}
	
	@Override
	protected void startScene(){
		
		activity.playMusic(R.raw.bgm509, true);

		if(isStarted)
			return;
					
		isStarted = true;
		
		Sprite _spr = new Sprite(0,0, _bg_TR, engine.getVertexBufferObjectManager()){
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub
				setY(getY() + scrollSpeed);
				if(getY()>MainActivity.SCREEN_HEIGHT)
					setY( -MainActivity.SCREEN_HEIGHT + getY()%MainActivity.SCREEN_HEIGHT );
				
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		Sprite _spr2 = new Sprite(0,-MainActivity.SCREEN_HEIGHT, _bg_TR, engine.getVertexBufferObjectManager()){
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub
				setY(getY() + scrollSpeed);
				if(getY()>MainActivity.SCREEN_HEIGHT){
					setY( -MainActivity.SCREEN_HEIGHT + getY()%MainActivity.SCREEN_HEIGHT );
				}
				
				super.onManagedUpdate(pSecondsElapsed);
			}
		};

		
		TiledSprite _lyneSpr = new TiledSprite(0,0, _lyne, activity.getVertexBufferObjectManager());
		_lyneSpr.setTag(1);
		_lyneSpr.setCurrentTileIndex(2);

		playerWidth = (int)_lyneSpr.getWidth();
		myX = (MainActivity.SCREEN_WIDTH - playerWidth)/2;
		preX = myX;

		_lyneSpr.setPosition( myX, 550);
		
		//레이어(?) 선언
		//안드엔진 초기에는 레이어 Entity가 별도로 존재했지만 쓰이지 않게 되었다. 그 대신 레이어 개념을 사용하려면 Entity 객체를 레이어처럼 쓰면 된다.
		//레이어를 쓰는 이유는, 총알이나 적 캐릭터 등은 수시로 생성과 제거를 반복하게 되는데, 새로 생성되는 오브젝트가 맨 위로 표시되기 때문에
		//그대로 올리면 이펙트보다 위에 총알이나 적 캐릭터가 표시될 수 있기 때문이다.
		_layerBullet = new Entity();
		_layerEnemy = new Entity();
		_layerEffect = new Entity();

		
		hud = new HUD();
		
		Sprite _hud1Spr = new Sprite(6, 14, _hud1, activity.getVertexBufferObjectManager());
		Sprite _hud2Spr = new Sprite(6, 741, _hud2, activity.getVertexBufferObjectManager());
		hud.attachChild(_hud1Spr);
		hud.attachChild(_hud2Spr);
		
		scoreSpr = new TiledSprite[8];
		for(int i=0;i<8;i++){
			scoreSpr[i] = new TiledSprite(0,0, activity._num_TR, engine.getVertexBufferObjectManager());
			scoreSpr[i].setCurrentTileIndex(0);
			hud.attachChild(scoreSpr[i]);
		}
		
		goldSpr = new TiledSprite[6];
		for(int i=0;i<6;i++){
			goldSpr[i] = new TiledSprite(0,0, activity._num_TR, engine.getVertexBufferObjectManager());
			goldSpr[i].setCurrentTileIndex(0);
			hud.attachChild(goldSpr[i]);
		}
		
		rangeSpr = new TiledSprite[6];
		for(int i=0;i<6;i++){
			rangeSpr[i] = new TiledSprite(0,0, activity._num_TR, engine.getVertexBufferObjectManager());
			rangeSpr[i].setCurrentTileIndex(0);
			hud.attachChild(rangeSpr[i]);
		}
		
		GameLib.setNum(scoreSpr, 197, 39, 8, 0, true, 2);
		GameLib.setNum(rangeSpr, 464, 39, 6, 0, true, 2);
		GameLib.setNum(goldSpr, 147, 766, 6, 0, false, 2);
		
		Sprite _twinG1 = new Sprite(0,0, _twinGuage, engine.getVertexBufferObjectManager());
		Sprite _twinG2 = new Sprite(0,0, _twinGuage2, engine.getVertexBufferObjectManager());
		Sprite _magnetG1 = new Sprite(0,0, _magnetGuage, engine.getVertexBufferObjectManager());
		Sprite _magnetG2 = new Sprite(0,0, _magnetGuage2, engine.getVertexBufferObjectManager());

		_twinG1.setVisible(false);
		_magnetG1.setVisible(false);
		_twinG1.setTag(2);
		_magnetG1.setTag(3);
		_twinG2.setTag(1);
		_magnetG2.setTag(1);
		_twinG1.attachChild(_twinG2);
		_magnetG1.attachChild(_magnetG2);
		_twinG2.setPosition(2, 14);
		_magnetG2.setPosition(2, 14);

		//생성한 Entity를 차례대로 올린다.
		//말하자면 dblpaint에서 그려주던 순서가 이것과 같은 셈 
		attachChild(_spr);
		attachChild(_spr2);
		attachChild(_layerBullet);
		attachChild(_lyneSpr);
		attachChild(_layerEnemy);
		attachChild(_layerEffect);

		attachChild(_twinG1);
		attachChild(_magnetG1);
		
		//HUD를 등록한다. HUD는 Scene이 아니라 Camera에 붙는다. Camera가 비추는 범위를 직접 억세스하여 변화시키더라도 HUD는 영향받지 않는다
		activity.camera.setHUD(hud);
		
		registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub
				
				update();
				
			}
		});
		
		isLock = false;
	}
}
