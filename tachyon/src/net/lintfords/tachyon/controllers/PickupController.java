package net.lintfords.tachyon.controllers;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintfords.tachyon.data.PickupManager;
import net.lintfords.tachyon.data.pickups.Boost.PICKUP_TYPE;

public class PickupController extends BaseController {

	public static final String CONTROLLER_NAME = "PickupController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private PickupManager mPickupManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialised() {
		// TODO Auto-generated method stub
		return false;
	}

	public PickupManager pickupManager() {
		return mPickupManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PickupController(ControllerManager pControllerManager, int pGroupID, PickupManager pPickupManager) {
		super(pControllerManager, CONTROLLER_NAME, pGroupID);

		mPickupManager = pPickupManager;

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

	public void addPickup(PICKUP_TYPE pType, float pX, float pY) {
		
	}
	
}
