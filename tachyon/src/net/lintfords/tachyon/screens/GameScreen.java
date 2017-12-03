package net.lintfords.tachyon.screens;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.BaseControllerGroups;
import net.lintford.library.controllers.camera.CameraFollowController;
import net.lintford.library.controllers.camera.CameraZoomController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.camera.Camera;
import net.lintford.library.renderers.BaseRendererGroups;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.screens.BaseGameScreen;
import net.lintfords.tachyon.controllers.BackgroundParallaxController;
import net.lintfords.tachyon.controllers.CarsController;
import net.lintfords.tachyon.controllers.GameController;
import net.lintfords.tachyon.controllers.GameParticlesController;
import net.lintfords.tachyon.controllers.PickupController;
import net.lintfords.tachyon.controllers.TrackController;
import net.lintfords.tachyon.controllers.WeaponController;
import net.lintfords.tachyon.data.BackgroundParallax;
import net.lintfords.tachyon.data.CarManager;
import net.lintfords.tachyon.data.GameParticleSystem;
import net.lintfords.tachyon.data.GameState;
import net.lintfords.tachyon.data.PickupManager;
import net.lintfords.tachyon.data.Track;
import net.lintfords.tachyon.data.TrackGenerator;
import net.lintfords.tachyon.data.WeaponManager;
import net.lintfords.tachyon.renderers.BackgroundParallaxRenderer;
import net.lintfords.tachyon.renderers.CarsRenderer;
import net.lintfords.tachyon.renderers.GameParticleRenderer;
import net.lintfords.tachyon.renderers.GameStateRenderer;
import net.lintfords.tachyon.renderers.PickupRenderer;
import net.lintfords.tachyon.renderers.TrackRenderer;
import net.lintfords.tachyon.renderers.WeaponRenderer;

public class GameScreen extends BaseGameScreen {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private GameState mGameState;
	private PickupManager mPickupManager;
	private WeaponManager mWeaponManager;
	private Track mTrack;
	private CarManager mCarManager;
	private GameParticleSystem mGameParticles;
	private BackgroundParallax mBackgroundParallax;

	private GameController mGameController;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GameScreen(ScreenManager pScreenManager) {
		super(pScreenManager);

		mGameState = new GameState();
		mCarManager = new CarManager();
		mBackgroundParallax = new BackgroundParallax();
		mGameParticles = new GameParticleSystem();
		mPickupManager = new PickupManager();
		mWeaponManager = new WeaponManager();

		// Add cars
		mCarManager.addCar(false);
		mCarManager.addCar(false);
		mCarManager.addCar(false);
		mCarManager.addCar(false);
		mCarManager.addCar(true);
		mCarManager.addCar(false);
		mCarManager.addCar(false);

		mTrack = TrackGenerator.generateTrack(System.currentTimeMillis(), 128, 512);

		// Controllers
		ControllerManager lControllerManager = pScreenManager.core().controllerManager();
		lControllerManager.addController(new BackgroundParallaxController(lControllerManager, BaseControllerGroups.CONTROLLER_GAME_GROUP_ID, mBackgroundParallax));
		lControllerManager.addController(new GameParticlesController(lControllerManager, mGameParticles, BaseControllerGroups.CONTROLLER_GAME_GROUP_ID));
		lControllerManager.addController(new TrackController(lControllerManager, BaseControllerGroups.CONTROLLER_GAME_GROUP_ID, mTrack));
		mGameController = new GameController(lControllerManager, BaseControllerGroups.CONTROLLER_GAME_GROUP_ID, mGameState);
		lControllerManager.addController(mGameController);
		lControllerManager.addController(new CarsController(lControllerManager, BaseControllerGroups.CONTROLLER_GAME_GROUP_ID, mCarManager));
		lControllerManager.addController(new PickupController(lControllerManager, BaseControllerGroups.CONTROLLER_GAME_GROUP_ID, mPickupManager));
		lControllerManager.addController(new WeaponController(lControllerManager, BaseControllerGroups.CONTROLLER_GAME_GROUP_ID, mWeaponManager));

		// Renderers
		RendererManager lRendererManager = pScreenManager.core().rendererManager();
		lRendererManager.addRenderer(new BackgroundParallaxRenderer(lRendererManager, BaseRendererGroups.RENDERER_GAME_GROUP_ID));
		lRendererManager.addRenderer(new TrackRenderer(lRendererManager, BaseRendererGroups.RENDERER_GAME_GROUP_ID));
		lRendererManager.addRenderer(new CarsRenderer(lRendererManager, BaseRendererGroups.RENDERER_GAME_GROUP_ID));
		lRendererManager.addRenderer(new GameParticleRenderer(lRendererManager, BaseRendererGroups.RENDERER_GAME_GROUP_ID));
		lRendererManager.addRenderer(new GameStateRenderer(lRendererManager, BaseRendererGroups.RENDERER_GAME_GROUP_ID));
		lRendererManager.addRenderer(new PickupRenderer(lRendererManager, BaseRendererGroups.RENDERER_GAME_GROUP_ID));
		lRendererManager.addRenderer(new WeaponRenderer(lRendererManager, BaseRendererGroups.RENDERER_GAME_GROUP_ID));

		// Camera controllers
		Camera lGameCamera = (Camera) pScreenManager.core().gameCamera();

		CameraZoomController lCameraZoomController = new CameraZoomController(lControllerManager, lGameCamera, BaseControllerGroups.CONTROLLER_GAME_GROUP_ID);
		lCameraZoomController.setZoomConstraints(0.4f, 0.6f);
		lCameraZoomController.zoomFactor(0.5f);
		lControllerManager.addController(lCameraZoomController);

		CameraFollowController lCameraFollowController = new CameraFollowController(lControllerManager, lGameCamera, mCarManager.playerCar(), BaseControllerGroups.CONTROLLER_GAME_GROUP_ID);
		lControllerManager.addController(lCameraFollowController);

		lControllerManager.initialiseControllers();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void handleInput(LintfordCore pCore, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pCore, pAcceptMouse, pAcceptKeyboard);

		if (pCore.input().keyDown(GLFW.GLFW_KEY_P) || pCore.input().keyDown(GLFW.GLFW_KEY_ESCAPE)) {
			mScreenManager.addScreen(new PauseScreen(mScreenManager));
			return;
		}

		// TEST Add opponent
		if (pCore.input().keyDown(GLFW.GLFW_KEY_O)) {
			mCarManager.addCar(false);

		}

	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		if (!pCoveredByOtherScreen) {

			if(mGameController.playerFinished()) {
				mScreenManager.addScreen(new FinishedScreen(mScreenManager));
				
			}
			
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
