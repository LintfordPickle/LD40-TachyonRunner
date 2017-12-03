package net.lintfords.tachyon.renderers;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.linebatch.LineBatch;
import net.lintford.library.core.graphics.sprites.AnimatedSprite;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheet;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;
import net.lintfords.tachyon.controllers.CarsController;
import net.lintfords.tachyon.data.Car;

public class CarsRenderer extends BaseRenderer {

	public static final String RENDERER_NAME = "CarsRenderer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private CarsController mCarsController;

	private SpriteSheet mCarsSpriteSheet;
	private SpriteBatch mSpriteBatch;

	private LineBatch mTextureBatch;

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

		mTextureBatch = new LineBatch();
		mSpriteBatch = new SpriteBatch();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mSpriteBatch.loadGLContent(pResourceManager);
		mCarsSpriteSheet = pResourceManager.spriteSheetManager().loadSpriteSheet("res/sprites/cars.sprites");

		mTextureBatch.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mSpriteBatch.unloadGLContent();
		mTextureBatch.unloadGLContent();

	}

	@Override
	public void draw(LintfordCore pCore) {
		int NUM_CARS = mCarsController.carManager().cars().size();
		for (int i = 0; i < NUM_CARS; i++) {
			Car lCar = mCarsController.carManager().cars().get(i);

			renderCar(pCore, lCar);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void renderCar(LintfordCore pCore, Car pCar) {

		AnimatedSprite carIdle = mCarsSpriteSheet.getAnimation(pCar.carSpriteName);
		float lScale = 1f + ((float) Math.cos(pCore.time().totalGameTime()) / 10f);

		float lWidth = carIdle.getW();
		float lHeight = carIdle.getH();

		mSpriteBatch.begin(pCore.gameCamera());
		mSpriteBatch.draw(mCarsSpriteSheet.texture(), carIdle.getSprite(), pCar.x - lWidth / 2 - 10, pCar.y - lHeight / 2 + 15, -3f, 64, 128, 0f, 0f, 0f, 0.2f, pCar.heading + (float) Math.toRadians(90), lWidth / 2, lHeight / 2, lScale, lScale);
		mSpriteBatch.draw(mCarsSpriteSheet.texture(), carIdle.getSprite(), pCar.x - lWidth / 2, pCar.y - lHeight / 2, -3f, 64, 128, 1f, 1f, 1f, 1f, pCar.heading + (float) Math.toRadians(90), lWidth / 2, lHeight / 2, lScale, lScale);
		mSpriteBatch.end();
		
		mTextureBatch.begin(pCore.gameCamera());
		mTextureBatch.changeColorNormalized(1f, 0f, 0f, 1f);
		mTextureBatch.draw(pCar.outerWallRear.x, pCar.outerWallRear.y, pCar.outerWallFront.x, pCar.outerWallFront.y, -1f);
		mTextureBatch.draw(pCar.innerWallRear.x, pCar.innerWallRear.y, pCar.innerWallFront.x, pCar.innerWallFront.y, -1f);
		mTextureBatch.end();
		

	}

}
