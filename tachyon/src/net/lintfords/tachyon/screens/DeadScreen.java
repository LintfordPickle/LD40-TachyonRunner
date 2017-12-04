package net.lintfords.tachyon.screens;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.ListLayout;
import net.lintford.library.screenmanager.screens.LoadingScreen;

public class DeadScreen extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SCREEN_TITLE = "Destroyed";

	public static final int BUTTON_ID_RESTART = 0;
	public static final int BUTTON_ID_EXIT = 1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DeadScreen(ScreenManager pScreenManager) {
		super(pScreenManager, SCREEN_TITLE);

		ListLayout lButtonLayout = new ListLayout(this);

		MenuEntry lResumeButton = new MenuEntry(mScreenManager, this, "Restart");
		MenuEntry lExitButton = new MenuEntry(mScreenManager, this, "Exit");

		lResumeButton.registerClickListener(this, BUTTON_ID_RESTART);
		lExitButton.registerClickListener(this, BUTTON_ID_EXIT);

		// lButtonLayout.menuEntries().add(lResumeButton);
		lButtonLayout.menuEntries().add(lExitButton);

		layouts().add(lButtonLayout);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_ID_RESTART:

			return;

		case BUTTON_ID_EXIT:
			LoadingScreen.load(mScreenManager, false, new MenuBackgroundScreen(mScreenManager), new MainMenuScreen(mScreenManager));
			return;
		}

	}

}
