package net.lintfords.tachyon.renderers;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.linebatch.LineBatch;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.maths.Matrix4f;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;
import net.lintfords.tachyon.controllers.TrackController;
import net.lintfords.tachyon.data.StartGridElement;
import net.lintfords.tachyon.data.Track;

public class TrackRenderer extends BaseRenderer {

	// The number of bytes an element has (all elements are floats here)
	protected static final int elementBytes = 4;

	// Elements per parameter
	protected static final int positionElementCount = 4;
	protected static final int colorElementCount = 4;
	protected static final int textureElementCount = 2;

	// Bytes per parameter
	protected static final int positionBytesCount = positionElementCount * elementBytes;
	protected static final int colorBytesCount = colorElementCount * elementBytes;
	protected static final int textureBytesCount = textureElementCount * elementBytes;

	// Byte offsets per parameter
	protected static final int positionByteOffset = 0;
	protected static final int colorByteOffset = positionByteOffset + positionBytesCount;
	protected static final int textureByteOffset = colorByteOffset + colorBytesCount;

	// The amount of elements that a vertex has
	protected static final int elementCount = positionElementCount + colorElementCount + textureElementCount;

	// The size of a vertex in bytes (sizeOf())
	protected static final int stride = positionBytesCount + colorBytesCount + textureBytesCount;

	public static final String RENDERER_NAME = "TrackRenderer";

	protected static final String VERT_FILENAME = "/res/shaders/shader_basic_pct.vert";
	protected static final String FRAG_FILENAME = "/res/shaders/shader_basic_pct.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	TextureBatch mTextureBatch;
	LineBatch mLineBatch;

	TrackController mTrackController;
	Texture mDebugTexture;
	Texture mTrackTexture;

	protected int mVaoId = -1;
	protected int mVboId = -1;
	protected int mVertexCount = 0;

	protected ShaderMVP_PT mShader;
	protected Matrix4f mModelMatrix;
	protected FloatBuffer mBuffer;

	protected boolean mTrackGenerated;

	@Override
	public int ZDepth() {
		return -7;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TrackRenderer(RendererManager pRendererManager, int pGroupID) {
		super(pRendererManager, RENDERER_NAME, pGroupID);

		ControllerManager lControllerManager = pRendererManager.core().controllerManager();
		mTrackController = (TrackController) lControllerManager.getControllerByNameRequired(TrackController.CONTROLLER_NAME);

		mShader = new ShaderMVP_PT(VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inColor");
				GL20.glBindAttribLocation(pShaderID, 2, "inTexCoord");
			}
		};

		mModelMatrix = new Matrix4f();

		mTextureBatch = new TextureBatch();
		mLineBatch = new LineBatch();

		mTrackGenerated = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mTextureBatch.loadGLContent(pResourceManager);
		mLineBatch.loadGLContent(pResourceManager);

		mShader.loadGLContent(pResourceManager);

		if (mVaoId == -1)
			mVaoId = GL30.glGenVertexArrays();

		if (mVboId == -1)
			mVboId = GL15.glGenBuffers();

		mDebugTexture = TextureManager.textureManager().loadTexture("TrackProps", "res/textures/trackProps.png");
		mTrackTexture = TextureManager.textureManager().loadTexture("Track", "res/textures/track.png");

		loadTrackMesh(mTrackController.track());

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mTextureBatch.unloadGLContent();
		mLineBatch.unloadGLContent();

		mShader.unloadGLContent();

		if (mVaoId > -1)
			GL30.glDeleteVertexArrays(mVaoId);

		if (mVboId > -1)
			GL15.glDeleteBuffers(mVboId);
	}

	@Override
	public void draw(LintfordCore pCore) {

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mTrackTexture.getTextureID());

		GL30.glBindVertexArray(mVaoId);

		mShader.projectionMatrix(pCore.gameCamera().projection());
		mShader.viewMatrix(pCore.gameCamera().view());
		mModelMatrix.setIdentity();
		mModelMatrix.translate(0, 0f, -6f);
		mShader.modelMatrix(mModelMatrix);

