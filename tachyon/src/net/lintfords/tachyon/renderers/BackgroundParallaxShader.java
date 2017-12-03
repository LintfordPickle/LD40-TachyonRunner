package net.lintfords.tachyon.renderers;

import org.lwjgl.opengl.GL20;

import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.maths.Vector2f;

public class BackgroundParallaxShader extends ShaderMVP_PT {

	private static final String VERT_SHADER = "/res/shaders/shader_basic_pct.vert";
	private static final String FRAG_SHADER = "res/shaders/background.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mTextureLocationBackground;

	private int mTextureSizeLocation;
	private int mTextureScaleLocation;

	private int mScreenDimensionsLocation;
	private int mCameraPositionLocation;
	private int mCameraOffsetLocation;

	private Vector2f mTextureDimensions;
	private float mTimeNormalised;
	private Vector2f mTextureScale;
	private float mDayLightFactor;
	private float mDayColorFactor;

	private Vector2f mScreenDimensions;
	private Vector2f mCameraPosition;
	private Vector2f mCameraOffset;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Vector2f textureScale() {
		return mTextureScale;
	}

	public void textureScale(Vector2f pNewValue) {
		mTextureScale = pNewValue;
	}

	public void textureScale(float pX, float pY) {
		mTextureScale.x = pX;
		mTextureScale.y = pY;
	}

	/** Gets the coeffient which affects how much of the color of the daylight affects the background layer. */
	public float dayColorFactor() {
		return mDayColorFactor;
	}

	/** Sets the coeffient which affects how much of the color of the daylight affects the background layer. */
	public void dayColorFactor(float pDayColorFactor) {
		mDayColorFactor = pDayColorFactor;
	}

	/** Gets the coeffient which affects how much of the amount of daylight affects the background layer (darker / lighter). */
	public float dayLightFactor() {
		return mDayLightFactor;
	}

	/** Sets the coeffient which affects how much of the amount of daylight affects the background layer (darker / lighter). */
	public void dayLightFactor(float pDayLightFactor) {
		mDayLightFactor = pDayLightFactor;
	}

	public float timeNor() {
		return mTimeNormalised;
	}

	public void timeNor(float pNewValue) {
		mTimeNormalised = pNewValue;
	}

	public void cameraPosition(Vector2f pNewPosition) {
		mCameraPosition = pNewPosition;
	}

	public void cameraPosition(float pX, float pY) {
		mCameraPosition.x = pX;
		mCameraPosition.y = pY;
	}

	public Vector2f cameraPosition() {
		return mCameraPosition;
	}

	public void cameraOffset(Vector2f pNewOffset) {
		mCameraOffset = pNewOffset;
	}

	public void cameraOffset(float pX, float pY) {
		mCameraOffset.x = pX;
		mCameraOffset.y = pY;
	}

	public Vector2f cameraOffset() {
		return mCameraOffset;
	}

	public void textureDimensions(Vector2f pNewPosition) {
		mTextureDimensions = pNewPosition;
	}

	public void textureDimensions(float pX, float pY) {
		mTextureDimensions.x = pX;
		mTextureDimensions.y = pY;
	}

	public Vector2f textureDimensions() {
		return mTextureDimensions;
	}

	public void screenDimensions(Vector2f pNewPosition) {
		mScreenDimensions = pNewPosition;
	}

	public void screenDimensions(float pX, float pY) {
		mScreenDimensions.x = pX;
		mScreenDimensions.y = pY;
	}

	public Vector2f screenDimensions() {
		return mScreenDimensions;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BackgroundParallaxShader() {
		super(VERT_SHADER, FRAG_SHADER);

		mScreenDimensionsLocation = -1;
		mTextureSizeLocation = -1;
		mTextureScaleLocation = -1;

		// Create with default values
		mScreenDimensions = new Vector2f(800, 600);
		mTextureDimensions = new Vector2f(512, 512);
		mCameraPosition = new Vector2f();
		mCameraOffset = new Vector2f();
		mTextureScale = new Vector2f();

		mDayColorFactor = 1f;
		mDayLightFactor = 1f;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void getUniformLocations() {
		super.getUniformLocations();

		// Get texture locations
		mTextureLocationBackground = GL20.glGetUniformLocation(shaderID(), "textureSamplerBackground");

		// Get uniform variable locations
		mTextureSizeLocation = GL20.glGetUniformLocation(shaderID(), "texSize");
		mTextureScaleLocation = GL20.glGetUniformLocation(shaderID(), "texScale");

		mScreenDimensionsLocation = GL20.glGetUniformLocation(shaderID(), "screenDimensions");
		mCameraPositionLocation = GL20.glGetUniformLocation(shaderID(), "cameraPostion");
		mCameraOffsetLocation = GL20.glGetUniformLocation(shaderID(), "cameraOffset");

		GL20.glUniform1i(mTextureLocationBackground, 0);

	}

	@Override
	protected void update() {
		super.update();

		// TODO(John): Check these for dirty values before calling OpenGL API functions.
		if (mTextureSizeLocation != -1) {
			GL20.glUniform2f(mTextureSizeLocation, mTextureDimensions.x, mTextureDimensions.y);
		}

		if (mTextureScaleLocation != -1) {
			GL20.glUniform2f(mTextureScaleLocation, mTextureScale.x, mTextureScale.y);
		}

		if (mScreenDimensionsLocation != -1) {
			GL20.glUniform2f(mScreenDimensionsLocation, mScreenDimensions.x, mScreenDimensions.y);
		}

		if (mCameraPositionLocation != -1) {
			GL20.glUniform2f(mCameraPositionLocation, mCameraPosition.x, mCameraPosition.y);
		}

		if (mCameraOffsetLocation != -1) {
			GL20.glUniform2f(mCameraOffsetLocation, mCameraOffset.x, mCameraOffset.y);
		}
	}

}
