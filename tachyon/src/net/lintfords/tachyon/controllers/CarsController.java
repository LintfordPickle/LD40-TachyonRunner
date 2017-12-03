package net.lintfords.tachyon.controllers;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.camera.CameraShakeController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.maths.Vector2f;
import net.lintfords.tachyon.data.Car;
import net.lintfords.tachyon.data.CarManager;
import net.lintfords.tachyon.renderers.CarsRenderer;

public class CarsController extends BaseController {

	public static final String CONTROLLER_NAME = "CarsController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private GameParticlesController mParticlesController;
	private TrackController mTrackController;
	private CarManager mCarManager;
	private GameController mGameController;
	private CameraShakeController mCameraShakeController;

	// SHould be a better way than logic access UI
	private CarsRenderer mCarRenderer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialised() {
		// TODO Auto-generated method stub
		return false;
	}

	public CarManager carManager() {
		return mCarManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public CarsController(ControllerManager pControllerManager, int pGroupID, CarManager pCarManager) {
		super(pControllerManager, CONTROLLER_NAME, pGroupID);

		mCarManager = pCarManager;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		mTrackController = (TrackController) mControllerManager.getControllerByNameRequired(TrackController.CONTROLLER_NAME);
		mParticlesController = (GameParticlesController) mControllerManager.getControllerByNameRequired(GameParticlesController.CONTROLLER_NAME);
		mGameController = (GameController) mControllerManager.getControllerByNameRequired(GameController.CONTROLLER_NAME);
		mCameraShakeController = (CameraShakeController) mControllerManager.getControllerByName(CameraShakeController.CONTROLLER_NAME);
		
		
		mCarRenderer = (CarsRenderer) mControllerManager.core().rendererManager().getRenderer(CarsRenderer.RENDERER_NAME);
		

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (!mGameController.hasRaceStarted() || !mGameController.playerStillAlive() || mGameController.playerFinished())
			return false;

		Car lPlayerCar = mCarManager.playerCar();
		if (lPlayerCar != null) {

			boolean isSteering = false;
			if (pCore.input().keyDown(GLFW.GLFW_KEY_W)) {
				lPlayerCar.speed += Car.CAR_ACCELERATION;

			}

			if (pCore.input().keyDown(GLFW.GLFW_KEY_S)) {
				lPlayerCar.speed -= Car.CAR_ACCELERATION * 0.7f;
			}

			if (pCore.input().keyDown(GLFW.GLFW_KEY_A)) {
				lPlayerCar.steerAngle += -Car.CAR_TURN_ANGLE_INC;
				isSteering = true;
			}

			if (pCore.input().keyDown(GLFW.GLFW_KEY_D)) {
				lPlayerCar.steerAngle += Car.CAR_TURN_ANGLE_INC;
				isSteering = true;
			}

			if (!isSteering)
				lPlayerCar.steerAngle *= 0.94f;

			if (pCore.input().keyDown(GLFW.GLFW_KEY_SPACE)) {
				lPlayerCar.handBrakeOn = true;

			}

		}

		if (pCore.input().keyDown(GLFW.GLFW_KEY_R)) {
			int NUM_CARS = carManager().cars().size();
			for (int i = 0; i < NUM_CARS; i++) {
				Car lCar = carManager().cars().get(i);

				lCar.currentTrackPoint = 0;

				lCar.x = 0;
				lCar.y = 0;

				lCar.speed = 0;
				lCar.heading = 0;

				lCar.mHealth = lCar.mMaxHealth;

			}

		}

		return super.handleInput(pCore);

	}

	@Override
	public void update(LintfordCore pCore) {
		if (!mGameController.hasRaceStarted())
			return;

		super.update(pCore);

		int NUM_CARS = carManager().cars().size();
		for (int i = 0; i < NUM_CARS; i++) {
			Car lCar = carManager().cars().get(i);

			if (!lCar.mPlayerControlled || mGameController.playerFinished()) {
				// updateAI(pCore, lCar);

			} else {

				final int NUM_CONTROL_POINTS = mTrackController.track().getNumTrackPoints();
				final int pNextControlPointID = (lCar.currentTrackPoint + 1) % NUM_CONTROL_POINTS;

				Vector2f pNextControlPoint = mTrackController.track().getTrackPoint(pNextControlPointID);

				final float lHeadingVecX = pNextControlPoint.x - lCar.x;
				final float lHeadingVecY = pNextControlPoint.y - lCar.y;

				float dist = (float) Math.sqrt((lHeadingVecX * lHeadingVecX) + (lHeadingVecY * lHeadingVecY));

				// Check if the vehicle has reached the destination
				if (dist < 512) {
					lCar.currentTrackPoint = pNextControlPointID;
					if (lCar.currentTrackPoint == 0)
						lCar.currentLap++;

				}

			}

			updateCar(pCore, lCar);

			// Update this car collisions with the track
			updateTrackCollisions(pCore, lCar);

			// Update this car collisions with other car
			for (int j = i + 1; j < NUM_CARS; j++) {
				if (lCar == carManager().cars().get(j))
					continue;

				Car lCarOther = carManager().cars().get(j);
				updateEntityCollisions(lCar, lCarOther);

			}
			
			// Check health 
			

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void updateAI(LintfordCore pCore, Car pCar) {
		final int NUM_TRACK_POINTS = mTrackController.track().getNumTrackPoints();
		final int pNextTrackPointID = (pCar.currentTrackPoint + 1) % NUM_TRACK_POINTS;

		Vector2f pNextControlPoint = mTrackController.track().getTrackPoint(pNextTrackPointID);

		// The amount of throttle and turn speed to be applied will depend on
		// the Fresnel term (faster if facing the target)
		final float lHeadingVecX = pNextControlPoint.x - pCar.x;
		final float lHeadingVecY = pNextControlPoint.y - pCar.y;

		float dist = (float) Math.sqrt((lHeadingVecX * lHeadingVecX) + (lHeadingVecY * lHeadingVecY));

		// Check if the vehicle has reached the destination
		if (dist > 512) {

			float lHeadingAngle = turnToFace(lHeadingVecX, lHeadingVecY, pCar.heading, Car.CAR_TURN_ANGLE_INC);

			pCar.steerAngle += lHeadingAngle;
			pCar.isSteering = true;

			pCar.speed += Car.CAR_ACCELERATION;

		} else {
			// Arrived at checkpoint
			pCar.currentTrackPoint = pNextTrackPointID;
		}

	}

	static float turnToFace(float pHeadingX, float pHeadingY, float pCurrentAngle, float pTurnSpeed) {
		float desiredAngle = (float) Math.atan2(pHeadingY, pHeadingX);

		float difference = wrapAngle(desiredAngle - pCurrentAngle);

		// clamp
		difference = clamp(difference, -pTurnSpeed, pTurnSpeed);

		return wrapAngle(difference);

	}

	/** wraps to -PI / PI */
	public static float wrapAngle(float radians) {
		while (radians < -Math.PI) {
			radians += Math.PI * 2;
		}
		while (radians > Math.PI) {
			radians -= Math.PI * 2;
		}
		return radians;
	}

	static float clamp(float v, float min, float max) {
		return Math.max(min, Math.min(max, v));
	}

	public static float lerp(float a, float b, float f) {
		return a + f * (b - a);
	}

	public void updateCar(LintfordCore pCore, Car pCar) {

		pCar.wheelBase = 32;
		pCar.speed = MathHelper.clamp(pCar.speed, -pCar.carSpeedMax * 0.5f, pCar.carSpeedMax);
		pCar.steerAngle = MathHelper.clamp(pCar.steerAngle, -Car.CAR_TURN_ANGLE_MAX, Car.CAR_TURN_ANGLE_MAX);

		// Project the wheel out along the heading direction, from the center of the vehicle
		pCar.mFrontWheels.x = pCar.x + pCar.wheelBase / 2 * (float) Math.cos(pCar.heading);
		pCar.mFrontWheels.y = pCar.y + pCar.wheelBase / 2 * (float) Math.sin(pCar.heading);

		pCar.mRearWheels.x = pCar.x - pCar.wheelBase / 2 * (float) Math.cos(pCar.heading);
		pCar.mRearWheels.y = pCar.y - pCar.wheelBase / 2 * (float) Math.sin(pCar.heading);

		// Move the wheels
		// Rear wheels move in carHeading
		pCar.mRearWheels.x += pCar.speed * pCore.time().elapseGameTimeSeconds() * (float) Math.cos(pCar.heading);
		pCar.mRearWheels.y += pCar.speed * pCore.time().elapseGameTimeSeconds() * (float) Math.sin(pCar.heading);

		// Front wheels move along carHeading + steering direction
		pCar.mFrontWheels.x += pCar.speed * pCore.time().elapseGameTimeSeconds() * (float) Math.cos(pCar.heading + pCar.steerAngle);
		pCar.mFrontWheels.y += pCar.speed * pCore.time().elapseGameTimeSeconds() * (float) Math.sin(pCar.heading + pCar.steerAngle);

		// Extrapolate the new car location (center of new wheel position)
		pCar.x = (pCar.mFrontWheels.x + pCar.mRearWheels.x) / 2;
		pCar.y = (pCar.mFrontWheels.y + pCar.mRearWheels.y) / 2;

		pCar.heading = (float) Math.atan2(pCar.mFrontWheels.y - pCar.mRearWheels.y, pCar.mFrontWheels.x - pCar.mRearWheels.x);

		pCar.speed *= 0.98f;
		pCar.steerAngle *= 0.98f;

		ParticleController pc = mParticlesController.getParticleControllerByName("Trails");
		if (pc != null && Math.abs(pCar.speed) > 10) {
			float lVelX = (float) -Math.cos(pCar.heading);
			float lVelY = (float) -Math.sin(pCar.heading);
			pc.particleSystem().spawnParticle(pCar.x + 16 + lVelX * 75f, pCar.y + 16f + lVelY * 75f, lVelX * pCar.speed * 0.0005f, lVelY * pCar.speed * 0.0005f, 200f, 0, 0, 32, 32, 16);

		}

	}

	public void updateTrackCollisions(LintfordCore pCore, Car pCar) {

		Vector2f tempDriveDirection = new Vector2f();
		Vector2f tempSideDirection = new Vector2f();

		int lIDFOund = mTrackController.getClosestTrackPointID(pCar.x, pCar.y);
		int prevTrackPointID = lIDFOund - 1;
		int nextTrackPointID = lIDFOund + 1;

		if (prevTrackPointID < 0)
			prevTrackPointID = mTrackController.track().getNumTrackPoints() - 1;

		if (nextTrackPointID > mTrackController.track().getNumTrackPoints() - 1)
			nextTrackPointID = 0;

		Vector2f lPrevPosition = mTrackController.track().getTrackPoint(prevTrackPointID);
		Vector2f lStartPosition = mTrackController.track().getTrackPoint(nextTrackPointID);

		tempDriveDirection.x = lPrevPosition.x - lStartPosition.x;
		tempDriveDirection.y = lPrevPosition.y - lStartPosition.y;

		tempSideDirection.x = tempDriveDirection.y;
		tempSideDirection.y = -tempDriveDirection.x;

		tempSideDirection.nor();
		tempDriveDirection.nor();

		final float lOffset = 2.2f;
		pCar.outerWallRear.x = lPrevPosition.x + tempSideDirection.x * mTrackController.track().segmentWidth / lOffset;
		pCar.outerWallRear.y = lPrevPosition.y + tempSideDirection.y * mTrackController.track().segmentWidth / lOffset;

		pCar.outerWallFront.x = lStartPosition.x + tempSideDirection.x * mTrackController.track().segmentWidth / lOffset;
		pCar.outerWallFront.y = lStartPosition.y + tempSideDirection.y * mTrackController.track().segmentWidth / lOffset;

		pCar.innerWallRear.x = lPrevPosition.x - tempSideDirection.x * mTrackController.track().segmentWidth / lOffset;
		pCar.innerWallRear.y = lPrevPosition.y - tempSideDirection.y * mTrackController.track().segmentWidth / lOffset;

		pCar.innerWallFront.x = lStartPosition.x - tempSideDirection.x * mTrackController.track().segmentWidth / lOffset;
		pCar.innerWallFront.y = lStartPosition.y - tempSideDirection.y * mTrackController.track().segmentWidth / lOffset;

		if (pCar.mPlayerControlled) {
			// Check for collisions against both walls
			float distO = getCircleLineIntersectionPoint(pCar.outerWallRear, pCar.outerWallFront, new Vector2f(pCar.x, pCar.y), pCar.radius);
			if (distO > 0) {
				// Calc the car velocity
				Vector2f carVelocity = new Vector2f();
				carVelocity.x = (float) Math.cos(pCar.heading) * pCar.speed;
				carVelocity.y = (float) Math.sin(pCar.heading) * pCar.speed;

				Vector2f wallNormal = new Vector2f();
				wallNormal.x = pCar.outerWallRear.x - lPrevPosition.x;
				wallNormal.y = pCar.outerWallRear.y - lPrevPosition.y;

				wallNormal.nor();
				carVelocity.nor();

				Vector2f reflectVelocityNormal = new Vector2f();
				// reflectVelocityNormal = carVelocity.reflected(wallNormal);
				reflectVelocityNormal.x = wallNormal.y;
				reflectVelocityNormal.y = -wallNormal.x;

				// push car away from wall
				{
					float aa = (float) Math.atan2(-wallNormal.y, -wallNormal.x);
					pCar.x += (float) Math.cos(aa) * 5f;
					pCar.y += (float) Math.sin(aa) * 5f;

				}

				// glide the car with the wall
				float aa = (float) Math.atan2(reflectVelocityNormal.y, reflectVelocityNormal.x);
				pCar.heading = aa;
				pCar.steerAngle = -Car.CAR_TURN_ANGLE_MAX * 0.5f;

				ParticleController pc = mParticlesController.getParticleControllerByName("Sparks");
				if (pc != null) {
					float dirX = reflectVelocityNormal.x;
					float dirY = reflectVelocityNormal.y;

					for (int j = 0; j < 7; j++) {
						float a = (float) Math.atan2(dirY, dirX);
						a += (float) Math.toRadians(RandomNumbers.random(0f, 60f) - 60f);

						float lVelX = (float) -Math.cos(a);
						float lVelY = (float) -Math.sin(a);

						pc.particleSystem().spawnParticle(pCar.x + 16, pCar.y + 16, lVelX * 0.5f, lVelY * 0.5f, 350f + RandomNumbers.random(0f, 200f), 32, 0, 32, 32, 16);

					}

				}
				
				pCar.mHealth -= 0.5f * (pCar.speed / pCar.carSpeedMax) * 2f;
				
				mCarRenderer.playExplosion(pCar.x, pCar.y);
				
				if(pCar.mPlayerControlled) {
					if(mCameraShakeController != null)
						mCameraShakeController.shake(300f, 20f);
				}
				
			}

			float distI = getCircleLineIntersectionPoint(pCar.innerWallRear, pCar.innerWallFront, new Vector2f(pCar.x, pCar.y), pCar.radius);

			if (distI > 0) {
				// Calc the car velocity
				Vector2f carVelocity = new Vector2f();
				carVelocity.x = (float) Math.cos(pCar.heading) * pCar.speed;
				carVelocity.y = (float) Math.sin(pCar.heading) * pCar.speed;

				Vector2f wallNormal = new Vector2f();
				wallNormal.x = pCar.innerWallRear.x - lPrevPosition.x;
				wallNormal.y = pCar.innerWallRear.y - lPrevPosition.y;

				wallNormal.nor();
				carVelocity.nor();

				Vector2f reflectVelocityNormal = new Vector2f();
				// reflectVelocityNormal = carVelocity.reflected(wallNormal);
				reflectVelocityNormal.x = -wallNormal.y;
				reflectVelocityNormal.y = wallNormal.x;

				// push car away from wall
				{
					float aa = (float) Math.atan2(-wallNormal.y, -wallNormal.x);
					pCar.x += (float) Math.cos(aa) * 5f;
					pCar.y += (float) Math.sin(aa) * 5f;

				}

				// glide the car with the wall
				float aa = (float) Math.atan2(reflectVelocityNormal.y, reflectVelocityNormal.x);
				pCar.heading = aa;
				pCar.steerAngle = Car.CAR_TURN_ANGLE_MAX * 0.5f;

				ParticleController pc = mParticlesController.getParticleControllerByName("Sparks");
				if (pc != null) {
					float dirX = reflectVelocityNormal.x;
					float dirY = reflectVelocityNormal.y;

					for (int j = 0; j < 7; j++) {
						float a = (float) Math.atan2(dirY, dirX);
						a += (float) Math.toRadians(RandomNumbers.random(0f, 60f) - 60f);

						float lVelX = (float) -Math.cos(a);
						float lVelY = (float) -Math.sin(a);

						pc.particleSystem().spawnParticle(pCar.x + 16, pCar.y + 16, lVelX * 0.5f, lVelY * 0.5f, 350f + RandomNumbers.random(0f, 200f), 32, 0, 32, 32, 16);

					}

				}
				
				mCarRenderer.playExplosion(pCar.x, pCar.y);
				
				pCar.mHealth -= 0.5f * (pCar.speed / pCar.carSpeedMax) * 2f;
				
				if(pCar.mPlayerControlled) {
					if(mCameraShakeController != null)
						mCameraShakeController.shake(300f, 20f);
				}

			}

		}

	}

	public void updateEntityCollisions(Car pCar0, Car pCar1) {
		if (pCar0 == pCar1)
			return;

		final float MAX_DIST = 128;
		if (Math.abs(pCar1.x - pCar0.x) <= MAX_DIST && Math.abs(pCar1.y - pCar0.y) <= MAX_DIST) {
			// More acc. dist check
			float dist = (float) Math.sqrt((pCar0.x - pCar1.x) * (pCar0.x - pCar1.x) + (pCar0.y - pCar1.y) * (pCar0.y - pCar1.y));

			if (dist < pCar0.radius + pCar1.radius) {
				float lAngle = (float) Math.atan2(pCar1.y - pCar0.y, pCar1.x - pCar0.x);
				float lForce = 50f;
				float lRepelPower = (pCar1.radius + pCar0.radius - dist) / (pCar1.radius + pCar0.radius);

				ParticleController pc = mParticlesController.getParticleControllerByName("Sparks");
				if (pc != null) {
					for (int j = 0; j < 16; j++) {

						float dirX = pCar0.x - pCar1.x;
						float dirY = pCar0.y - pCar1.y;

						float a = (float) Math.atan2(dirY, dirX);
						a += (float) Math.toRadians(RandomNumbers.random(0f, 60f) - 60f);

						float lVelX = (float) -Math.cos(a);
						float lVelY = (float) -Math.sin(a);

						pc.particleSystem().spawnParticle(pCar0.x + 16, pCar0.y + 16, lVelX * 0.5f, lVelY * 0.5f, 350f, 32, 0, 32, 32, 16);

					}

				}

				pCar0.x -= Math.cos(lAngle) * lRepelPower * lForce;
				pCar0.y -= Math.sin(lAngle) * lRepelPower * lForce;

				pCar1.x += Math.cos(lAngle) * lRepelPower * lForce;
				pCar1.y += Math.sin(lAngle) * lRepelPower * lForce;

				pCar0.speed *= RandomNumbers.random(0.5f, 0.6f);
				pCar1.speed *= RandomNumbers.random(0.93f, 0.97f);

				if (!pCar0.mPlayerControlled) {
					pCar0.steerAngle = -Car.CAR_TURN_ANGLE_INC;
					pCar0.heading -= Car.CAR_TURN_ANGLE_INC;

				}else {
					if(mCameraShakeController != null)
						mCameraShakeController.shake(200f, 10f);
				}

				if (!pCar1.mPlayerControlled) {
					pCar1.steerAngle = Car.CAR_TURN_ANGLE_INC;
					pCar1.heading += Car.CAR_TURN_ANGLE_INC;

				}else {
					if(mCameraShakeController != null)
						mCameraShakeController.shake(300f, 20f);
				}

				pCar0.mHealth -= 0.5f * (pCar0.speed / pCar0.carSpeedMax) * 2f;
				pCar1.mHealth -= 0.5f * (pCar1.speed / pCar1.carSpeedMax) * 2f;

			}

		}
	}

	// https://stackoverflow.com/questions/13053061/circle-line-intersection-points
	public static float getCircleLineIntersectionPoint(Vector2f pointA, Vector2f pointB, Vector2f center, float radius) {
		float baX = pointB.x - pointA.x;
		float baY = pointB.y - pointA.y;
		float caX = center.x - pointA.x;
		float caY = center.y - pointA.y;

		float a = baX * baX + baY * baY;
		float bBy2 = baX * caX + baY * caY;
		float c = caX * caX + caY * caY - radius * radius;

		float pBy2 = bBy2 / a;
		float q = c / a;

		return pBy2 * pBy2 - q;

		// float disc = pBy2 * pBy2 - q;
		// if (disc < 0) {
		// return null;
		// }
		//
		// // if disc == 0 ... dealt with later
		// float tmpSqrt = (float) Math.sqrt(disc);
		// float abScalingFactor1 = -pBy2 + tmpSqrt;
		// float abScalingFactor2 = -pBy2 - tmpSqrt;
		//
		// Vector2f p1 = new Vector2f(pointA.x - baX * abScalingFactor1, pointA.y - baY * abScalingFactor1);
		// if (disc == 0) { // abScalingFactor1 == abScalingFactor2
		// return Collections.singletonList(p1);
		// }
		// Vector2f p2 = new Vector2f(pointA.x - baX * abScalingFactor2, pointA.y - baY * abScalingFactor2);
		//
		// return Arrays.asList(p1, p2);
	}

}
