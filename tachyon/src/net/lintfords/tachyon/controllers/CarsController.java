package net.lintfords.tachyon.controllers;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.camera.CameraShakeController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.audio.AudioManager;
import net.lintford.library.core.graphics.particles.Particle;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.maths.RandomNumbers;
import net.lintford.library.core.maths.Vector2f;
import net.lintfords.tachyon.data.Car;
import net.lintfords.tachyon.data.CarManager;
import net.lintfords.tachyon.renderers.CarsRenderer;

public class CarsController extends BaseController {

	public static final String CONTROLLER_NAME = "CarsController";

	public static final float SPARK_TIMER_WAIT = 15; // ms

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

	AudioManager lAM;

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
			if (pCore.input().keyDown(GLFW.GLFW_KEY_W) && !lPlayerCar.handBrakeOn) {
				lPlayerCar.speed += Car.CAR_ACCELERATION;

			}

			if (pCore.input().keyDown(GLFW.GLFW_KEY_S)) {
				lPlayerCar.speed -= Car.CAR_ACCELERATION * 0.7f;
			}

			if (pCore.input().keyDown(GLFW.GLFW_KEY_A)) {
				lPlayerCar.steerAngle += -lPlayerCar.carTurnAngleInc;
				isSteering = true;
			}

			if (pCore.input().keyDown(GLFW.GLFW_KEY_D)) {
				lPlayerCar.steerAngle += lPlayerCar.carTurnAngleInc;
				isSteering = true;
			}

			if (!isSteering)
				lPlayerCar.steerAngle *= 0.94f;

