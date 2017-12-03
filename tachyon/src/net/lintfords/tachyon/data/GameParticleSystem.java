package net.lintfords.tachyon.data;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.graphics.particles.ParticleSystem;
import net.lintford.library.data.BaseData;

public class GameParticleSystem extends BaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7527675184687190438L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<ParticleSystem> mParticleSystems;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns the number of {@link ParticleSystem}s in this {@link GameParticleSystem} instance. */
	public int getNumParticleSystems() {
		return mParticleSystems.size();
	}

	public List<ParticleSystem> particleSystems() {
		return mParticleSystems;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GameParticleSystem() {
		mParticleSystems = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initalise() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Adds the given {@link ParticleSystem} to the {@link GameParticleSystem}. */
	public boolean addParticleSystem(final ParticleSystem pParticleSystem) {
		if (pParticleSystem == null) {
			return false;
		}

		if (mParticleSystems.contains(pParticleSystem)) {
			return false;
		}

		mParticleSystems.add(pParticleSystem);

		return true;
	}

	/** Returns the {@link ParticleSystem} whose name matches the {@link String} given. null is returned if the {@link ParticleSystem} cannot be found. */
	public ParticleSystem getParticleSystem(String pSystemName) {
		final int PARTICLE_SYS_COUNT = mParticleSystems.size();
		for (int i = 0; i < PARTICLE_SYS_COUNT; i++) {
			if (mParticleSystems.get(i) == null)
				continue;
			if (mParticleSystems.get(i).name() == null)
				continue;

			if (mParticleSystems.get(i).name().equals(pSystemName)) {
				return mParticleSystems.get(i);

			}

		}

		return null;

	}

}
