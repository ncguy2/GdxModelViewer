in vec3 a_position;
in vec3 a_normal;
in vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat3 u_normalMatrix;

out vec4 WorldPosition;
out vec3 Normal;
out vec2 TexCoords;
out vec4 Colour;

void main() {
	WorldPosition = u_worldTrans * vec4(a_position, 1.0);
	gl_Position = u_projViewTrans * WorldPosition;

	TexCoords = a_texCoord0;
	Normal = normalize(u_normalMatrix * a_normal);

	Colour = vec4(1.0);
}
