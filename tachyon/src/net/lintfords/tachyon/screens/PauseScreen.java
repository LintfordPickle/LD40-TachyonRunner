package net.lintfords.tachyon.screens;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.ListLayout;
import net.lintford.library.screenmanager.screens.LoadingScreen;

public class PauseScreen extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SCREEN_TITLE = "Paused";

	public static final int BUTTON_ID_RESUME = 0;
	public static final int BUTTON_ID_RESTART = 1;
	public static final int BUTTON_ID_EXIT = 2;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PauseScreen(ScreenManager pScreenManager) {
		super(pScreenManager, SCREEN_TITLE);

		ListLayout lButtonLayout = new ListLayout(this);

		MenuEntry lResumeButton = new MenuEntry(mScreenManager, this, "Resume");
		MenuEntry lRestartButton = new MenuEntry(mScreenManager, this, "Restart");
		MenuEntry lExitButton = new MenuEntry(mScreenManager, this, "Exit");

		lResumeButton.registerClickListener(this, BUTTON_ID_RESUME);
		lRestartButton.registerClickListener(this, BUTTON_ID_RESTART);
		lExitButton.registerClickListener(this, BUTTON_ID_EXIT);

		lButtonLayout.menuEntries().add(lResumeButton);
		lButtonLayout.menuEntries().add(lExitButton);

		layouts().add(lButtonLayout);

		// Show game below
		mIsPopup = true;

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
		case BUTTON_ID_RESUME:
			exitScreen();
			return;

		case BUTTON_ID_EXIT:
			LoadingScreen.load(mScreenManager, false, new MenuBackgroundScreen(mScreenManager), new MainMenuScreen(mScreenManager));
			return;
			
		}

	}

}
