package net.lintfords.tachyon.controllers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.ParticleSystem;
import net.lintford.library.renderers.particles.ParticleRenderer;

public class ParticleController {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ParticleRenderer mParticleRenderer;
	private ParticleSystem mParticleSystem;
	private boolean mIsAssigned;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ParticleSystem particleSystem() {
		return mParticleSystem;
	}

	public ParticleRenderer particleRenderer() {
		return mParticleRenderer;
	}

	public boolean isAssigned() {
		return mIsAssigned;
	}

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isInitialised() {
		return true;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleController() {
		mIsAssigned = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {

	}

	public void update(LintfordCore pCore) {
		mParticleSystem.update(pCore);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void assignParticleSystem(final ParticleSystem pParticleSystem) {
		mParticleSystem = pParticleSystem;
		mIsAssigned = mParticleSystem != null;

	}

	public void unassign() {
		mParticleSystem = null;
		mIsAssigned = false;

	}

	public void setParticleRenderer(final ParticleRenderer pParticleRenderer) {
		mParticleRenderer = pParticleRenderer;

	}

}
