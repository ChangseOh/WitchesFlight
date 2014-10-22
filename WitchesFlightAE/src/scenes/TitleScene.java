package scenes;

import gamelib.CustomScene;
import gamelib.GameLib;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
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
import org.andengine.util.modifier.IModifier;

import com.icegeo.witchesflightae.MainActivity;
import com.icegeo.witchesflightae.R;

public class TitleScene extends CustomScene {

	public TitleScene(MainActivity activity, Engine engine){
		
		super(activity, engine);
		
		isLock = true;

		setBackground(new Background( 0.9804f, 0.6274f, 0.8784f ));
		
		viewGold = activity.getGold();
	}
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	public ITextureRegion _bg_TR;//배경
	public ITextureRegion _title_TR;//타이틀
	public ITextureRegion _chr_TR;//리네
	public ITextureRegion _high_TR;//최고점수
	public ITextureRegion _gold_TR;//골드
	public ITextureRegion _any_TR;//아무키
	public ITextureRegion _all_TR;//전체이용가
	public ITextureRegion _power_TR;//파워업
	
	public TiledSprite goldSpr[];
	public TiledSprite nowSpr[];
	public TiledSprite nextSpr[];
	public TiledSprite priceSpr[];
	
	int viewGold;
	int minusValue;
	int nextPowGold;
	
	boolean isLock;
	float scrollSpeed = 3.0f;
	
