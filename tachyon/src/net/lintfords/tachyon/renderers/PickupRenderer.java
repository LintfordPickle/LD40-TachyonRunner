package net.lintfords.tachyon.renderers;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;
import net.lintfords.tachyon.controllers.WeaponController;

public class PickupRenderer extends BaseRenderer {

	public static final String RENDERER_NAME = "WeaponsRenderer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private WeaponController mWeaponController;

	private TextureBatch mTextureBatch;
	private Texture mTexture;

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PickupRenderer(RendererManager pRendererManager, int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);

		ControllerManager lControllerManager = pRendererManager.core().controllerManager();
		mWeaponController = (WeaponController) lControllerManager.getControllerByNameRequired(WeaponController.CONTROLLER_NAME);

		mTextureBatch = new TextureBatch();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mTextureBatch.loadGLContent(pResourceManager);
		mTexture = TextureManager.textureManager().loadTexture("Pickup", "res/textures/pickups.png");

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mTextureBatch.unloadGLContent();

	}

	@Override
	public void draw(LintfordCore pCore) {
		// TODO Auto-generated method stub

	}

	// --------------------------------------
	// Methods
	// --------------------------------------
}
