package net.lintfords.tachyon.data;

import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.data.entities.CircleEntity;
import net.lintford.library.data.entities.WorldEntity;

public class Car extends CircleEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1052490263558574073L;

	public final static float CAR_ACCELERATION = 40; // px?

	public final static float CAR_TURN_ANGLE_INC = (float) Math.toRadians(2f); // increments in radians
	public final static float CAR_TURN_ANGLE_MAX = (float) Math.toRadians(3f); // should be in rads

	// --------------------------------------
	// Variables
	// --------------------------------------

	public Vector2f outerWallRear = new Vector2f();
	public Vector2f outerWallFront = new Vector2f();
	
	public Vector2f innerWallRear = new Vector2f();
	public Vector2f innerWallFront = new Vector2f();
	
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
	public float carSpeedMax = 1800; // px?
	public float steerAngle;

	public boolean handBrakeOn;
	public boolean gasDown;
	public boolean isSteering;

	public float width;
	public float height;

	public String carSpriteName = "Car01";
	public float sourceX, sourceY, sourceW, sourceH;

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
		radius = 32;

		mHealth = mMaxHealth;

	}

	@Override
	public boolean intersects(WorldEntity pOther) {
		return false;

	}

}
