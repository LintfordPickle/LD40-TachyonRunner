package net.lintfords.tachyon;

import org.lwjgl.opengl.GL11;

import net.lintford.library.GameInfo;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintfords.tachyon.screens.MainMenuScreen;
import net.lintfords.tachyon.screens.MenuBackgroundScreen;

public class BaseGame extends LintfordCore {

	ScreenManager mScreenManager;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseGame(GameInfo pGameInfo) {
		super(pGameInfo);

	}

	@Override
	protected void onInitialiseApp() {
		super.onInitialiseApp();

		TextureManager.textureManager().loadTexture(ScreenManager.SCREENMANAGER_TEXTURE_NAME, "res/textures/screenmanager.png");

		mScreenManager = new ScreenManager(this);
		mScreenManager.initialise("res/fonts/nasalization.ttf");

		mScreenManager.addScreen(new MenuBackgroundScreen(mScreenManager));
		mScreenManager.addScreen(new MainMenuScreen(mScreenManager));

	}

	@Override
	protected void onInitialiseGL() {
		super.onInitialiseGL();

		// Enable depth testing
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// Enable depth testing
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);

		// Set the clear color to corn flower blue
		GL11.glClearColor(29.0f / 255.0f, 29.0f / 255.0f, 56.0f / 255.0f, 1.0f);

	}

	@Override
	protected void onLoadGLContent() {
		super.onLoadGLContent();

		mScreenManager.loadGLContent(mResourceManager);

	}

	@Override
	protected void onUnloadGLContent() {
		super.onUnloadGLContent();

		mScreenManager.unloadGLContent();

	}

	@Override
	protected void onHandleInput() {
		super.onHandleInput();

		mScreenManager.handleInput(this);

	}

	@Override
	protected void onUpdate() {
		super.onUpdate();

		mScreenManager.update(this);

	}

	@Override
	protected void onDraw() {
		// Clear the depth buffer and color buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		super.onDraw();

		mScreenManager.draw(this);

	}

	// --------------------------------------
	// Entry-Point
	// --------------------------------------

	public static void main(String[] args) {
		GameInfo lGameInfo = new GameInfo() {
			@Override
			public String applicationName() {
				return "Tachyon Razor";
			}

			@Override
			public String windowTitle() {
				return applicationName();
			}
		};

		BaseGame lGameWindow = new BaseGame(lGameInfo);
		lGameWindow.createWindow();

	}

}
