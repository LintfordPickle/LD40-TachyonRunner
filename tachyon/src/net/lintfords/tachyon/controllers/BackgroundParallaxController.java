package net.lintfords.tachyon.controllers;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.maths.Rectangle;
import net.lintfords.tachyon.data.BackgroundParallax;

public class BackgroundParallaxController extends BaseController {

	public static final String CONTROLLER_NAME = "BackgroundParallaxController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BackgroundParallax mBackgroundParallax;

	private GameParticlesController mGameParticlesController;

	private float mWispTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public BackgroundParallax backgroundParallax() {
		return mBackgroundParallax;
	}

	@Override
	public boolean isInitialised() {
		return mBackgroundParallax != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BackgroundParallaxController(ControllerManager pControllerManager, int pBaseControllerGroup, BackgroundParallax pBackground) {
		super(pControllerManager, CONTROLLER_NAME, pBaseControllerGroup);

		mBackgroundParallax = pBackground;

		mGameParticlesController = (GameParticlesController) pControllerManager.getControllerByName(GameParticlesController.CONTROLLER_NAME);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		mWispTimer += pCore.time().elapseGameTimeMilli();

		if (mWispTimer > 50) {
			if (mGameParticlesController != null) {
				ParticleController lWisp = mGameParticlesController.getParticleControllerByName("WispParticleSystem");
				if (lWisp != null) {

					Rectangle lGameArea = pCore.gameCamera().boundingRectangle();

					float lLocalSpaceX = RandomNumbers.random(0, lGameArea.width * 4f) - lGameArea.width * 2;
					float lLocalSpaceY = RandomNumbers.random(0, lGameArea.height * 4f) - lGameArea.height * 2;

					float lX = lLocalSpaceX + lGameArea.centerX();
					float lY = lLocalSpaceY + lGameArea.centerY();

					lWisp.particleSystem().spawnParticle(lX, lY, 0, 0, 5000f);

				}

			}
			mWispTimer = 0;
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
