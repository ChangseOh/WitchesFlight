package com.icegeo.witchesflightae;

import gamelib.CustomScene;
import gamelib.GameLib;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import scenes.TitleScene;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.KeyEvent;

public class MainActivity extends SimpleBaseGameActivity {

	public Camera camera;
	public static final int SCREEN_WIDTH = 480;
	public static final int SCREEN_HEIGHT = 800;
	CustomScene nowScene;
	
	//게임 정보
	private int score = 0;//최고 점수
	private int powlevel = 0;//현재 파워
	private int gold = 0;//현재 소지 골드

	public int scoreBuff = 0;
	public int goldBuff = 0;
	public int rangeBuff = 0;
	public boolean isNewrecord;
	
	public int getScore(){
		return GameLib.filter(score);
	}
	public int getPower(){
		return GameLib.filter(powlevel);
	}
	public int getGold(){
		return GameLib.filter(gold);
	}
	public void setScore(int score){
		this.score = GameLib.filter(score);
	}
	public void setPower(int power){
		powlevel = GameLib.filter(power);
	}
	public void setGold(int gold){
		this.gold = GameLib.filter(gold);
	}
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		// TODO Auto-generated method stub
		camera = new Camera (0,0, SCREEN_WIDTH, SCREEN_HEIGHT);
		EngineOptions _eOptions = new EngineOptions(true,
				ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(camera.getWidth() / camera.getHeight()),
				camera);
		
		_eOptions.getAudioOptions().setNeedsMusic(true);
		_eOptions.getAudioOptions().setNeedsSound(true);
		
		return _eOptions;
	}

	private BitmapTextureAtlas gameTextureAtlas;
	public ITiledTextureRegion _num_TR;//숫자
	public ITextureRegion _blackTR;//암전용 블랙스크린
	public ITextureRegion _whiteTR;//화이트아웃용 스크린

	public Sound gamesound[];//효과음

	MediaPlayer music;
	
	@Override
	protected void onCreateResources() {
		// TODO Auto-generated method stub
		
		try{
			gameTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 256,128, TextureOptions.BILINEAR);
			_num_TR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, this, "numpic.png", 0,0, 10,1);
			_blackTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, this, "black.png", 0,34);
			_whiteTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, this, "white.png", 50,34);
			gameTextureAtlas.load();
		
			gamesound = new Sound[8];
			SoundFactory.setAssetBasePath("sound/");
//			MusicFactory.setAssetBasePath("sound/");
			
			gamesound[0] = SoundFactory.createSoundFromAsset(getSoundManager(), this, "se_get.ogg");
			gamesound[1] = SoundFactory.createSoundFromAsset(getSoundManager(), this, "se_crash.ogg");
			gamesound[2] = SoundFactory.createSoundFromAsset(getSoundManager(), this, "gameover.ogg");
			gamesound[3] = SoundFactory.createSoundFromAsset(getSoundManager(), this, "se_shoot.ogg");
			gamesound[4] = SoundFactory.createSoundFromAsset(getSoundManager(), this, "se_cash.ogg");
			gamesound[5] = SoundFactory.createSoundFromAsset(getSoundManager(), this, "item1.ogg");
			gamesound[6] = SoundFactory.createSoundFromAsset(getSoundManager(), this, "se_kwakaka.ogg");
			gamesound[7] = SoundFactory.createSoundFromAsset(getSoundManager(), this, "se_shoong.ogg");
			
			gamesound[0].setLooping(false);
			gamesound[1].setLooping(false);
			gamesound[2].setLooping(false);
			gamesound[3].setLooping(false);
			gamesound[4].setLooping(false);
			gamesound[5].setLooping(false);
			gamesound[6].setLooping(false);
			gamesound[7].setLooping(false);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected Scene onCreateScene() {
		// TODO Auto-generated method stub

		if(!GameLib.LoadGame(this, "gamedat")){//데이타 불러오기
			
			//실패하면 초기화
			score = GameLib.filter(1000);//최고 점수
			powlevel = GameLib.filter(0);//현재 파워
			gold = GameLib.filter(0);//현재 소지 골드
		}
		
		TitleScene scene = new TitleScene(this, mEngine);
		nowScene = scene;
		
		return scene;
	}
	
	void releaseResources(){
		
		if(nowScene!=null)
			nowScene.releaseResource();

		//공용 리소스 해제
		mEngine.setScene(null);
		_num_TR = null;
		_blackTR = null;
		_whiteTR = null;
		gameTextureAtlas.unload();
		
		//사운드 해제
		for(int i=0;i<gamesound.length;i++)
			gamesound[i].release();
	}
	
	public void changeScene(CustomScene scene){

		nowScene.releaseResource();
		
		mEngine.setScene(scene);
		nowScene = scene;

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if(event.getAction()==KeyEvent.ACTION_UP && keyCode==KeyEvent.KEYCODE_BACK){
			onPauseGame();
			AlertDialog.Builder _dialog;
			_dialog = new AlertDialog.Builder(this);
			
	    	_dialog.setPositiveButton("Exit", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					releaseResources();
					finish();
					System.exit(0);
				}
			});
	    	_dialog.setNegativeButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					onResumeGame();
				}
			});
	    	_dialog.setMessage("Do you want Exit?");
	    	_dialog.show();
	    	return false;
		}

    	return super.onKeyUp(keyCode, event);
	}
	@Override
	public synchronized void onResumeGame() {
		// TODO Auto-generated method stub
		if(music!=null)
			music.start();
		super.onResumeGame();
	}
	@Override
	public synchronized void onPauseGame() {
		// TODO Auto-generated method stub
		if(music!=null)
			music.pause();
		super.onPauseGame();
	}
	
	int nowMusic = -1;
	public void playMusic(int musicraw, boolean roof){
		if(music!=null) {
			music.stop();
			music.release();
		}
		music=null;
		//if(!_bgm) return;
		//music=MediaPlayer.create(context, _music[kind]);
		music=MediaPlayer.create(this, musicraw);

		music.setVolume(0.9f,0.9f);
		music.setLooping(roof);
		music.seekTo(0);
		music.start();
	}
	public void stopMusic(){
		if(music!=null) {
			music.stop();
			music.release();
		}
		music=null;
	}
}