			if (pCore.input().keyDown(GLFW.GLFW_KEY_SPACE)) {
				if (lPlayerCar.speed > 0)
					lPlayerCar.speed -= Car.CAR_ACCELERATION * 0.7f;
				if (lPlayerCar.speed < 0)
					lPlayerCar.speed += Car.CAR_ACCELERATION * 0.7f;

				lPlayerCar.handBrakeOn = true;

			} else {
				lPlayerCar.handBrakeOn = false;
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
				updateAI(pCore, lCar);

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
		int lIDFOund = mTrackController.getClosestTrackPointID(pCar.x, pCar.y);
		final int pNextTrackPointID = (lIDFOund + 2) % NUM_TRACK_POINTS;
		final int pNextTrackPointIDPlusOne = (lIDFOund + 3) % NUM_TRACK_POINTS;

		Vector2f pNextControlPoint = mTrackController.track().getTrackPoint(pNextTrackPointID);
		Vector2f pNextControlPointPlusOne = mTrackController.track().getTrackPoint(pNextTrackPointIDPlusOne);

		Vector2f trackDirection = new Vector2f();
		trackDirection.x = pNextControlPointPlusOne.x - pNextControlPoint.x;
		trackDirection.y = pNextControlPointPlusOne.y - pNextControlPoint.y;
		trackDirection.nor();

		float tempX = trackDirection.x;
		trackDirection.x = -trackDirection.y;
		trackDirection.y = tempX;

		float offX = trackDirection.x * RandomNumbers.random(-mTrackController.track().segmentWidth / 2f, mTrackController.track().segmentWidth / 2f);
		float offY = trackDirection.y * RandomNumbers.random(-mTrackController.track().segmentWidth / 2f, mTrackController.track().segmentWidth / 2f);

		// The amount of throttle and turn speed to be applied will depend on
		// the Fresnel term (faster if facing the target)
		final float lHeadingVecX = (pNextControlPoint.x + offX) - pCar.x;
		final float lHeadingVecY = (pNextControlPoint.y + offY) - pCar.y;

		float dist = (float) Math.sqrt((lHeadingVecX * lHeadingVecX) + (lHeadingVecY * lHeadingVecY));

		// Check if the vehicle has reached the destination
		if (dist > 64) {

			float lHeadingAngle = turnToFace(lHeadingVecX, lHeadingVecY, pCar.heading, pCar.carTurnAngleInc * 3f);

			pCar.steerAngle += lHeadingAngle;
			pCar.isSteering = true;

			pCar.speed += Car.CAR_ACCELERATION;

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

		pCar.sparkTimer += pCore.time().elapseGameTimeMilli();
		pCar.bangTimer += pCore.time().elapseGameTimeMilli();
		pCar.clangTimer += pCore.time().elapseGameTimeMilli();
		pCar.smokeTimer += pCore.time().elapseGameTimeMilli();

		pCar.dx += (float) Math.cos(pCar.heading + pCar.steerAngle) * (pCar.speed / pCar.carSpeedMax) * .1f;
		pCar.dy += (float) Math.sin(pCar.heading + pCar.steerAngle) * (pCar.speed / pCar.carSpeedMax) * .1f;

		pCar.dr += pCar.steerAngle / 10f * (pCar.speed / pCar.carSpeedMax);

		pCar.dx = MathHelper.clamp(pCar.dx, -2f, 2f);
		pCar.dy = MathHelper.clamp(pCar.dy, -2f, 2f);

		pCar.wheelBase = 32;
		pCar.speed = MathHelper.clamp(pCar.speed, -pCar.carSpeedMax * 0.5f, pCar.carSpeedMax);
		pCar.steerAngle = MathHelper.clamp(pCar.steerAngle, -pCar.carTurnAngleMax, pCar.carTurnAngleMax);

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
		if (pc != null && Math.abs(pCar.speed) > 5) {
			float lVelX = (float) -Math.cos(pCar.heading);
			float lVelY = (float) -Math.sin(pCar.heading);
			Particle p = pc.particleSystem().spawnParticle(pCar.x + 16 + lVelX * 75f, pCar.y +16 + lVelY * 75f, lVelX * pCar.speed * 0.0005f, lVelY * pCar.speed * 0.0005f, 400f, 0, 0, 32, 32, 16);
			if(p != null) {
				p.r = pCar.r;
				p.g = pCar.g;
				p.b = pCar.b;
				p.radius = 32;
				
			}

		}

		pCar.x += pCar.dx * pCore.time().elapseGameTimeMilli();
		pCar.y += pCar.dy * pCore.time().elapseGameTimeMilli();

		if (pCar.handBrakeOn) {
			pCar.heading += pCar.dr;
		}

		if (pCar.handBrakeOn)
			pCar.dr *= 0.91f;
		else
			pCar.dr *= 0.7f;

		pCar.dx *= 0.96f;
		pCar.dy *= 0.96f;
		
		if(pCar.mHealth < pCar.mMaxHealth / 2) {
			if(pCar.mPlayerControlled && pCar.mHealth <=  0) {
				if(pCar.smokeTimer > 10) {
					pCar.smokeTimer = 0;
					ParticleController smokePartSys = mParticlesController.getParticleControllerByName("SmokeParticleSystem");
					if (pc != null && pCar.sparkTimer > SPARK_TIMER_WAIT) {
						pCar.sparkTimer = 0;
						
						float lVelX = (float) -Math.cos(pCar.heading);
						float lVelY = (float) -Math.sin(pCar.heading);
						
						smokePartSys.particleSystem().spawnParticle(pCar.x + 75, pCar.y , lVelX * 0.5f, lVelY * 0.5f, 300f);
						
					}
					
				}
			}
			
			if(pCar.smokeTimer > 100) {
				pCar.smokeTimer = 0;
				ParticleController smokePartSys = mParticlesController.getParticleControllerByName("SmokeParticleSystem");
				if (pc != null && pCar.sparkTimer > SPARK_TIMER_WAIT) {
					pCar.sparkTimer = 0;
					
					float lVelX = (float) -Math.cos(pCar.heading);
					float lVelY = (float) -Math.sin(pCar.heading);
					
					smokePartSys.particleSystem().spawnParticle(pCar.x + 75, pCar.y , lVelX * 0.5f, lVelY * 0.5f, 300f);
					
				}
				
			}
		
			
			
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

		if (pCar.mPlayerControlled || !pCar.mPlayerControlled) {
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
				pCar.steerAngle = -pCar.carTurnAngleMax * 0.1f;
				pCar.dr = 0;
				pCar.dx = 0;
				pCar.dy = 0;
				pCar.mHealth -= 0.7f * (pCar.speed / pCar.carSpeedMax) * 2f;

				mCarRenderer.playExplosion(pCar.x, pCar.y);

				if (pCar.mPlayerControlled) {
					if (mCameraShakeController != null)
						mCameraShakeController.shake(300f, 20f);
				}

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
				pCar.steerAngle = pCar.carTurnAngleMax * 0.1f;
				pCar.dr = 0;
				pCar.dx = 0;
				pCar.dy = 0;

				mCarRenderer.playExplosion(pCar.x, pCar.y);

				pCar.mHealth -= 0.7f * (pCar.speed / pCar.carSpeedMax) * 2f;

				if (pCar.mPlayerControlled) {
					if (mCameraShakeController != null)
						mCameraShakeController.shake(300f, 20f);
				}

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
				if (pc != null && pCar0.sparkTimer > SPARK_TIMER_WAIT) {
					pCar0.sparkTimer = 0;

					float dirX = pCar0.x - pCar1.x;
					float dirY = pCar0.y - pCar1.y;

					float a = (float) Math.atan2(dirY, dirX);
					a += (float) Math.toRadians(RandomNumbers.random(0f, 60f) - 60f);

					float lVelX = (float) -Math.cos(a);
					float lVelY = (float) -Math.sin(a);

					pc.particleSystem().spawnParticle(pCar0.x + 16, pCar0.y + 16, lVelX * 0.5f, lVelY * 0.5f, 700f, 32, 0, 32, 32, 16);

				}

				pCar0.x -= Math.cos(lAngle) * lRepelPower * lForce;
				pCar0.y -= Math.sin(lAngle) * lRepelPower * lForce;

				pCar1.x += Math.cos(lAngle) * lRepelPower * lForce;
				pCar1.y += Math.sin(lAngle) * lRepelPower * lForce;

				pCar0.speed *= RandomNumbers.random(0.5f, 0.6f);
				pCar1.speed *= RandomNumbers.random(0.93f, 0.97f);

				if (!pCar0.mPlayerControlled) {
					pCar0.steerAngle = -pCar0.carTurnAngleInc;
					pCar0.heading -= pCar0.carTurnAngleInc;

				} else {
					if (mCameraShakeController != null)
						mCameraShakeController.shake(200f, 10f);
				}

				if (!pCar1.mPlayerControlled) {
					pCar1.steerAngle = pCar1.carTurnAngleInc;
					pCar1.heading += pCar1.carTurnAngleInc;

				} else {
					if (mCameraShakeController != null)
						mCameraShakeController.shake(300f, 20f);
				}

				pCar0.mHealth -= 0.4f * (pCar0.speed / pCar0.carSpeedMax) * 1.6f;
				pCar1.mHealth -= 0.4f * (pCar1.speed / pCar1.carSpeedMax) * 1.6f;

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

	}

}
