package net.lintfords.tachyon.renderers;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.maths.Rectangle;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;
import net.lintfords.tachyon.controllers.GameController;
import net.lintfords.tachyon.data.GameState;

public class GameStateRenderer extends BaseRenderer {

	public static final String RENDERER_NAME = "GameStateRenderer";

	public static final String UI_FONT_PATHNAME = "res/fonts/retroville.ttf";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private FontUnit mUIFont;
	private FontUnit mUIFontScore;

	private TextureBatch mTextureBatch;
	private GameController mGameController;

	private UIBarCustom mPlayerHealth;

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GameStateRenderer(RendererManager pRendererManager, int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);

		ControllerManager lControllerManager = pRendererManager.core().controllerManager();
		mGameController = (GameController) lControllerManager.getControllerByNameRequired(GameController.CONTROLLER_NAME);

		mTextureBatch = new TextureBatch();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mUIFont = pResourceManager.fontManager().loadNewFont("UIFont", UI_FONT_PATHNAME, 24, true);
		mUIFontScore = pResourceManager.fontManager().loadNewFont("UIFontBig", UI_FONT_PATHNAME, 48, true);

		mTextureBatch.loadGLContent(pResourceManager);
		TextureManager.textureManager().loadTexture("UITexture", "res/textures/ui.png");

		mPlayerHealth = new UIBarCustom(0, 100, "UITexture");
		mPlayerHealth.setColor(22f, 1f, 1f, 0.8f);
		mPlayerHealth.setDestRectangle(0, 0, 100, 25);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

	}

	@Override
	public void draw(LintfordCore pCore) {

		if (mGameController.playerFinished() || !mGameController.playerStillAlive()) {
			GameState lGameState = mGameController.gameState();
			Rectangle lHUDArea = pCore.HUD().boundingRectangle();
			
			float posPosWidth = mUIFont.bitmap().getStringWidth("POS  ");
			float actPosWidth = mUIFont.bitmap().getStringWidth(" " + lGameState.playerPosition);
			float ofWidth = mUIFont.bitmap().getStringWidth("OF ");

			mUIFont.begin(pCore.HUD());
			mUIFont.draw("POS", lHUDArea.left() + 5, lHUDArea.bottom() - 5 - 50, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);
			mUIFont.draw("" + lGameState.playerPosition, lHUDArea.left() + posPosWidth + 5, lHUDArea.bottom() - 5 - 50, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);
			mUIFont.draw("OF", lHUDArea.left() + posPosWidth + 5 + actPosWidth + 5, lHUDArea.bottom() - 5 - 50, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);
			mUIFont.draw("" + lGameState.totalRacers, lHUDArea.left() + posPosWidth + 5 + actPosWidth + 5 + ofWidth + 5, lHUDArea.bottom() - 5 - 50, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);
			mUIFont.end();
			
			return;

		}

		renderPositionInfo(pCore);
		renderLapInfo(pCore);
		renderHealthInfo(pCore);

		// Count down timer
		if (!mGameController.hasRaceStarted()) {
			float lTextWidth = mUIFontScore.bitmap().getStringWidth("READY!");
			float lScale = 2f - mGameController.countDownTimer() / GameController.COUNT_DOWN_INC_TIME;
			float lNumberWidth = mUIFontScore.bitmap().getStringWidth("" + mGameController.countDownTime(), lScale);
			float lNumberHeight = mUIFontScore.bitmap().getStringHeight("" + mGameController.countDownTime(), lScale);
			mUIFontScore.begin(pCore.HUD());
			mUIFontScore.draw("READY!", 0 - lTextWidth / 2, -100, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);
			mUIFontScore.draw("" + mGameController.countDownTime(), 0 - lNumberWidth / 2, 0 - lNumberHeight / 2, -0.1f, 1f, 1f, 1f, 1f, lScale, -1);
			mUIFontScore.end();

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void renderPositionInfo(LintfordCore pCore) {
		GameState lGameState = mGameController.gameState();
		Rectangle lHUDArea = pCore.HUD().boundingRectangle();

		float actPosWidth = mUIFont.bitmap().getStringWidth(lGameState.playerPosition + "");
		float ofWidth = mUIFont.bitmap().getStringWidth("OF");

		mUIFont.begin(pCore.HUD());
		mUIFont.draw("POS", lHUDArea.left() + 5, lHUDArea.top() + 5, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);
		mUIFont.draw("" + lGameState.playerPosition, lHUDArea.left() + 5, lHUDArea.top() + 35, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);
		mUIFont.draw("OF", lHUDArea.left() + 5 + actPosWidth + 5, lHUDArea.top() + 35, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);
		mUIFont.draw("" + lGameState.totalRacers, lHUDArea.left() + 5 + actPosWidth + 5 + ofWidth + 5, lHUDArea.top() + 35, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);
		mUIFont.end();
	}

	private void renderLapInfo(LintfordCore pCore) {
		GameState lGameState = mGameController.gameState();
		Rectangle lHUDArea = pCore.HUD().boundingRectangle();

		float laptextWidth = mUIFont.bitmap().getStringWidth("LAP");
		float actLapWidth = mUIFont.bitmap().getStringWidth(lGameState.currentLap + "");
		float ofWidth = mUIFont.bitmap().getStringWidth("OF");
		float numLapWidth = mUIFont.bitmap().getStringWidth(lGameState.totalLaps + "");

		float actLapWidth1 = mUIFont.bitmap().getStringWidth("" + lGameState.currentTrackPoint);

		mUIFont.begin(pCore.HUD());
		mUIFont.draw("LAP", lHUDArea.right() - 5 - laptextWidth, lHUDArea.top() + 5, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);
		mUIFont.draw("" + lGameState.currentLap, lHUDArea.right() - 5 - actLapWidth - 5 - ofWidth - 5 - numLapWidth - 5, lHUDArea.top() + 35, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);
		mUIFont.draw("OF", lHUDArea.right() - 5 - actLapWidth - 5 - ofWidth - 5, lHUDArea.top() + 35, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);
		mUIFont.draw("" + lGameState.totalLaps, lHUDArea.right() - 5 - numLapWidth - 5, lHUDArea.top() + 35, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);

		mUIFont.draw("" + lGameState.currentTrackPoint, lHUDArea.right() - 5 - actLapWidth1, lHUDArea.top() + 65, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);

		mUIFont.end();

	}

	private void renderHealthInfo(LintfordCore pCore) {
		GameState lGameState = mGameController.gameState();
		Rectangle lHUDArea = pCore.HUD().boundingRectangle();

		mUIFont.begin(pCore.HUD());
		mUIFont.draw("Health:", lHUDArea.left() + 5, lHUDArea.bottom() - 32 - 5 - 32, -0.1f, 1f, 1f, 1f, 1f, 1f, -1);
		mUIFont.end();

		mPlayerHealth.setColor(0.86f, 0.2f, 0.17f, 0.8f);
		mPlayerHealth.setDestRectangle(lHUDArea.left() + 5, lHUDArea.bottom() - 32 - 5, 100, 32);
		mPlayerHealth.setCurrentValue(lGameState.playerHealth);
		mPlayerHealth.setMinMax(0, lGameState.playerHealthMax);
		mPlayerHealth.draw(pCore, mTextureBatch);

	}

}
