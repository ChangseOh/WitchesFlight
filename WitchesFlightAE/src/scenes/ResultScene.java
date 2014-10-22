package scenes;

import gamelib.CustomScene;
import gamelib.GameLib;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.background.Background;
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

public class ResultScene extends CustomScene {

	public ResultScene(MainActivity activity, Engine engine){
		super(activity, engine);

		viewScore = 0;
		viewRange = 0;
		viewGold = 0;
		viewTotal = 0;
		
		cnt = 0;
		isTouch = true;
		isLock = true;
	}
	
	int cnt;
	boolean isTouch;
	boolean isLock;
	
	int resultScore;
	int resultRange;
	int resultGold;
	int resultTotal;

	int viewScore;
	int viewRange;
	int viewGold;
	int viewTotal;
	
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	private ITextureRegion _bg_TR;//배경
	private ITextureRegion _stampTR;//스탬프
	
	private TiledSprite scoreSpr[];
	private TiledSprite goldSpr[];
	private TiledSprite rangeSpr[];
	private TiledSprite totalSpr[];
	@Override
	public void loadResources() {
		// TODO Auto-generated method stub
		setBackground(new Background( 0.9804f, 0.6274f, 0.8784f ));

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("result/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048,2048, TextureOptions.BILINEAR);
		_bg_TR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "result_base.png");
		_stampTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "stamp.png");

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

		detachChildren();//scene에서 Entity를 제거한다
		
		//비트맵을 비사용상태로
		_bg_TR = null;
		_stampTR = null;
		
		//텍스처 언로드
		gameTextureAtlas.unload();
	}

	@Override
	public boolean callTouchEvent(
			TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		if(isTouch)
			return false;
		
		if(pSceneTouchEvent.getAction()==TouchEvent.ACTION_UP){
			activity.gamesound[3].play();
			activity.changeScene(new TitleScene(activity, engine));
		}
		return false;
	}

	@Override
	protected void startScene() {
		// TODO Auto-generated method stub
		
		if(isStarted)
			return;
		
		activity.gamesound[2].play();

		isStarted = true;
		
		Sprite bgSpr = new Sprite(0,0, _bg_TR, activity.getVertexBufferObjectManager());
		attachChild(bgSpr);
		
		Sprite stampSpr = new Sprite(37, 376, _stampTR, activity.getVertexBufferObjectManager());
		stampSpr.setAlpha(0);
		stampSpr.setScale(3.0f);
		stampSpr.setTag(1);
		
		Sprite stampeffectSpr = new Sprite(37, 376, _stampTR, activity.getVertexBufferObjectManager());
		stampeffectSpr.setAlpha(0);
		stampeffectSpr.setTag(2);

		attachChild(stampSpr);
		attachChild(stampeffectSpr);

		scoreSpr = new TiledSprite[8];
		for(int i=0;i<8;i++){
			scoreSpr[i] = new TiledSprite(0,0, activity._num_TR, engine.getVertexBufferObjectManager());
			scoreSpr[i].setCurrentTileIndex(0);
			attachChild(scoreSpr[i]);
		}
		totalSpr = new TiledSprite[8];
		for(int i=0;i<8;i++){
			totalSpr[i] = new TiledSprite(0,0, activity._num_TR, engine.getVertexBufferObjectManager());
			totalSpr[i].setCurrentTileIndex(0);
			attachChild(totalSpr[i]);
		}
		
		goldSpr = new TiledSprite[6];
		for(int i=0;i<6;i++){
			goldSpr[i] = new TiledSprite(0,0, activity._num_TR, engine.getVertexBufferObjectManager());
			goldSpr[i].setCurrentTileIndex(0);
			attachChild(goldSpr[i]);
		}
		
		rangeSpr = new TiledSprite[6];
		for(int i=0;i<6;i++){
			rangeSpr[i] = new TiledSprite(0,0, activity._num_TR, engine.getVertexBufferObjectManager());
			rangeSpr[i].setCurrentTileIndex(0);
			attachChild(rangeSpr[i]);
		}
		
		GameLib.setNum(scoreSpr, 328, 257, 8, viewScore, true, 2);
		GameLib.setNum(rangeSpr, 328, 332, 6, viewRange, true, 2);
		GameLib.setNum(totalSpr, 328, 434, 8, viewTotal, false, 2);
		GameLib.setNum(goldSpr, 328, 581, 6, viewGold, false, 2);
		
		Sprite _black = new Sprite(0, 0, activity._blackTR, activity.getVertexBufferObjectManager());
		_black.setScale(22.0f);
		attachChild(_black);
		
		_black.registerEntityModifier(new AlphaModifier(1.5f, 1.0f, 0));
		registerEntityModifier(new DelayModifier(1.0f, new IEntityModifierListener() {
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				// TODO Auto-generated method stub
				isLock = false;
			}
		}));
		
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
		
	}
	
	void update(){
		
		if(isLock)
			return;
		
		cnt++;

		//시간이 되면 목표값을 설정한다
		if(cnt==5)
			resultScore = activity.scoreBuff;
		
		if(cnt==20)
			resultRange = activity.rangeBuff;
		
		if(cnt==30)
			resultTotal = activity.scoreBuff + activity.rangeBuff;
		
		if(cnt==40)
			resultGold = activity.goldBuff;
		
		if(cnt==100){
			isTouch = false;
			
			if(activity.isNewrecord){//신기록 - 참잘했어요 도장 쾅
				//도장
				getChildByTag(1).registerEntityModifier(new SequenceEntityModifier(
						new ParallelEntityModifier(
								new ScaleModifier(0.4f, 3.0f, 1.0f),
								new AlphaModifier(0.3f, 0, 1.0f)
								),
						new ScaleModifier(0.02f, 1.0f, 1.1f),
						new ScaleModifier(0.02f, 1.1f, 1.0f)
						));
				//도장 파동
				getChildByTag(2).registerEntityModifier(new SequenceEntityModifier(
						new DelayModifier(0.3f),
						new ParallelEntityModifier(
								new ScaleModifier(0.3f, 1.0f, 5.0f),
								new AlphaModifier(0.3f, 1.0f, 0)
								)
						));
			}
		}
		
		if(viewScore!=resultScore){
			int dec = String.valueOf(resultScore-viewScore).length();
			viewScore += Math.pow(10,  dec-2<1?1:dec-2);
			if(viewScore>resultScore)
				viewScore = resultScore;
			GameLib.setNum(scoreSpr, 328, 257, 8, viewScore, true, 2);
		}
		if(viewRange!=resultRange){
			int dec = String.valueOf(resultRange-viewRange).length();
			viewRange += Math.pow(10,  dec-2<1?1:dec-2);
			if(viewRange>resultRange)
				viewRange = resultRange;
			GameLib.setNum(rangeSpr, 328, 332, 6, viewRange, true, 2);
		}
		if(viewTotal!=resultTotal){
			int dec = String.valueOf(resultTotal-viewTotal).length();
			viewTotal += Math.pow(10,  dec-2<1?1:dec-2);
			if(viewTotal>resultTotal)
				viewTotal = resultTotal;
			GameLib.setNum(totalSpr, 328, 434, 8, viewTotal, false, 2);
		}
		if(viewGold!=resultGold){
			int dec = String.valueOf(resultGold-viewGold).length();
			viewGold += Math.pow(10,  dec-2<1?1:dec-2);
			if(viewGold>resultGold)
				viewGold = resultGold;
			GameLib.setNum(goldSpr, 328, 581, 6, viewGold, false, 2);
		}
	}
	
}
