#version 330 core

in vec2 texCoord;
uniform sampler2D tex_1;

out vec4 finalColor;

void main() {
    finalColor = texture(tex_1, texCoord);
}
