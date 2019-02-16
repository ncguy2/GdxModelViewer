#define PI 3.141592

out vec4 FinalColour;

in vec3 Colour;
in vec3 FragCoord;

uniform vec4 u_additiveColour;
uniform float gridSize = 64.0;

void main() {

    vec2 Coord = cos(PI/gridSize*vec2(FragCoord.x, FragCoord.z));
    FinalColour = vec4(1.0)-0.5*smoothstep(0.999,1.0,max(Coord.x,Coord.y));
    FinalColour = vec4(1.0) - FinalColour;
    FinalColour.a = 1.0;

//	FinalColour = vec4(FragCoord, 1.0);
}
