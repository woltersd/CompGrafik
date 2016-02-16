#version 330 core
layout (location = 0) in vec3 position; // The position variable has attribute position 0
layout (location = 1) in vec2 texture;

out vec2 texCoord;

void main() {
    texCoord = texture;
    gl_Position = vec4(position, 1.0);
}
