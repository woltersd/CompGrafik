#version 330
layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texture;

uniform mat4 cameraMatrix;
// needed?
uniform mat4 projection;

out vec2 texCoord;


void main()
{
    texCoord = texture;
    gl_Position =/* projection*/  cameraMatrix * vec4(position, 1.0);
  //  gl_Position = projection * cameraMatrix * vec4(position, 1.0);
}