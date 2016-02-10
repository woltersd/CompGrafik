#version 330 core
layout (location = 0) in vec3 position;
out vec3 texCoords;

uniform mat4 projection;
uniform mat4 cameraMatrix;


void main()
{
    // temp
   // mat4 test = projection * cameraMatrix;
    gl_Position =   /*projection */ cameraMatrix   * vec4(position, 1.0);
    texCoords = position;
}