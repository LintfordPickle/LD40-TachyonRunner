package net.lintfords.tachyon.data;

import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.data.entities.CircleEntity;
import net.lintford.library.data.entities.WorldEntity;

public class Car extends CircleEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1052490263558574073L;

	public final static float CAR_ACCELERATION = 30; // px?

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float dr;

	public Vector2f outerWallRear = new Vector2f();
	public Vector2f outerWallFront = new Vector2f();

	public Vector2f innerWallRear = new Vector2f();
	public Vector2f innerWallFront = new Vector2f();

	public Vector2f goingToOffset = new Vector2f();;

	public float mMaxHealth = 100;
	public float mHealth;

	public float currentDist;
	public int currentTrackPoint;
	public int currentLap;
	public int currentPosition;

	public Vector2f mHeadingTo;

	public Vector2f mFrontWheels;
	public Vector2f mRearWheels;

	public boolean mPlayerControlled;
	public boolean isRacing;

	public float wheelBase;
	public float heading;
	public float speed;
	public float carSpeedMax;
	public float carTurnAngleInc;
	public float carTurnAngleMax;
	public float steerAngle;

	public boolean handBrakeOn;
	public boolean gasDown;
	public boolean isSteering;

	public float width;
	public float height;

	public float r, g, b;

	public String carSpriteName = "Car01";
	public float sourceX, sourceY, sourceW, sourceH;

	public float bangTimer; // cars
	public float clangTimer; // Walls
	public float sparkTimer;
	public float smokeTimer;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Car() {
		mFrontWheels = new Vector2f();
		mRearWheels = new Vector2f();

		sourceX = 0;
		sourceY = 0;
		sourceW = 64;
		sourceH = 128;

		width = 64;
		height = 128;
		radius = 48;

		carSpeedMax = 1100 * RandomNumbers.random(0.90f, 1.1f);
		carTurnAngleInc = (float) Math.toRadians(1.1f * RandomNumbers.random(0.90f, 1.1f));
		carTurnAngleMax = (float) Math.toRadians(3f * RandomNumbers.random(0.90f, 1.1f));

		r = RandomNumbers.random(0.0f, 1.0f);
		g = RandomNumbers.random(0.0f, 1.0f);
		b = RandomNumbers.random(0.0f, 1.0f);
		
		mHealth = mMaxHealth;

	}

	@Override
	public boolean intersects(WorldEntity pOther) {
		return false;

	}

}
