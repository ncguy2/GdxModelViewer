out vec4 FinalColour;

in vec4 Colour;
in vec2 TexCoords;

struct Brush {
    vec4 colour;
    vec2 point;
    float size;
    float hardness;
};

uniform Brush brush;
uniform sampler2D u_texture;
uniform vec2 texSize;

void main() {
    vec4 texCol = texture(u_texture, TexCoords);

    float dist = distance(brush.point, (TexCoords * texSize));

    if(dist <= brush.size) {
        float perc = 1.0 - (dist / brush.size);
//        perc *= brush.colour.a;
//        perc *= brush.hardness;
        texCol = mix(texCol, brush.colour, perc);
    }

    FinalColour = texCol;
}