#ifndef POSITION_NAME
#define POSITION_NAME gPosition
#endif

#ifndef NORMAL_NAME
#define NORMAL_NAME gNormal
#endif

#ifndef DIFFUSE_NAME
#define DIFFUSE_NAME gDiffuse.rgb
#endif

#ifndef SPECULAR_NAME
#define SPECULAR_NAME gSpecular.r
#endif

#ifndef ALPHA_NAME
#define ALPHA_NAME gDiffuse.a
#endif

void toPositionStore(vec4 pos) {
    POSITION_NAME = pos.xyz;
}

void toNormalStore(vec3 nor) {
    NORMAL_NAME = nor * 0.5 + 0.5;
}

void toDiffuseStore(vec3 dif) {
    DIFFUSE_NAME = dif;
}

void toAlphaStore(float a) {
    ALPHA_NAME = a;
}

void toSpecularStore(float s) {
    SPECULAR_NAME = s;
}