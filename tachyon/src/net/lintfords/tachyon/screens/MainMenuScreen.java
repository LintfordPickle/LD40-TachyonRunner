package net.lintfords.tachyon.screens;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.ListLayout;

public class MainMenuScreen extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SCREEN_TITLE = "";

	public static final int BUTTON_ID_START = 0;
	public static final int BUTTON_ID_CREDITS = 1;
	public static final int BUTTON_ID_EXIT = 2;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MainMenuScreen(ScreenManager pScreenManager) {
		super(pScreenManager, SCREEN_TITLE);

		ListLayout lMenuButtons = new ListLayout(this);

		MenuEntry lStartGameButton = new MenuEntry(mScreenManager, this, "Play");
		MenuEntry lCreditsButton = new MenuEntry(mScreenManager, this, "Credits");
		MenuEntry lExitButton = new MenuEntry(mScreenManager, this, "Exit");

		lStartGameButton.registerClickListener(this, BUTTON_ID_START);
		lCreditsButton.registerClickListener(this, BUTTON_ID_CREDITS);
		lExitButton.registerClickListener(this, BUTTON_ID_EXIT);

		lMenuButtons.menuEntries().add(lStartGameButton);
//		lMenuButtons.menuEntries().add(lCreditsButton);
		lMenuButtons.menuEntries().add(lExitButton);

		layouts().add(lMenuButtons);
		
		// mIsPopup = true;
		mESCBackEnabled = false;

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
		case BUTTON_ID_START:
			// LoadingScreen.load(mScreenManager, true, new GameScreen(mScreenManager));
			mScreenManager.addScreen(new SelectShipsScreen(mScreenManager));
			break;
		case BUTTON_ID_CREDITS:
			mScreenManager.addScreen(new CreditsScreen(mScreenManager, "Credits"));
			break;
		case BUTTON_ID_EXIT:
			mScreenManager.exitGame();
			break;
		}

	}

}
