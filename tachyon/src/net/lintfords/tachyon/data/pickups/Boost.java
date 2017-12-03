package net.lintfords.tachyon.data.pickups;

public abstract class Boost extends Pickup {

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

	public Boost() {
		sx = 32;
		sy = 0;
		sw = 32;
		sh = 32;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

}
