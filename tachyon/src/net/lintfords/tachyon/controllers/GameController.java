package net.lintfords.tachyon.controllers;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.maths.Vector2f;
import net.lintfords.tachyon.data.Car;
import net.lintfords.tachyon.data.GameState;
import net.lintfords.tachyon.data.StartGridElement;

public class GameController extends BaseController {

	public static final String CONTROLLER_NAME = "GameController";

	public static final int COUNT_DOWN_TIME = 1;
	public static final float COUNT_DOWN_INC_TIME = 1000f;

	public static final int NUM_LAPS = 5;
	public static final int NUM_RACERS = 10;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private GameState mGameState;
	private CarsController mCarsController;
	private TrackController mTrackController;

	private int mCountDownTimer;
	private int mCountDown = COUNT_DOWN_TIME;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int countDownTimer() {
		return mCountDownTimer;
	}

	public int countDownTime() {
		return mCountDown;
	}

	public boolean hasRaceStarted() {
		return mCountDown <= 0;
	}

	public GameState gameState() {
		return mGameState;
	}

	@Override
	public boolean isInitialised() {
		return false;
	}

	public boolean playerFinished() {
		return mGameState.currentLap >= mGameState.totalLaps;
	}

	public boolean playerStillAlive() {
		return mGameState.playerAlive;
	}

	public int playerPosition() {
		return mGameState.playerPosition;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GameController(ControllerManager pControllerManager, int pGroupID, GameState pGameState) {
		super(pControllerManager, CONTROLLER_NAME, pGroupID);

		mGameState = pGameState;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		mCarsController = (CarsController) mControllerManager.getControllerByNameRequired(CarsController.CONTROLLER_NAME);
		mTrackController = (TrackController) mControllerManager.getControllerByNameRequired(TrackController.CONTROLLER_NAME);

		int startGridID = mTrackController.track().getNumTrackPoints() - 1;

		Vector2f tempDriveDirection = new Vector2f();
		Vector2f tempSideDirection = new Vector2f();

		// Set the start grid
		final int NUM_CARS = mCarsController.carManager().cars().size();
		boolean left = true;
		for (int i = 0; i < NUM_CARS; i++) {
			Vector2f lStartPosition = mTrackController.track().getTrackPoint(startGridID);
			Vector2f lPrevPosition = mTrackController.track().getTrackPoint(startGridID - 1);

			tempDriveDirection.x = lPrevPosition.x - lStartPosition.x;
			tempDriveDirection.y = lPrevPosition.y - lStartPosition.y;

			tempSideDirection.x = tempDriveDirection.y;
			tempSideDirection.y = -tempDriveDirection.x;

			tempSideDirection.nor();

			float lStartAngle = (float) Math.atan2(tempDriveDirection.y, tempDriveDirection.x) + (float) Math.toRadians(180);

			float lSideSignumX = left ? tempSideDirection.x : -tempSideDirection.x;
			float lSideSignumY = left ? tempSideDirection.y : -tempSideDirection.y;

			Vector2f outerPoint = new Vector2f();
			outerPoint.x = lStartPosition.x + lSideSignumX * mTrackController.track().segmentWidth / 4;
			outerPoint.y = lStartPosition.y + lSideSignumY * mTrackController.track().segmentWidth / 4;

			Car lCar = mCarsController.carManager().cars().get(i);

			lCar.currentPosition = i;
			lCar.currentLap = 0;
			lCar.currentDist = 0;

			lCar.heading = lStartAngle;
			lCar.x = outerPoint.x;
			lCar.y = outerPoint.y;

			mTrackController.track().startElements().add(new StartGridElement(outerPoint.x, outerPoint.y, lStartAngle));

			left = !left;
			startGridID -= 1;

		}

		// inform the game state
		mGameState.playerHealthMax = 100;

		mGameState.totalLaps = NUM_LAPS;
		mGameState.totalRacers = mCarsController.carManager().cars().size();

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		Car lPlayerCar = mCarsController.carManager().playerCar();

		mGameState.playerHealth = (int) lPlayerCar.mHealth;
		mGameState.currentLap = MathHelper.clampi(lPlayerCar.currentLap, 0, 4);
		mGameState.playerPosition = lPlayerCar.currentPosition;
		mGameState.currentTrackPoint = lPlayerCar.currentTrackPoint;

		if (lPlayerCar.mHealth <= 0) {
			mGameState.playerAlive = false;

		}

		// Countdown to begin
		if (!hasRaceStarted()) {
			mCountDownTimer += pCore.time().elapseGameTimeMilli();
			if (mCountDownTimer > COUNT_DOWN_INC_TIME) {
				mCountDownTimer -= COUNT_DOWN_INC_TIME;
				mCountDown--;

			}

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
