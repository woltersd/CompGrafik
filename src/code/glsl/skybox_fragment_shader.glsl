#version 330 core
in vec3 texCoords;
out vec4 color;

/******************/
uniform sampler2D tex_1;
in vec2 texCoord;
/**********************/
//uniform samplerCube skybox;

void main()
{
    color = texture(tex_1, texCoord);//texture(skybox, texCoords);
}
