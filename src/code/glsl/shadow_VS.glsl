#version 330 core
layout (location = 0) in vec3 position;
layout (location = 2) in vec2 texture;

uniform struct Light {
   vec3 position;
   vec3 intensities; //color of the light
} light;

uniform mat4 cameraMatrix;
uniform mat4 modelMatrix;

out vec2 texC;

void main()
{
   texC = texture;

   //float t = -(light.position.y/(position.y - light.position.y));
   //gl_Position = cameraMatrix * modelMatrix * vec4(vec3(light.position.x + (t * (position.x - light.position.x)) , 0.0f, light.position.z + (t * (position.z - light.position.z))), 1.0f);

   mat4 shadow = mat4(light.position.y, -light.position.x, 0.0f, 0.0f,
                      0.0f, 0.0f, 0.0f, 0.0f,
                      0.0f, -light.position.z, light.position.y, 0.0f,
                      0.0f, -1.0f, 0.0f, light.position.y);

   gl_Position = cameraMatrix * modelMatrix * shadow * vec4(vec3(position), 1.0f);
}