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
uniform vec3 u_cameraPosition;
uniform float majorGridSize = 100.0;
uniform float minorGridSize = 25.0;
uniform float patchGridSize = 12.5;
uniform float distanceFade = 2048.0;
uniform float u_alphaTest;

#define POSITION_NAME gPosition
#define NORMAL_NAME gNormal
#define DIFFUSE_NAME gDiffuse.rgb
#define SPECULAR_NAME gSpecular.r
#define ALPHA_NAME gDiffuse.a
#define PI 3.141592

#pragma include("/shaders/common/gbuffer.frag")

float calculateIntensityForCurrentPos(float gridSize) {
    vec2 Coord = cos(PI/gridSize*vec2(WorldPosition.x, WorldPosition.z));
    return 1.0 - (1.0-0.5*smoothstep(0.99,1.0,max(Coord.x,Coord.y)));
}

void main() {

    vec4 diffuse = vec4(0.0);

    diffuse += calculateIntensityForCurrentPos(majorGridSize) * vec4(1.0);
    diffuse += calculateIntensityForCurrentPos(minorGridSize) * vec4(0.8);
    diffuse += calculateIntensityForCurrentPos(patchGridSize) * vec4(0.5);

    float distance = length(u_cameraPosition.xyz - WorldPosition.xyz);

    diffuse *= (1 - (distance / distanceFade));

    if(diffuse.a < u_alphaTest) {
        discard;
    }

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
