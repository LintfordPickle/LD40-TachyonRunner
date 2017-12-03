package net.lintfords.tachyon.controllers;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintfords.tachyon.data.WeaponManager;

public class WeaponController extends BaseController {

	public static final String CONTROLLER_NAME = "WeaponController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private WeaponManager mWeaponManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialised() {
		// TODO Auto-generated method stub
		return false;
	}

	public WeaponManager weaponManager() {
		return mWeaponManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public WeaponController(ControllerManager pControllerManager, int pGroupID, WeaponManager pWeaponManager) {
		super(pControllerManager, CONTROLLER_NAME, pGroupID);

		mWeaponManager = pWeaponManager;

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
