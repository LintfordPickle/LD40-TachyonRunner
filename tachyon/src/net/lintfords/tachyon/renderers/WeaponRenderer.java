package net.lintfords.tachyon.renderers;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;
import net.lintfords.tachyon.controllers.WeaponController;

public class WeaponRenderer extends BaseRenderer {

	public static final String RENDERER_NAME = "WeaponsRenderer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private WeaponController mWeaponController;

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public WeaponRenderer(RendererManager pRendererManager, int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);

		ControllerManager lControllerManager = pRendererManager.core().controllerManager();
		mWeaponController = (WeaponController) lControllerManager.getControllerByNameRequired(WeaponController.CONTROLLER_NAME);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

	}

	@Override
	public void draw(LintfordCore pCore) {
		// TODO Auto-generated method stub

	}

	// --------------------------------------
	// Methods
	// --------------------------------------
}
