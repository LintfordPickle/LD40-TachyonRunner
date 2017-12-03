package net.lintfords.tachyon.renderers;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.sprites.AnimatedSprite;
import net.lintford.library.core.graphics.sprites.AnimatedSpriteListener;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheet;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;
import net.lintfords.tachyon.controllers.CarsController;
import net.lintfords.tachyon.data.Car;

public class CarsRenderer extends BaseRenderer implements AnimatedSpriteListener {

	public static final String RENDERER_NAME = "CarsRenderer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private CarsController mCarsController;

	private SpriteSheet mCarsSpriteSheet;
	private SpriteSheet mExplosionSpriteSheet;
	private SpriteBatch mSpriteBatch;

	private AnimatedSprite mExplosion;
	private float mExplosionPosX;
	private float mExplosionPosY;

	private List<AnimatedSprite> mExplosionPool;
	private List<AnimatedSprite> mExplosions;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int ZDepth() {
		return -3;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public CarsRenderer(RendererManager pRendererManager, int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);

		ControllerManager lControllerManager = pRendererManager.core().controllerManager();
		mCarsController = (CarsController) lControllerManager.getControllerByNameRequired(CarsController.CONTROLLER_NAME);

		mSpriteBatch = new SpriteBatch();

		mExplosionPool = new ArrayList<>();
		mExplosions = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mSpriteBatch.loadGLContent(pResourceManager);
		mCarsSpriteSheet = pResourceManager.spriteSheetManager().loadSpriteSheet("res/sprites/cars.sprites");
		mExplosionSpriteSheet = pResourceManager.spriteSheetManager().loadSpriteSheet("res/sprites/explosion.sprites");
		mExplosion = mExplosionSpriteSheet.getAnimation("Explosion");
		mExplosion.animatedSpriteListender(this);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mSpriteBatch.unloadGLContent();

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		mExplosion.update(pCore.time(), 1f);
	}

	@Override
	public void draw(LintfordCore pCore) {
		int NUM_CARS = mCarsController.carManager().cars().size();
		for (int i = 0; i < NUM_CARS; i++) {
			Car lCar = mCarsController.carManager().cars().get(i);

			renderCar(pCore, lCar);

		}

		if (mExplosion.enabled() && mExplosions.size() > 0) {
			mSpriteBatch.begin(pCore.gameCamera());
			float lEWidth = 64 * 3f;
			float lEHeight = 32 * 3f;

			mSpriteBatch.draw(mExplosionSpriteSheet.texture(), mExplosion.getSprite(), mExplosionPosX - lEWidth / 2, mExplosionPosY - lEHeight / 2, -3f, lEWidth, lEHeight, 1f, 1f, 1f, 1f, 0, lEWidth / 2, lEHeight / 2, 1f, 1f);
			mSpriteBatch.end();

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void renderCar(LintfordCore pCore, Car pCar) {

		AnimatedSprite carIdle = mCarsSpriteSheet.getAnimation(pCar.carSpriteName);
		carIdle.update(pCore.time(), 1f);
		float lScale = 1f + ((float) Math.cos(pCore.time().totalGameTime()) / 10f);

		float lWidth = carIdle.getW();
		float lHeight = carIdle.getH();

		mSpriteBatch.begin(pCore.gameCamera());
		mSpriteBatch.draw(mCarsSpriteSheet.texture(), carIdle.getSprite(), pCar.x - lWidth / 2 - 20, pCar.y - lHeight / 2 + 40, -3f, 64, 128, 0f, 0f, 0f, 0.5f, pCar.heading + (float) Math.toRadians(90), lWidth / 2, lHeight / 2, lScale, lScale);
		mSpriteBatch.draw(mCarsSpriteSheet.texture(), carIdle.getSprite(), pCar.x - lWidth / 2, pCar.y - lHeight / 2, -3f, 64, 128, 1f, 1f, 1f, 1f, pCar.heading + (float) Math.toRadians(90), lWidth / 2, lHeight / 2, lScale, lScale);
		mSpriteBatch.end();

	}

	@Override
	public void onStarted(AnimatedSprite pSender) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLooped(AnimatedSprite pSender) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopped(AnimatedSprite pSender) {
		// TODO Auto-generated method stub

	}

	public void playExplosion(float pWorldX, float pWorldY) {
		mExplosion.enabled(true);
		mExplosion.setFrame(0);
		mExplosionPosX = pWorldX;
		mExplosionPosY = pWorldY;

	}

}
