out vec4 FinalColour;

in vec3 Colour;

uniform vec4 u_additiveColour;

void main() {
	FinalColour = vec4(Colour + u_additiveColour.rgb, 1.0);
}
