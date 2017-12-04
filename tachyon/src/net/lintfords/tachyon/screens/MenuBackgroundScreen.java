package net.lintfords.tachyon.screens;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class MenuBackgroundScreen extends Screen {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private TextureBatch mTextureBatch;
	private Texture mBackgroundtexture;
	
	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuBackgroundScreen(ScreenManager pScreenManager) {
		super(pScreenManager);

		mTextureBatch = new TextureBatch();
		
		mShowInBackground = true;
	
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {

		mTextureBatch.loadGLContent(pResourceManager);
		mBackgroundtexture = TextureManager.textureManager().loadTexture("MenuBackground", "res/textures/menu.png");
		
	}

	@Override
	public void unloadGLContent() {

		mTextureBatch.unloadGLContent();
		
	}
	
	@Override
	public void draw(LintfordCore pCore) {
		super.draw(pCore);
		
		mTextureBatch.begin(pCore.HUD());
		mTextureBatch.draw(0, 0, 800, 600, -400, -300, -6f, 800, 600, 1f, mBackgroundtexture);
		mTextureBatch.end();
		
	}

	// --------------------------------------
	// Methods
	// --------------------------------------
}
