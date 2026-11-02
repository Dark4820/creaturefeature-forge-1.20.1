#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
// PORT: 1.20.1 entity positions arrive rotated by the camera; IViewRotMat turns them back to world axes (1.21 frame)
uniform mat3 IViewRotMat;
uniform int FogShape;
uniform float OffYPosition;
uniform float GameTime;
uniform vec3 Light0_Direction;
uniform vec3 TypeMult;
uniform vec3 Light1_Direction;

out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;

void main() {
    // PORT: position21 = Position as 1.21 sees it (camera-relative, world axes)
    vec3 position21 = IViewRotMat * Position;
    vec3 positioner = position21;
    vertexDistance = fog_distance(ModelViewMat, position21, FogShape);
    vertexColor = Color;
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, vec3(0,0,1), Color);
    if(OffYPosition>-300.0||OffYPosition<-301.0){
        vertexColor.a*=clamp(position21.y-OffYPosition+sin(GameTime*3200.0+length(position21.xz))/8.0,0.0,1.0);
        float fracted  =clamp(position21.y-OffYPosition+sin(GameTime*1200.0+length(position21.xz))/10.0,0.0,1.0);
        fracted-=fract(fracted*64.0)/64.0;
        positioner+=vec3(sin(GameTime*6400.0),0.0,-cos(GameTime*6400.0))*(1.0-fracted)/7.0;
        vertexColor.x*=fracted*TypeMult.x+1.0f-TypeMult.x;
        vertexColor.y*=fracted*TypeMult.y+1.0f-TypeMult.y;
        vertexColor.z*=fracted*TypeMult.z+1.0f-TypeMult.z;
        fracted=pow(fracted,5.0);
        fracted=min(1.0,fracted);
        lightMapColor = vec4(1,1,1,1)*fracted+texelFetch(Sampler2, UV2 / 16, 0)*(1.0f-fracted);
    }
    else{
        lightMapColor=texelFetch(Sampler2, UV2 / 16, 0);
    }

    // PORT: back to the camera-rotated frame of 1.20.1 (IViewRotMat is a rotation: inverse = transpose)
    gl_Position = ProjMat * ModelViewMat * vec4(transpose(IViewRotMat) * positioner, 1.0);


    overlayColor = texelFetch(Sampler1, UV1, 0);
    texCoord0 = UV0;
}