package net.lintfords.tachyon.controllers;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.core.RendererController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.ParticleSystem;
import net.lintford.library.core.graphics.particles.initialisers.ParticleDestinationRegionInitialiser;
import net.lintford.library.core.graphics.particles.initialisers.ParticleRandomSizeInitialiser;
import net.lintford.library.core.graphics.particles.initialisers.ParticleSourceRegionInitialiser;
import net.lintford.library.core.graphics.particles.initialisers.ParticleTurnToFaceInitialiser;
import net.lintford.library.core.graphics.particles.modifiers.ParticleLifetimeAlphaFadeInOutModifier;
import net.lintford.library.core.graphics.particles.modifiers.ParticleLifetimeAlphaFadeOutModifier;
import net.lintford.library.core.graphics.particles.modifiers.ParticlePhysicsModifier;
import net.lintford.library.renderers.particles.ParticleRenderer;
import net.lintfords.tachyon.data.GameParticleSystem;
import net.lintfords.tachyon.data.ParticleWispMovementModifier;
import net.lintfords.tachyon.renderers.GameParticleRenderer;

public class GameParticlesController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "GameParticleController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private GameParticleSystem mGameParticleSystems;

	protected List<ParticleController> mParticleControllers;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns true if the {@link mGameParticleSystems} has been assigned, false otherwise. */
	@Override
	public boolean isInitialised() {
		return mGameParticleSystems != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GameParticlesController(final ControllerManager pControllerManager, final GameParticleSystem pGameParticles, final int pGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pGroupID);

		mGameParticleSystems = pGameParticles;
		mParticleControllers = new ArrayList<>();

		createTrailParticleSystem();
		createBitsParticleSystem();
		createSparksParticleSystem();
		createWispParticleSystem();
		createDamageParticleSystem();
		
		// Create a ParticleController for each of the ParticleSystems in the GameParticleSystems.
		final int NUM_PARTICLE_SYSTEMS = mGameParticleSystems.getNumParticleSystems();
		for (int i = 0; i < NUM_PARTICLE_SYSTEMS; i++) {
			// Create a new instance of a ParticleController.
			final ParticleController PARTICAL_CONTROLLER = new ParticleController();

			// Assign the ParticleSystem to this controller
			PARTICAL_CONTROLLER.assignParticleSystem(mGameParticleSystems.particleSystems().get(i));

			// Add the controller to the update loop
			mParticleControllers.add(PARTICAL_CONTROLLER);

		}

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(LintfordCore pCore) {
		// Iterate through the current particle controllers and update them
		final int NUM_CONTROLLERS = mParticleControllers.size();
		for (int i = 0; i < NUM_CONTROLLERS; i++) {
			if (mParticleControllers.get(i).isAssigned()) {
				mParticleControllers.get(i).update(pCore);

				// Need to make sure all the particle systems have controllers
				if (mParticleControllers.get(i).particleRenderer() == null) {
					final RendererController RENDERER_CONTROLLER = (RendererController) mControllerManager.getControllerByNameRequired(RendererController.CONTROLLER_NAME);

					final GameParticleRenderer GAME_PARTICAL_RENDERER = (GameParticleRenderer) RENDERER_CONTROLLER.rendererManager().getRenderer(GameParticleRenderer.RENDERER_NAME);
					if (GAME_PARTICAL_RENDERER != null) {
						final ParticleRenderer PARTICAL_RENDERER = GAME_PARTICAL_RENDERER.getFreeParticleSystemRenderer();

						if (PARTICAL_RENDERER != null) {
							// TODO: Get rid of this circular reference in the Particle Renderer/controller/model thing
							mParticleControllers.get(i).setParticleRenderer(PARTICAL_RENDERER);
							PARTICAL_RENDERER.assignParticleSystem(mParticleControllers.get(i).particleSystem());

						}

					}
				}

			}

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void createTrailParticleSystem() {
		final ParticleSystem lTrailParticles = new ParticleSystem("Trails", 256);
		lTrailParticles.initialise("ParticleTexture", "res/textures/particles.png");

		lTrailParticles.addInitialiser(new ParticleTurnToFaceInitialiser());
		lTrailParticles.addModifier(new ParticleLifetimeAlphaFadeOutModifier());
		lTrailParticles.addModifier(new ParticlePhysicsModifier());

		mGameParticleSystems.addParticleSystem(lTrailParticles);

	}

	private void createBitsParticleSystem() {

	}

	private void createSparksParticleSystem() {
		final ParticleSystem lSparks = new ParticleSystem("Sparks", 512);
		lSparks.initialise("ParticleTexture", "res/textures/particles.png");

		lSparks.addInitialiser(new ParticleTurnToFaceInitialiser());

		lSparks.addInitialiser(new ParticleSourceRegionInitialiser(16, 0, 16, 16));
		lSparks.addInitialiser(new ParticleDestinationRegionInitialiser(8f));
		lSparks.addInitialiser(new ParticleRandomSizeInitialiser(1f, 4f));

		lSparks.addModifier(new ParticleLifetimeAlphaFadeOutModifier());
		lSparks.addModifier(new ParticlePhysicsModifier());

		mGameParticleSystems.addParticleSystem(lSparks);

	}

	private void createWispParticleSystem() {
		final ParticleSystem WISP_PARTICLE_SYSTEM = new ParticleSystem("WispParticleSystem", 256);
		WISP_PARTICLE_SYSTEM.initialise("ParticleTexture", "res/textures/particles.png");

		WISP_PARTICLE_SYSTEM.addInitialiser(new ParticleSourceRegionInitialiser(96, 0, 16, 16));
		WISP_PARTICLE_SYSTEM.addInitialiser(new ParticleRandomSizeInitialiser(4f, 6f));

		WISP_PARTICLE_SYSTEM.addModifier(new ParticleLifetimeAlphaFadeInOutModifier());
		WISP_PARTICLE_SYSTEM.addModifier(new ParticleWispMovementModifier());
		WISP_PARTICLE_SYSTEM.addModifier(new ParticlePhysicsModifier());

		mGameParticleSystems.addParticleSystem(WISP_PARTICLE_SYSTEM);

	}
	
	private void createDamageParticleSystem() {
		final ParticleSystem WISP_PARTICLE_SYSTEM = new ParticleSystem("DamageParticleSystem", 256);
		WISP_PARTICLE_SYSTEM.initialise("ParticleTexture", "res/textures/particles.png");

		WISP_PARTICLE_SYSTEM.addInitialiser(new ParticleSourceRegionInitialiser(0, 32, 32, 32));
		WISP_PARTICLE_SYSTEM.addInitialiser(new ParticleRandomSizeInitialiser(4f, 6f));

		WISP_PARTICLE_SYSTEM.addModifier(new ParticleLifetimeAlphaFadeInOutModifier());
		WISP_PARTICLE_SYSTEM.addModifier(new ParticlePhysicsModifier());

		mGameParticleSystems.addParticleSystem(WISP_PARTICLE_SYSTEM);

	}

	/** Returns the {@link ParticleController} whose {@link ParticleSystem}'s name matches the given {@link String}. null is returned if the ParticleController is not found. */
	public ParticleController getParticleControllerByName(final String pParticleSystemName) {
		final int NUM_CONTROLLER = mParticleControllers.size();
		for (int i = 0; i < NUM_CONTROLLER; i++) {
			if (mParticleControllers.get(i).isAssigned()) {
				if (mParticleControllers.get(i).particleSystem().name().equals(pParticleSystemName)) {
					return mParticleControllers.get(i);
				}

			}

		}

		return null;

	}

	/** Returns the {@link ParticleSystem} whose name matches the {@link String} given. null is returned if the {@link ParticleSystem} cannot be found. */
	public ParticleSystem getParticleSystemByName(final String pParticleSystemName) {
		return mGameParticleSystems.getParticleSystem(pParticleSystemName);

	}

}
