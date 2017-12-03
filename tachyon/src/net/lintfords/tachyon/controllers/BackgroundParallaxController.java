package net.lintfords.tachyon.controllers;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintfords.tachyon.data.BackgroundParallax;

public class BackgroundParallaxController extends BaseController {

	public static final String CONTROLLER_NAME = "BackgroundParallaxController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BackgroundParallax mBackgroundParallax;

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

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		// TODO Auto-generated method stub

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
