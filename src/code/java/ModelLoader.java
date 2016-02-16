package code.java;

import code.java.GLModel.*;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.awt.GLCanvas;

import javax.vecmath.Point3f;
import java.util.LinkedList;
import java.util.List;

public class ModelLoader {

    public static List<GLObject> loadModelList (GL3 gl, GLCanvas canvas, InputListener inputListener) {
        List<GLObject> modelList;
        GLModel model;
        GLShadow shadow;
        GLCollision collision;
        BackgroundSubtractor subtractor;
        modelList = new LinkedList<>();

        float lightPos[] = new float[]{100f, 50f, -40f};
        float lightColor[] = new float[]{1f, 0.6f, 0.75f};

        Shader shader = new Shader(gl, "/src/code/glsl/","vertex_shader.glsl", "texture_FS.glsl");
        shader.setGlobalUniform("light.position", lightPos);
        shader.setGlobalUniform("light.intensities", lightColor);

        Shader shadowShader = new Shader(gl, "/src/code/glsl/","shadow_VS.glsl", "shadow_FS.glsl");
        shadowShader.setGlobalUniform("light.position", lightPos);
        shadowShader.setGlobalUniform("light.intensities", new float[]{1f, 1f, 1f});

        Shader camShader = new Shader(gl, "/src/code/glsl/","vertex_shader.glsl", "subtractor_FS.glsl");
        camShader.setGlobalUniform("light.position", lightPos);
        camShader.setGlobalUniform("light.intensities", lightColor);

        Shader camShadowShader = new Shader(gl, "/src/code/glsl/","shadow_VS.glsl", "camShadow_FS.glsl");
        camShadowShader.setGlobalUniform("light.position", lightPos);
        camShadowShader.setGlobalUniform("light.intensities", new float[]{1f, 1f, 1f});

        Shader skyShader = new Shader(gl, "/src/code/glsl/","sky_VS.glsl", "sky_FS.glsl");
        model = new GLModel(gl, "sky.obj", skyShader);
        model.setModelMatrixScale(12,12,12);
        modelList.add(model);

        GLBall ball = new GLBall(gl, "ball.obj", shader, 0.35f, new Point3f(2f, 2f, 0f));
        ball.setModelMatrixOffset(4f, 2f, 0f);
        modelList.add(ball);
        shadow = new GLShadow(gl, ball, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "outline.obj", new Shader(gl, "/src/code/glsl/","vertex_shader.glsl", "color_FS.glsl"));
        model.setModelMatrixOffset(0,0.01f,0);
        model.setShaderUniform("light.position", lightPos);
        model.setShaderUniform("light.intensities", lightColor);
        modelList.add(model);

        model = new GLModel(gl, "ground.obj", shader);
        modelList.add(model);
        collision = new GLPlaneCollision(new Point3f[]{new Point3f(8,0,0), new Point3f(0,0,0)}, "x");
        ball.addCollision(collision);
        collision = new GLPlaneCollision(new Point3f[]{new Point3f(0,0,0), new Point3f(-8,0,0)}, "x");
        ball.addCollision(collision);

        model = new GLModel(gl, "net.obj", shader);
        modelList.add(model);
        collision = new GLPlaneCollision(new Point3f[]{new Point3f(0,2.43f,0), new Point3f(0,1.43f,0)}, "y");
        ball.addCollision(collision);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        collision = new GLPlaneCollision(new Point3f[]{new Point3f(-8,40,0), new Point3f(-8,0,0)}, "y");
        ball.addCollision(collision);
        collision = new GLPlaneCollision(new Point3f[]{new Point3f(8,40,0), new Point3f(8,0,0)}, "y");
        ball.addCollision(collision);


        model = new GLModel(gl, "cylinder.obj", shader);
        model.setModelMatrixOffset(0f, 0f, 4f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "cylinder.obj", shader);
        model.setModelMatrixOffset(0f, 0f, -4f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);


        subtractor = new BackgroundSubtractor(gl, 0, canvas, inputListener);
        modelList.add(subtractor);

        GLSphereCollision sphereCollision = new GLSphereCollision(new Point3f(-4f, 0.0f, 0.0f), 1);
        model = new GLCam(gl, subtractor, "player1.obj", camShader, -4f, sphereCollision, inputListener);
        model.setModelMatrixOffset(-4f, 0.0f, 0.0f);
        modelList.add(model);
        ball.addCollision(sphereCollision);
        shadow = new GLShadow(gl, model, camShadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        sphereCollision = new GLSphereCollision(new Point3f(4f, 0.0f, 0.0f), 1);
        model = new GLCam(gl, subtractor, "player2.obj", camShader, 4, sphereCollision, inputListener);
        model.setModelMatrixOffset(4f, 0.0f, 0.0f);
        modelList.add(model);
        ball.addCollision(sphereCollision);
        shadow = new GLShadow(gl, model, camShadowShader);
        modelList.add(shadow);



        modelList.addAll(loadPalmTrees(gl, shader, shadowShader));
        modelList.addAll(loadGrass(gl, shader, shadowShader));

        GLHelpDialog helpDialog = new GLHelpDialog(gl, inputListener);
        modelList.add(helpDialog);

        return modelList;

    }

    private static List<GLObject> loadGrass(GL3 gl, Shader shader, Shader shadowShader) {
        List<GLObject> modelList = new LinkedList<>();
        GLModel model;
        GLShadow shadow;

        float[] values = {
                1, -5, 0, 20,
                2, -3, 0, 10,
                2, 0, 0, 15,
                1, -15, 0, 25,
                2, 12, 0, 10,
                3, -1, 0, 25,
                3, -9, 0, 0,
                2, -11, 0, -2,
                3, -15, 0, 2,
                3, 9, 0, -1,
                2, 8, 0, 10,
                1, 13, 0, 1,
                2, 4, 0, 20,
                1, -5, 0, -20,
                2, -3, 0, -10,
                2, 0, 0, -15,
                1, -15, 0, -25,
                2, 12, 0, -10,
                3, -1, 0, -25,
                3, -9, 0, -40,
                3, -1, 0, -25,
                3, -9, 0, -40,
                2, -22, 0, -35,
                3, -1, 0, -25,
                3, -9, 0, -40,
                2, -22, 0, -35,
                3, 0, 0, -30,
                3, 22, 0, -45,
                2, 8, 0, -10,
                1, 13, 0, -15,
                2, 4, 0, -20
        };

        for (int i = 0; i < values.length / 4; i++) {
            int index = i * 4;
            model = new GLModel(gl, "Grass0" +(int)values[index] +".obj", shader);
            model.setModelMatrixOffset(values[index+1], values[index+2], values[index+3]);
            modelList.add(model);
            shadow = new GLShadow(gl, model, shadowShader);
            model.setShadow(shadow);
            modelList.add(shadow);
        }
        return modelList;
    }

    private static List<GLObject> loadPalmTrees(GL3 gl, Shader shader, Shader shadowShader) {

        List<GLObject> modelList = new LinkedList<>();
        GLModel model;
        GLShadow shadow;

    /*    float[] values = {
                -5, 0, -40,     0,
                -20, 0, -10,    1,
                -2, 0, -60,     2,
                5, 0, -10,      3,
                25, 0, -30,     3,
                30, 0, -25,     2,
                20, 0, -35,     1.5f,
                8,  0, -30,     0.5f,
                20, 0, -32,     1.0f,
                35, 0, -34,     1.5f,
                16, 0, -48,     2.8f,
                -20, 0, -20,    3.5f,
                -40, 0, -28,    1.8f,
                -3, 0, -24,     2.5f,
                -8, 0 -18,      0.8f,
                0, 0, -18,      2.8f,
                0, 0, -50,      0.3f,
                -14, 0, -22,    1.8f,
                2, 0, -30,      2.5f,
                -14, 0, -18,    0.8f,
                12, 0, -30,     0.3f,
                -4, 0, -22,     1.8f,
                -30, 0, -24,    2.5f,
                -8, 0, -18,     0.8f
        };

        for (int i = 0; i < values.length / 4; i++) {
            int index = i * 4;
            model = new GLModel(gl, "palm02.obj", shader);
            model.setModelMatrixOffset(values[index], values[index+1], values[index+2]);
            model.setModelMatrixRotation(values[index+3], 0, 1, 0);
            modelList.add(model);
            shadow = new GLShadow(gl, model, shadowShader);
            modelList.add(shadow);
        }*/

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-5f, 0f, -40f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-20f, 0f, -10f);
        model.setModelMatrixRotation(1, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-2f, 0f, -60f);
        model.setModelMatrixRotation(2, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(5f, 0f, -10f);
        model.setModelMatrixRotation(3, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(25f, 0f, -30f);
        model.setModelMatrixRotation(3, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(30f, 0f, -25f);
        model.setModelMatrixRotation(2, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);


        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(20f, 0f, -35f);
        model.setModelMatrixRotation(1.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(8f, 0f, -30f);
        model.setModelMatrixRotation(0.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(20f, 0f, -32f);
        model.setModelMatrixRotation(1.0f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(35f, 0f, -34f);
        model.setModelMatrixRotation(1.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(16f, 0f, -48f);
        model.setModelMatrixRotation(2.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-20f, 0f, -20f);
        model.setModelMatrixRotation(3.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-40f, 0f, -28f);
        model.setModelMatrixRotation(1.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-3f, 0f, -24f);
        model.setModelMatrixRotation(2.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-8f, 0f, -18f);
        model.setModelMatrixRotation(0.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(0f, 0f, -18f);
        model.setModelMatrixRotation(2.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-11f, 0f, -50f);
        model.setModelMatrixRotation(3.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-14f, 0f, -22f);
        model.setModelMatrixRotation(1.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(2f, 0f, -30f);
        model.setModelMatrixRotation(2.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-14f, 0f, -18f);
        model.setModelMatrixRotation(0.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(12f, 0f, -30f);
        model.setModelMatrixRotation(3.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-4f, 0f, -22f);
        model.setModelMatrixRotation(1.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-30f, 0f, -24f);
        model.setModelMatrixRotation(2.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-8f, 0f, -18f);
        model.setModelMatrixRotation(0.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        model.setShadow(shadow);
        modelList.add(shadow);

        return modelList;
    }
}
