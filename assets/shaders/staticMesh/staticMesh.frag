layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gDiffuse;
layout (location = 3) out vec3 gSpecular;
layout (location = 4) out vec3 gTexCoords;

in vec4 WorldPosition;
in vec3 Normal;
in vec2 TexCoords;
in vec4 Colour;

uniform sampler2D u_diffuseTexture;
uniform vec4 u_diffuseColor;

#define POSITION_NAME gPosition
#define NORMAL_NAME gNormal
#define DIFFUSE_NAME gDiffuse.rgb
#define SPECULAR_NAME gSpecular.r
#define ALPHA_NAME gDiffuse.a

#pragma include("/shaders/common/gbuffer.frag")

void main() {

    #if defined(diffuseTextureFlag) && defined(diffuseColorFlag) && defined(colorFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, TexCoords) * u_diffuseColor * Colour;
	#elif defined(diffuseTextureFlag) && defined(diffuseColorFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, TexCoords) * u_diffuseColor;
	#elif defined(diffuseTextureFlag) && defined(colorFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, TexCoords) * Colour;
	#elif defined(diffuseTextureFlag)
		vec4 diffuse = texture2D(u_diffuseTexture, TexCoords);
	#elif defined(diffuseColorFlag) && defined(colorFlag)
		vec4 diffuse = u_diffuseColor * Colour;
	#elif defined(diffuseColorFlag)
		vec4 diffuse = u_diffuseColor;
	#elif defined(colorFlag)
		vec4 diffuse = Colour;
	#else
		vec4 diffuse = vec4(1.0);
	#endif

	toPositionStore(WorldPosition);

	if(gl_FrontFacing) {
        toNormalStore(Normal);
    }else{
        toNormalStore(-Normal);
    }

	toDiffuseStore(diffuse.rgb);
	toAlphaStore(diffuse.a);
	toSpecularStore(1.0);

	gTexCoords = vec3(TexCoords, 0.0);

}
