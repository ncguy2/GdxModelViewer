in vec3 a_position;
in vec3 a_normal;
in vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat3 u_normalMatrix;
uniform mat4 u_bones[numBones];

out vec4 WorldPosition;
out vec3 Normal;
out vec2 TexCoords;
out vec4 Colour;

#ifdef boneWeight0Flag
in vec2 a_boneWeight0;
#endif //boneWeight0Flag

#ifdef boneWeight1Flag
in vec2 a_boneWeight1;
#endif //boneWeight1Flag

#ifdef boneWeight2Flag
in vec2 a_boneWeight2;
#endif //boneWeight2Flag

#ifdef boneWeight3Flag
in vec2 a_boneWeight3;
#endif //boneWeight3Flag

#ifdef boneWeight4Flag
in vec2 a_boneWeight4;
#endif //boneWeight4Flag

#ifdef boneWeight5Flag
in vec2 a_boneWeight5;
#endif //boneWeight5Flag

#ifdef boneWeight6Flag
in vec2 a_boneWeight6;
#endif //boneWeight6Flag

#ifdef boneWeight7Flag
in vec2 a_boneWeight7;
#endif //boneWeight7Flag

void main() {

	mat4 skinning = mat4(0.0);
	#ifdef boneWeight0Flag
		skinning += (a_boneWeight0.y) * u_bones[int(a_boneWeight0.x)];
	#endif //boneWeight0Flag
	#ifdef boneWeight1Flag
		skinning += (a_boneWeight1.y) * u_bones[int(a_boneWeight1.x)];
	#endif //boneWeight1Flag
	#ifdef boneWeight2Flag
		skinning += (a_boneWeight2.y) * u_bones[int(a_boneWeight2.x)];
	#endif //boneWeight2Flag
	#ifdef boneWeight3Flag
		skinning += (a_boneWeight3.y) * u_bones[int(a_boneWeight3.x)];
	#endif //boneWeight3Flag
	#ifdef boneWeight4Flag
		skinning += (a_boneWeight4.y) * u_bones[int(a_boneWeight4.x)];
	#endif //boneWeight4Flag
	#ifdef boneWeight5Flag
		skinning += (a_boneWeight5.y) * u_bones[int(a_boneWeight5.x)];
	#endif //boneWeight5Flag
	#ifdef boneWeight6Flag
		skinning += (a_boneWeight6.y) * u_bones[int(a_boneWeight6.x)];
	#endif //boneWeight6Flag
	#ifdef boneWeight7Flag
		skinning += (a_boneWeight7.y) * u_bones[int(a_boneWeight7.x)];
	#endif //boneWeight7Flag

	WorldPosition = u_worldTrans * skinning * vec4(a_position, 1.0);
	gl_Position = u_projViewTrans * WorldPosition;

	TexCoords = a_texCoord0;
//	Normal = normalize(u_normalMatrix * a_normal);
	Normal = normalize((u_worldTrans * skinning * vec4(a_normal, 0.0)).xyz);

	Colour = vec4(1.0);
}
