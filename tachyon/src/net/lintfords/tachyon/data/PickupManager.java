package net.lintfords.tachyon.data;

import java.util.ArrayList;
import java.util.List;

import net.lintfords.tachyon.data.pickups.Pickup;

public class PickupManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<Pickup> mPickupPool;
	private List<Pickup> mPickupUpdate;
	private List<Pickup> mPickups;

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PickupManager() {

		mPickupPool = new ArrayList<>();
		mPickupUpdate = new ArrayList<>();
		mPickups = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

}