	@Override
	public void loadResources() {
		// TODO Auto-generated method stub

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("title/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048,2048, TextureOptions.BILINEAR);
		_bg_TR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "ground_title.png");
		_title_TR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "logo.png");//타이틀
		_chr_TR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "lyne_title.png");//리네
		_high_TR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "high.png");//최고점수
		_gold_TR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "gold.png");//골드
		_any_TR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "pushany.png");//아무키
		_all_TR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "all_grd.png");//전연령
		_power_TR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "powerup.png");//파워업
		
		
		try{
			
			gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(1, 1, 1));
			gameTextureAtlas.load();
			gameTextureAtlas.setTextureAtlasStateListener(new ITextureAtlasStateListener<IBitmapTextureAtlasSource>() {
				@Override
				public void onUnloadedFromHardware(ITexture pTexture) {
					// TODO Auto-generated method stub
					//unload가 끝나면 불린다
				}
				@Override
				public void onLoadedToHardware(ITexture pTexture) {
					// TODO Auto-generated method stub
					//모든 로드가 끝나면 불린다
					startScene();
				}
				@Override
				public void onTextureAtlasSourceLoaded(
						ITextureAtlas<IBitmapTextureAtlasSource> pTextureAtlas,
						IBitmapTextureAtlasSource pTextureAtlasSource) {
					// TODO Auto-generated method stub
					//비트맵 하나를 로드할 때마다 불린다
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
		
		//scene 상의 모든 Entity 제거
		detachChildren();
		
		//ITextureRegion 제거
		_bg_TR = null;//배경
		_title_TR = null;//타이틀
		_chr_TR = null;//리네
		_high_TR = null;//최고점수
		_gold_TR = null;//골드
		_any_TR = null;//아무키
		_power_TR = null;//파워업

		//텍스처 언로드
		gameTextureAtlas.unload();
	}

	@Override
	public boolean callTouchEvent(
			TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		if(pSceneTouchEvent.getAction()==TouchEvent.ACTION_UP){
			
			float _x = pSceneTouchEvent.getX();
			float _y = pSceneTouchEvent.getY();
			Sprite _spr = (Sprite)getChildByTag(1);
			if(_spr.getX()<=_x && _x<=_spr.getX() + _spr.getWidthScaled() &&
				_spr.getY()<=_y && _y<=_spr.getY() + _spr.getHeightScaled())
				return false;

			activity.gamesound[3].play();
			activity.stopMusic();
			activity.changeScene(new GameScene(activity, engine));
		}
		return false;
	}
	
	@Override
	protected void startScene(){
		
		//activity.bgm[0].play();
		activity.playMusic(R.raw.bgm654, true);
		
		if(isStarted)
			return;
		
		isStarted = true;
		
//		AutoParallaxBackground _bgPara1 = new AutoParallaxBackground(0,0,0,15);
//		_bgPara1.attachParallaxEntity(new ParallaxEntity(2.0f,
//				new Sprite(0,0, _bg_TR, engine.getVertexBufferObjectManager())
//				));
//		setBackground(_bgPara1);
		
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

		Sprite _lyneSpr = new Sprite(30,900, _chr_TR, engine.getVertexBufferObjectManager());
		Sprite _logoSpr = new Sprite(30,100, _title_TR, engine.getVertexBufferObjectManager());
		Sprite _highSpr = new Sprite(75, 536, _high_TR, engine.getVertexBufferObjectManager());
		Sprite _goldSpr = new Sprite(75, 607, _gold_TR, engine.getVertexBufferObjectManager());
		Sprite _anySpr = new Sprite(109.5f, 485, _any_TR, engine.getVertexBufferObjectManager());
		Sprite _allSpr = new Sprite(MainActivity.SCREEN_WIDTH-_all_TR.getWidth()-8, 8, _all_TR, engine.getVertexBufferObjectManager());
		Sprite _powerSpr = new Sprite(19, 674, _power_TR, engine.getVertexBufferObjectManager()){

			@Override
			public boolean onAreaTouched(
					TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// TODO Auto-generated method stub
				
				if(pSceneTouchEvent.getAction()!=TouchEvent.ACTION_UP)
					return false;
				
				if(viewGold!=activity.getGold()){
					activity.gamesound[1].play();
					return false;
				}
				
				if(activity.getGold()>=nextPowGold && activity.getPower()<29){
					
					activity.gamesound[4].play();

					activity.setGold(activity.getGold() - nextPowGold);
					activity.setPower(activity.getPower()+1);
					
					nextPowGold = (activity.getPower()/4)*500+(activity.getPower()+1)*100;//파워업 비용 계산
					if(activity.getPower()==29)
						nextPowGold = 0;
					
					GameLib.SaveGame(activity, "gamedat");
					
					//숫자 스프라이트 정렬
					GameLib.setNum(nowSpr, 142, 693, 2, activity.getPower()+1, true, 0);//현재 파워레벨 정렬
					GameLib.setNum(nextSpr, 218, 724, 2, activity.getPower()==29?30:activity.getPower()+2, true, 0);//다음 파워레벨 정렬
					GameLib.setNum(priceSpr, 216, 761, 5, nextPowGold, false, 2);//파워업에 필요한 비용 정렬
				}
				
				return super
						.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		_powerSpr.setTag(1);

		_logoSpr.setAlpha(0);
		
		attachChild(_spr);
		attachChild(_spr2);
		attachChild(_lyneSpr);
		attachChild(_logoSpr);
		attachChild(_highSpr);
		attachChild(_goldSpr);
		attachChild(_anySpr);
		attachChild(_allSpr);
		attachChild(_powerSpr);

		TiledSprite scoreSpr[] = new TiledSprite[8];
		for(int i=0;i<8;i++){
			scoreSpr[i] = new TiledSprite(0,0, activity._num_TR, engine.getVertexBufferObjectManager());
			scoreSpr[i].setCurrentTileIndex(0);
			attachChild(scoreSpr[i]);
		}

		goldSpr = new TiledSprite[6];
		for(int i=0;i<6;i++){
			goldSpr[i] = new TiledSprite(0,0, activity._num_TR, engine.getVertexBufferObjectManager());
			goldSpr[i].setCurrentTileIndex(0);
			attachChild(goldSpr[i]);
		}

		nowSpr = new TiledSprite[2];
		nextSpr = new TiledSprite[2];
		for(int i=0;i<2;i++){
			nowSpr[i] = new TiledSprite(142+i*25,693, activity._num_TR, engine.getVertexBufferObjectManager());
			nowSpr[i].setCurrentTileIndex(0);
			attachChild(nowSpr[i]);

			nextSpr[i] = new TiledSprite(218+i*25, 724, activity._num_TR, engine.getVertexBufferObjectManager());
			nextSpr[i].setCurrentTileIndex(0);
			attachChild(nextSpr[i]);
		}

		priceSpr = new TiledSprite[5];
		for(int i=0;i<5;i++){
			priceSpr[i] = new TiledSprite(86+i*25,761, activity._num_TR, engine.getVertexBufferObjectManager());
			priceSpr[i].setCurrentTileIndex(0);
			attachChild(priceSpr[i]);
		}

		nextPowGold = (activity.getPower()/4)*500+(activity.getPower()+1)*100;//파워업 비용 계산
		if(activity.getPower()==29)
			nextPowGold = 0;
		
		//숫자 스프라이트 정렬
		GameLib.setNum(scoreSpr, 390, 527, 8, activity.getScore(), true, 2);//최고점수 정렬
		GameLib.setNum(goldSpr, 390, 600, 8, activity.getGold(), false, 2);//소지골드 정렬
		GameLib.setNum(nowSpr, 142, 693, 2, activity.getPower()+1, true, 0);//현재 파워레벨 정렬
		GameLib.setNum(nextSpr, 218, 724, 2, activity.getPower()==29?30:activity.getPower()+2, true, 0);//다음 파워레벨 정렬
		GameLib.setNum(priceSpr, 216, 761, 5, nextPowGold, false, 2);//파워업에 필요한 비용 정렬

		//애니메이션
		_lyneSpr.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(1.0f),//시간 지연
				new MoveYModifier(1.0f, 900.0f, 182.0f),//아래에서 위로 이동하여 등장
				new LoopEntityModifier(//이하 내용을 반복 실행 (횟수지정 없음=무한반복)
					new SequenceEntityModifier(//이하 내용을 순서대로 실행
							new MoveYModifier(2.0f, 202.0f, 142.0f),//위로 올라갔다
							new MoveYModifier(2.0f, 142.0f, 202.0f)//아래로 내려간다
							) 
					)
				)
		);

		//EntityModifier의 처리 시작/끝에 연동하는 리스너 
		IEntityModifierListener _listener = new IEntityModifierListener() {
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				//이 리스너가 포함된 EntityModifier가 시작될 때 불린다
			}
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				//이 리스너가 포함된 EntityModifier가 끝날 때 불린다
				isLock = false;
			}
		};
		//로고 애니메이션
		_logoSpr.setScale(3.0f);
		_logoSpr.registerEntityModifier(new SequenceEntityModifier(//순서대로 실행
				new DelayModifier(1.5f),//시간지연
				new ParallelEntityModifier(//병행해서 실행
						new ScaleModifier(1.2f, 3.0f, 1.0f),//크기 변환
						new AlphaModifier(1.2f, 0, 1, _listener)//투명->불투명 상태로 전환
					)
				)
		);
		
		//아무 키나 누르세요 애니메이션
		_anySpr.registerEntityModifier(new LoopEntityModifier(//이하 내용을 반복 실행 (횟수지정 없음=무한반복)
				new SequenceEntityModifier(//이하 내용을 순서대로 실행
						new DelayModifier(2.0f),//시간 지연
						new AlphaModifier(0.1f,  1.0f, 0.0f),//불투명->투명
						new DelayModifier(1.0f),//시간 지연
						new AlphaModifier(0.1f,  0.0f, 1.0f)//투명->불투명
						)
				));
		
		registerTouchArea(_powerSpr);//파워 버튼을 등록한다
		
		//엔진 스레드가 루프를 돌 때마다 수행되는 업데이트 핸들러 
		this.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {
				// TODO Auto-generated method stub
			}
			@Override
			public void onUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub
				if(viewGold!=activity.getGold()){
					int dec = String.valueOf(Math.abs(viewGold-activity.getGold())).length();
					if(viewGold>activity.getGold()){
						viewGold-=Math.pow(10, dec-2<1?1:dec-2);
						if(viewGold<activity.getGold())
							viewGold = activity.getGold();
					}else{
						viewGold+=Math.pow(10, dec-2<1?1:dec-2);
						if(viewGold>activity.getGold())
							viewGold = activity.getGold();
					}
					GameLib.setNum(goldSpr, 390, 600, 8, viewGold, false, 2);//소지골드 정렬
				}
			}
		});
	}
}
