package net.lintfords.tachyon.data.pickups;

import net.lintford.library.data.BaseData;

public abstract class Pickup extends BaseData {

	private static final long serialVersionUID = 7062681712561549260L;

	public enum PICKUP_TYPE {
		Missile, Health, Boost,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	public PICKUP_TYPE mPickUpType = PICKUP_TYPE.Boost;
	public float sx, sy, sw, sh; // source tex
	

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

}
