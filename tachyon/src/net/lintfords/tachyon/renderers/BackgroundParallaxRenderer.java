package net.lintfords.tachyon.renderers;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.vertices.VertexDataStructurePT;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.options.DisplayConfig;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;
import net.lintfords.tachyon.controllers.BackgroundParallaxController;
import net.lintfords.tachyon.data.BackgroundParallax;

public class BackgroundParallaxRenderer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int ZDEPTH = -8;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BackgroundParallaxController mBackgroundParallax;
	private BackgroundParallaxShader mShader;
	private Texture mBackgroundTexture;

	private Matrix4f mModelMatrix;

	private boolean mIsLoaded;

	private boolean mIsFree;

	private int mVaoId = -1;
	private int mVboId = -1;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int ZDepth() {
		return ZDEPTH;
	}

	public Matrix4f modelMatrix() {
		return mModelMatrix;
	}

	public void modelMatrix(Matrix4f pNewValue) {
		mModelMatrix = pNewValue;
	}

	public boolean isFree() {
		return mIsFree;
	}

	public void isFree(boolean pNewValue) {
		mIsFree = pNewValue;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BackgroundParallaxRenderer(RendererManager pRendererManager, int o) {
		super(pRendererManager, "sadsad", 0);
		mShader = new BackgroundParallaxShader();

		mModelMatrix = new Matrix4f();

		mIsFree = true;
		mIsLoaded = false;

		ControllerManager lControllerManager = pRendererManager.core().controllerManager();
		mBackgroundParallax = (BackgroundParallaxController) lControllerManager.getControllerByNameRequired(BackgroundParallaxController.CONTROLLER_NAME);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		if (mIsLoaded)
			return;

		super.loadGLContent(pResourceManager);

		mShader.loadGLContent(pResourceManager);
		mShader.screenDimensions(pResourceManager.masterConfig().display().windowSize().x, pResourceManager.masterConfig().display().windowSize().y);

		mBackgroundTexture = TextureManager.textureManager().loadTexture(mBackgroundParallax.backgroundParallax().textureName, mBackgroundParallax.backgroundParallax().textureFilename);

		loadGLGeometry();

		mIsLoaded = true;

	}

	private void loadGLGeometry() {
		if (mVaoId == -1)
			mVaoId = GL30.glGenVertexArrays();

		if (mVboId == -1)
			mVboId = GL15.glGenBuffers();

		// Create the plane with a Z_DEPTH of 0 and translate it into depth position
		VertexDataStructurePT lNewVertex0 = new VertexDataStructurePT();
		lNewVertex0.xyzw(-.5f, -.5f, 0, 1f);
		lNewVertex0.uv(0, 0);

		VertexDataStructurePT lNewVertex1 = new VertexDataStructurePT();
		lNewVertex1.xyzw(-.5f, .5f, 0, 1f);
		lNewVertex1.uv(0, 1);

		VertexDataStructurePT lNewVertex2 = new VertexDataStructurePT();
		lNewVertex2.xyzw(.5f, .5f, 0, 1f);
		lNewVertex2.uv(1, 1);

		VertexDataStructurePT lNewVertex3 = new VertexDataStructurePT();
		lNewVertex3.xyzw(.5f, -.5f, 0, 1f);
		lNewVertex3.uv(1, 0);

		// copy vertices to the float buffer
		FloatBuffer lBuffer = BufferUtils.createFloatBuffer(6 * VertexDataStructurePT.stride);

		lBuffer.put(lNewVertex0.getElements());
		lBuffer.put(lNewVertex1.getElements());
		lBuffer.put(lNewVertex2.getElements());

		lBuffer.put(lNewVertex2.getElements());
		lBuffer.put(lNewVertex3.getElements());
		lBuffer.put(lNewVertex0.getElements());

		lBuffer.flip();

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, lBuffer, GL15.GL_STATIC_DRAW);

		GL20.glVertexAttribPointer(0, VertexDataStructurePT.positionElementCount, GL11.GL_FLOAT, false, VertexDataStructurePT.stride, VertexDataStructurePT.positionByteOffset);
		GL20.glVertexAttribPointer(1, VertexDataStructurePT.textureElementCount, GL11.GL_FLOAT, false, VertexDataStructurePT.stride, VertexDataStructurePT.textureByteOffset);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);

	}

	public void unloadGLContent() {
		if (!mIsLoaded)
			return;

		if (mVaoId > -1) {
			GL15.glDeleteBuffers(mVaoId);
			mVaoId = -1;
		}

		if (mVboId > -1) {
			GL15.glDeleteBuffers(mVboId);
			mVboId = -1;
		}

		mShader.unloadGLContent();

		mIsLoaded = false;

	}

	public void update(LintfordCore pCore) {
		if (!mIsLoaded) {
			return;
		}

		mShader.dayLightFactor(1f);

	}

	public void draw(LintfordCore pCore) {
		if (!mIsLoaded) {
			return;
		}

		final BackgroundParallax BACKGROUND_PARALAX = mBackgroundParallax.backgroundParallax();

		ICamera lGameCamera = pCore.gameCamera();
		DisplayConfig lDisplay = pCore.config().display();

		// Translate and scale the geometry (i.e. quad) to orientate it around the camera
		float lGeometryWidth = (lDisplay.windowSize().x * lGameCamera.getZoomFactorOverOne());
		float lGeometryHeight = (lDisplay.windowSize().y * lGameCamera.getZoomFactorOverOne());

		// Calculate the size of a texel
		final float TEX_TO_PIX = 1f / mBackgroundTexture.getTextureHeight();

		// These are for the geometry of the fullscreen quad (not the texture)
		mModelMatrix.setIdentity();
		mModelMatrix.translate(-lGameCamera.getPosition().x, -lGameCamera.getPosition().y, ZDEPTH);
		mModelMatrix.scale(lGeometryWidth, lGeometryHeight, 1f);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mBackgroundTexture.getTextureID());

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		// render
		mShader.projectionMatrix(lGameCamera.projection());
		mShader.viewMatrix(lGameCamera.view());
		mShader.modelMatrix(mModelMatrix);

		{

			// 0.25f factor to slow down the X Axis translation
			mShader.cameraPosition((lGameCamera.getPosition().x * 0.25f * BACKGROUND_PARALAX.translationSpeedX) * TEX_TO_PIX, (lGameCamera.getPosition().y * 0.25f * BACKGROUND_PARALAX.translationSpeedY) * TEX_TO_PIX);

		}

		{
			// Need to calculate the offset of the texture to apply WITHIN the geometric quad.
			// This will be used to place the texture center at sea level

			final float OFFSET_TO_TEXTURE_FLOOR_HEIGHT = BACKGROUND_PARALAX.centerLineHeight;
			final float HALF_TEXTURE_HEIGHT = mBackgroundTexture.getTextureHeight() * 0.5f;

			final float FLOOR_LINE_OFFSET_Y = -(OFFSET_TO_TEXTURE_FLOOR_HEIGHT - HALF_TEXTURE_HEIGHT);

			final float TEXTURE_OFFSET_Y = BACKGROUND_PARALAX.textureOffsetV;

			mShader.cameraOffset(0, (0 + FLOOR_LINE_OFFSET_Y + TEXTURE_OFFSET_Y) * TEX_TO_PIX);

		}

		{
			// Calculate the size ratio between the window and the texture sizes so the texture aspect ratio can be maintained
			// if the window size is changed.

			final float CAM_SCALE_X = lGameCamera.getZoomFactorOverOne();

			float lW = (float) (lDisplay.windowSize().x * CAM_SCALE_X) / (float) mBackgroundTexture.getTextureWidth();
			float lH = (float) (lDisplay.windowSize().y * CAM_SCALE_X) / (float) mBackgroundTexture.getTextureHeight();

			mShader.textureScale(lW * BACKGROUND_PARALAX.textureScaleX, lH * BACKGROUND_PARALAX.textureScaleY);

		}

		mShader.timeNor(1f);
		mShader.screenDimensions(lDisplay.windowSize().x, lDisplay.windowSize().y);
		mShader.bind();

		// Bind to the VAO that has all the information about the quad vertices
		GL30.glBindVertexArray(mVaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

		// Put everything back to default (de select)
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);

		mShader.unbind();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
