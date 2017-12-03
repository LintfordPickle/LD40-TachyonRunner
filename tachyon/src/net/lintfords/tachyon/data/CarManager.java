package net.lintfords.tachyon.data;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.data.BaseData;

public class CarManager extends BaseData {

	private static final long serialVersionUID = -1052490263558574073L;

	public static final int NOT_PLAYER_CONTROLED = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<Car> mCarsList;
	private Car mPlayerCar;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<Car> cars() {
		return mCarsList;
	}

	public Car playerCar() {
		return mPlayerCar;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public CarManager() {
		mCarsList = new ArrayList<>();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

	public Car addCar(boolean pPlayerControlled) {
		if (pPlayerControlled && mPlayerCar != null)
			return null;

		Car lNewCar = new Car();

		if (pPlayerControlled) {
			mPlayerCar = lNewCar;
			lNewCar.mPlayerControlled = true;

		} else {
			lNewCar.carSpriteName = "Car02";
			lNewCar.mPlayerControlled = false;
			lNewCar.carSpeedMax = 1200 * RandomNumbers.random(0.90f, 1.5f);

		}

		mCarsList.add(lNewCar);

		return lNewCar;

	}

	public void removeCar(Car pCar) {
		mCarsList.remove(pCar);

	}

}
