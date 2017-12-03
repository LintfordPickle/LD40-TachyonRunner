#version 150 core

uniform sampler2D textureSamplerBackground;

uniform vec2 texSize = vec2(512, 512);				// needed for texture scaling
uniform vec2 texScale = vec2(.5, .5);

uniform vec2 screenDimensions = vec2(800, 600);		// texture scaling
uniform vec2 cameraPostion = vec2(0, 0);			// texture translation
uniform vec2 cameraOffset = vec2(0,0);				// region offset

in vec2 passTexCoord;
out vec4 outColor;

void main() {
	// Get the background pixel color (taking camera position and window size into consideration)
	
	vec2 scaleOffset = vec2(0.5, 0.5);
	vec2 newTexCoords = passTexCoord;
	
	newTexCoords -= scaleOffset;
	newTexCoords *= texScale;
	newTexCoords += scaleOffset;
	
	float texCoordX = newTexCoords.x - (cameraOffset.x + cameraPostion.x);
	float texCoordY = newTexCoords.y - (cameraOffset.y + cameraPostion.y);
	
	vec4 texBackgroundColor = texture(textureSamplerBackground, vec2(texCoordX, texCoordY));

	outColor = texBackgroundColor;
	
}