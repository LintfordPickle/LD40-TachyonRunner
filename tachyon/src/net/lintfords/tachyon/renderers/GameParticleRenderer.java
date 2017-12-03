package net.lintfords.tachyon.renderers;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.renderers.particles.ParticleRenderer;

public class GameParticleRenderer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "GameParticleRenderer";

	private static final int RENDERER_POOL_SIZE = 32;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<ParticleRenderer> mParticleRenderers;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int ZDepth() {
		return -1;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GameParticleRenderer(final RendererManager pRendererManager, final int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);

		mParticleRenderers = new ArrayList<>();

		// Fill the pool
		for (int i = 0; i < RENDERER_POOL_SIZE; i++) {
			mParticleRenderers.add(new ParticleRenderer());
		}

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		for (int i = 0; i < RENDERER_POOL_SIZE; i++) {
			mParticleRenderers.get(i).loadGLContent(pResourceManager);

		}

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		for (int i = 0; i < RENDERER_POOL_SIZE; i++) {
			mParticleRenderers.get(i).unloadGLContent();

		}

	}

	@Override
	public void draw(LintfordCore pCore) {
		// TODO Auto-generated method stub
		final int NUM_PARTICLE_RENDERERS = mParticleRenderers.size();
		for (int i = 0; i < NUM_PARTICLE_RENDERERS; i++) {
			mParticleRenderers.get(i).draw(pCore);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Returns an unassigned {@link ParticleRenderer}. null is returned if there are no unassigned particle renderers remaining and the system resources do not allow the pool to be expanded. */
	public ParticleRenderer getFreeParticleSystemRenderer() {
		final int PARTICLE_RENDERER_COUNT = mParticleRenderers.size();
		for (int i = 0; i < PARTICLE_RENDERER_COUNT; i++) {
			if (!mParticleRenderers.get(i).isAssigned()) {
				return mParticleRenderers.get(i);
			}
		}

		// TODO: Before returning null, we need to check if it is possible to expand the PARTICLE_SYSTEM_RENDERER pool.
		return null;
	}

}
