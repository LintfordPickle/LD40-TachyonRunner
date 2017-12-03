package net.lintfords.tachyon.renderers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.maths.MathHelper;

public class UIBarCustom {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float x, y, w, h;
	private float r, g, b, a;

	private float mMinValue;
	private float mMaxValue;
	private float mCurValue;

	private String mTextureName;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setDestRectangle(float pX, float pY, float pW, float pH) {
		x = pX;
		y = pY;
		w = pW;
		h = pH;
	}

	public void setColor(float pR, float pG, float pB, float pA) {
		r = pR;
		g = pG;
		b = pB;
		a = pA;
	}

	public void setCurrentValue(float pValue) {
		mCurValue = MathHelper.clamp(pValue, mMinValue, mMaxValue);

	}

	public void setMinMax(float pMinValue, float pMaxValue) {
		mMinValue = pMinValue;
		mMaxValue = pMaxValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIBarCustom(float pMinValue, float pMaxValue, String pTextureName) {
		mMinValue = pMinValue;
		mMaxValue = pMaxValue;

		mTextureName = pTextureName;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void draw(LintfordCore pCore, TextureBatch pTextureBatch) {
		if (pTextureBatch == null)
			return;

		float lBarWidth = MathHelper.scaleToRange(mCurValue, 0, 100, 0, w - 8);
		lBarWidth = MathHelper.clamp(lBarWidth, 0, w);

		// Draw outer bar
		pTextureBatch.begin(pCore.HUD());
		pTextureBatch.draw(64, 0, 64, 32, x - 2, y - 2, -0.1f, w + 4, h + 4, 1f, 1f, 1f, 1f, 1f, TextureManager.textureManager().getTexture(mTextureName));
		pTextureBatch.draw(64, 32, 32, 32, x + 4, y + 4, -0.1f, lBarWidth, h - 8, 1f, r, g, b, a, TextureManager.textureManager().getTexture(mTextureName));
		pTextureBatch.end();
	}

}
