in vec3 a_position;
in vec2 a_texCoord0;
in vec3 a_normal;
in vec4 a_color;

uniform mat4 u_projViewTrans; // hide
uniform mat4 u_worldTrans; // hide
uniform mat3 u_normalMatrix; // hide


out vec2 TexCoords;
out vec3 Normal;
out vec3 Colour;

out vec3 FragCoord;
out vec3 VertCoord;

void main() {

    Colour = a_color.rgb;

    vec4 worldPos = u_worldTrans * vec4(a_position, 1.0);
    vec4 pos = u_projViewTrans * worldPos;

    TexCoords = a_texCoord0;
    vec3 normal = normalize(u_normalMatrix * a_normal);
    Normal = normal;

    FragCoord = worldPos.xyz;
    VertCoord = pos.xyz;
    gl_Position = pos;

}