		mShader.bind();

		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, mVertexCount);

		mShader.unbind();

		GL30.glBindVertexArray(0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		boolean pRenderControlNodes = true;
		if (pRenderControlNodes) {
			List<Vector2f> origPoints = mTrackController.track().controlPoints();
			mTextureBatch.begin(pCore.gameCamera());

			int NUM_POINTS = origPoints.size();
			for (int i = 0; i < NUM_POINTS; i++) {
				float x = origPoints.get(i).x;
				float y = origPoints.get(i).y;

				mTextureBatch.draw(0, 0, 128, 1, x - 8, y - 8, -5.5f, 16, 16, 1f, mDebugTexture);

			}
			
			int NUM_START_ELEMENTS = mTrackController.track().startElements().size();
			for (int i = 0; i < NUM_START_ELEMENTS; i++) {
				StartGridElement e = mTrackController.track().startElements().get(i);
				float x = e.x;
				float y = e.y;
				float a = e.a;

				mTextureBatch.draw(0, 0, 64, 128, x, y, -5.4f, 64, 128, 1f, 1f, 1f, 1f,   a + (float)Math.toRadians(90) , 32, 64, 1f, 1f, mDebugTexture);

			}

			mTextureBatch.end();

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void loadTrackMesh(Track pTrack) {
		if (pTrack == null)
			return;

		buildTrackSegments(mTrackController.track());

		GL30.glBindVertexArray(mVaoId);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mBuffer, GL15.GL_STATIC_DRAW);

		GL20.glVertexAttribPointer(0, positionElementCount, GL11.GL_FLOAT, false, stride, positionByteOffset);
		GL20.glVertexAttribPointer(1, colorElementCount, GL11.GL_FLOAT, false, stride, colorByteOffset);
		GL20.glVertexAttribPointer(2, textureElementCount, GL11.GL_FLOAT, false, stride, textureByteOffset);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		GL30.glBindVertexArray(0);

		mTrackGenerated = true;
	}

	private void buildTrackSegments(Track pTrack) {

		List<Vector2f> lTrackPoints = pTrack.trackPoints();

		Vector2f tempDriveDirection = new Vector2f();
		Vector2f tempSideDirection = new Vector2f();

		mBuffer = BufferUtils.createFloatBuffer(lTrackPoints.size() * 4 * stride);

		float lDistance = 0;
		final int trackPointCount = lTrackPoints.size();
		for (int i = 0; i < trackPointCount; i++) {
			int nextIndex = i + 1;
			if (nextIndex > lTrackPoints.size() - 1) {
				nextIndex = 0;
			}

			tempDriveDirection.x = lTrackPoints.get(nextIndex).x - lTrackPoints.get(i).x;
			tempDriveDirection.y = lTrackPoints.get(nextIndex).y - lTrackPoints.get(i).y;

			tempSideDirection.x = tempDriveDirection.y;
			tempSideDirection.y = -tempDriveDirection.x;

			tempSideDirection.nor();

			Vector2f outerPoint = new Vector2f();
			outerPoint.x = lTrackPoints.get(i).x + tempSideDirection.x * pTrack.segmentWidth / 2;
			outerPoint.y = lTrackPoints.get(i).y + tempSideDirection.y * pTrack.segmentWidth / 2;

			Vector2f innerPoint = new Vector2f();
			innerPoint.x = lTrackPoints.get(i).x - tempSideDirection.x * pTrack.segmentWidth / 2;
			innerPoint.y = lTrackPoints.get(i).y - tempSideDirection.y * pTrack.segmentWidth / 2;

			addVertToBuffer(innerPoint.x, innerPoint.y, 0, 0, lDistance / 256f);
			addVertToBuffer(outerPoint.x, outerPoint.y, 0, 1, lDistance / 256f);

			lDistance += tempDriveDirection.len();

		}
		
		// Add the last two vertices to close the track
		tempDriveDirection.x = lTrackPoints.get(1).x - lTrackPoints.get(0).x;
		tempDriveDirection.y = lTrackPoints.get(1).y - lTrackPoints.get(0).y;

		tempSideDirection.x = tempDriveDirection.y;
		tempSideDirection.y = -tempDriveDirection.x;

		tempSideDirection.nor();

		Vector2f outerPoint = new Vector2f();
		outerPoint.x = lTrackPoints.get(0).x + tempSideDirection.x * pTrack.segmentWidth / 2;
		outerPoint.y = lTrackPoints.get(0).y + tempSideDirection.y * pTrack.segmentWidth / 2;

		Vector2f innerPoint = new Vector2f();
		innerPoint.x = lTrackPoints.get(0).x - tempSideDirection.x * pTrack.segmentWidth / 2;
		innerPoint.y = lTrackPoints.get(0).y - tempSideDirection.y * pTrack.segmentWidth / 2;

		addVertToBuffer(innerPoint.x, innerPoint.y, 0, 0, lDistance / 256f);
		addVertToBuffer(outerPoint.x, outerPoint.y, 0, 1, lDistance / 256f);

		lDistance += tempDriveDirection.len();
		

		mBuffer.flip();

	}

	private void addVertToBuffer(float x, float y, float z, float u, float v) {

		mBuffer.put(x);
		mBuffer.put(y);
		mBuffer.put(z);
		mBuffer.put(1f);

		mBuffer.put(1f);
		mBuffer.put(1f);
		mBuffer.put(1f);
		mBuffer.put(1f);

		mBuffer.put(u);
		mBuffer.put(v);

		mVertexCount++;

	}

}
