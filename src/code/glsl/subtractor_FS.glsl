#version 330 core

uniform mat4 modelMatrix;
uniform sampler2D image;
uniform sampler2D thresh;

uniform struct Light {
   vec3 position;
   vec3 intensities; //color of the light
} light;

in vec3 fragNormal;
in vec3 fragVert;
in vec2 texCoord;


out vec4 finalColor;

void main()
{
    vec4 vertexColor = texture(image, texCoord);
    vec4 threshColor = texture(thresh, texCoord);
    if(threshColor.x == 0.0f) vertexColor.a = 0.0f;

    //calculate normal in world coordinates
    mat3 normalMatrix = transpose(inverse(mat3(modelMatrix)));
    vec3 normal = normalize(normalMatrix * fragNormal);

    //calculate the location of this fragment (pixel) in world coordinates
    vec3 fragPosition = vec3(modelMatrix * vec4(normal, 1));

    //calculate the vector from this pixels surface to the light source
    vec3 surfaceToLight = light.position - fragPosition;

    //calculate the cosine of the angle of incidence
    float brightness = dot(normal, surfaceToLight) / (length(surfaceToLight) * length(normal));
    /*if (brightness < 0) {
        brightness = -brightness;
    }*/
    brightness = clamp(brightness, 0, 1);

    finalColor = vec4(brightness * light.intensities * vertexColor.rgb, vertexColor.a);
}