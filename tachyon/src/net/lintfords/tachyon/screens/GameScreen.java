package net.lintfords.tachyon.screens;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.screens.BaseGameScreen;

public class GameScreen extends BaseGameScreen {

	// --------------------------------------
	// Variables
	// --------------------------------------

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GameScreen(ScreenManager pScreenManager) {
		super(pScreenManager);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void handleInput(LintfordCore pCore, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pCore, pAcceptMouse, pAcceptKeyboard);

		if (pCore.input().keyDown(GLFW.GLFW_KEY_P) || pCore.input().keyDown(GLFW.GLFW_KEY_ESCAPE)) {
			mScreenManager.addScreen(new PauseScreen(mScreenManager));
			return;
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
