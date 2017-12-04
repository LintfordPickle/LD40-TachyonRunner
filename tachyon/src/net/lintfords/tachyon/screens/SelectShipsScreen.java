package net.lintfords.tachyon.screens;

import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.entries.MenuSliderEntry;
import net.lintford.library.screenmanager.layouts.ListLayout;
import net.lintford.library.screenmanager.screens.LoadingScreen;

public class SelectShipsScreen extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SCREEN_TITLE = "";

	public static final int BUTTON_ID_START = 0;
	public static final int BUTTON_ID_BACK = 1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private MenuSliderEntry mLapsCountEntry;
	private MenuSliderEntry mShipCountEntry;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public SelectShipsScreen(ScreenManager pScreenManager) {
		super(pScreenManager, SCREEN_TITLE);

		ListLayout lMenuButtons = new ListLayout(this);

		MenuEntry lStartGameButton = new MenuEntry(mScreenManager, this, "Start Game");
		mLapsCountEntry = new MenuSliderEntry(mScreenManager, this);
		mShipCountEntry = new MenuSliderEntry(mScreenManager, this);
		MenuEntry lExitButton = new MenuEntry(mScreenManager, this, "Back");

		MenuEntry lSeperator = new MenuEntry(mScreenManager, this, "");
		lSeperator.active(false);

		lStartGameButton.registerClickListener(this, BUTTON_ID_START);
		lExitButton.registerClickListener(this, BUTTON_ID_BACK);

		lMenuButtons.menuEntries().add(mLapsCountEntry);
		lMenuButtons.menuEntries().add(mShipCountEntry);
		lMenuButtons.menuEntries().add(lStartGameButton);
		lMenuButtons.menuEntries().add(lSeperator);
		lMenuButtons.menuEntries().add(lExitButton);

		mShipCountEntry.label("Ships: ");
		mShipCountEntry.setBounds(4, 24, 1);
		mShipCountEntry.setValue(12);
		
		mLapsCountEntry.label("Laps: ");
		mLapsCountEntry.setBounds(2, 8, 1);
		mLapsCountEntry.setValue(4);

		layouts().add(lMenuButtons);

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
			System.out.println("Starting new game with " + mShipCountEntry.getCurrentValue() + " ships");
			LoadingScreen.load(mScreenManager, true, new GameScreen(mScreenManager, mLapsCountEntry.getCurrentValue(), mShipCountEntry.getCurrentValue()));
			break;

		case BUTTON_ID_BACK:
			exitScreen();
			break;

		}

	}

}
