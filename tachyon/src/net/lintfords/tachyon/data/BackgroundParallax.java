package net.lintfords.tachyon.data;

/** {@link BackgroundParallax} contains the information specific to a single parallax background layer. */
public class BackgroundParallax {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String name = "Background";
	public String textureName = "Background";
	public String textureFilename = "res/textures/background.png";

	public boolean textureRepeatX = true; // repeat
	public boolean textureRepeatY = true; // repeat

	public float centerLineHeight = 0;

	public float textureOffsetU;
	public float textureOffsetV;
	public float translationSpeedX = .3f;
	public float translationSpeedY = .3f;
	public float zDepth = -10;
	public float textureScaleX = .2f;
	public float textureScaleY = .2f;
	public float textureDimensionX = 512;
	public float textureDimensionY = 512;

	public boolean tileTexture;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BackgroundParallax() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

}
