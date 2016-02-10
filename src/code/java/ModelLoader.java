package code.java;

import code.java.GLModel.*;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.awt.GLCanvas;

import javax.vecmath.Point3f;
import java.util.LinkedList;
import java.util.List;

public class ModelLoader {

    public static List<GLObject> loadModelList (GL3 gl, GLCanvas canvas) {
        List<GLObject> modelList;
        GLModel model;
        GLShadow shadow;
        GLCollision collision;
        BackgroundSubtractor subtractor;
        modelList = new LinkedList<>();


        Shader shader = new Shader(gl, "/src/code/glsl/","vertex_shader.glsl", "texture_FS.glsl");
        shader.setGlobalUniform("light.position", new float[] {20f, 20f, 0f});
        shader.setGlobalUniform("light.intensities", new float[] {1f, 1f, 1f});

        Shader shadowShader = new Shader(gl, "/src/code/glsl/","shadow_VS.glsl", "shadow_FS.glsl");
        shadowShader.setGlobalUniform("light.position", new float[] {20f, 20f, 20f});
        shadowShader.setGlobalUniform("light.intensities", new float[] {1f, 1f, 1f});
        Shader camShader = new Shader(gl, "/src/code/glsl/","vertex_shader.glsl", "subtractor_FS.glsl");
        camShader.setGlobalUniform("light.position", new float[] {20f, 20f, 0f});
        camShader.setGlobalUniform("light.intensities", new float[] {1f, 1f, 1f});

        Shader skyShader = new Shader(gl, "/src/code/glsl/","sky_VS.glsl", "sky_FS.glsl");
        model = new GLModel(gl, "sky.obj", skyShader);
        model.setModelMatrixScale(10,10,10);
        modelList.add(model);

        GLBall ball = new GLBall(gl, "ball.obj", shader, 0.35f, new Point3f(2f, 2f, 0f));
        ball.setModelMatrixOffset(2f, 2f, 0f);
        modelList.add(ball);
        shadow = new GLShadow(gl, ball, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "outline.obj", new Shader(gl, "/src/code/glsl/","vertex_shader.glsl", "color_FS.glsl"));
        model.setModelMatrixOffset(0,0.01f,0);
        model.setShaderUniform("light.position", new float[] {20f, 20f, 0f});
        model.setShaderUniform("light.intensities", new float[] {1f, 1f, 1f});
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
        modelList.add(shadow);


        model = new GLModel(gl, "cylinder.obj", shader);
        model.setModelMatrixOffset(0f, 0f, 4f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "cylinder.obj", shader);
        model.setModelMatrixOffset(0f, 0f, -4f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        
        /*
        subtractor = new BackgroundSubtractor(gl, 0, canvas);
        modelList.add(subtractor);

        GLSphereCollision sphereCollision = new GLSphereCollision(new Point3f(-4f, 0.0f, 0.0f), 1);
        model = new GLCam(gl, subtractor, "player1.obj", camShader, -4f, sphereCollision);
        model.setModelMatrixOffset(-4f, 0.0f, 0.0f);
        modelList.add(model);
        ball.addCollision(sphereCollision);
        //shadow = new GLShadow(gl, model, shadowShader);
        //modelList.add(shadow);

        sphereCollision = new GLSphereCollision(new Point3f(4f, 0.0f, 0.0f), 1);
        model = new GLCam(gl, subtractor, "player2.obj", camShader, 4, sphereCollision);
        model.setModelMatrixOffset(4f, 0.0f, 0.0f);
        modelList.add(model);
        ball.addCollision(sphereCollision);
        //shadow = new GLShadow(gl, model, shadowShader);
        //modelList.add(shadow);
        */

        modelList.addAll(loadPalmTrees(gl, shader, shadowShader));
        modelList.addAll(loadGrass(gl, shader, shadowShader));

        return modelList;

    }

    private static List<GLObject> loadGrass(GL3 gl, Shader shader, Shader shadowShader) {
        List<GLObject> modelList = new LinkedList<>();
        GLModel model;
        GLShadow shadow;

        model = new GLModel(gl, "Grass01.obj", shader);
        model.setModelMatrixOffset(-5f, 0f, 20f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass02.obj", shader);
        model.setModelMatrixOffset(-3f, 0f, 10f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass02.obj", shader);
        model.setModelMatrixOffset(0f, 0f, 15f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass01.obj", shader);
        model.setModelMatrixOffset(-15f, 0f, 25f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass02.obj", shader);
        model.setModelMatrixOffset(12f, 0f, 10f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass03.obj", shader);
        model.setModelMatrixOffset(-1f, 0f, 25f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass03.obj", shader);
        model.setModelMatrixOffset(-9f, 0f, 0f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass02.obj", shader);
        model.setModelMatrixOffset(-11f, 0f, -2f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass03.obj", shader);
        model.setModelMatrixOffset(-15f, 0f, 2f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass03.obj", shader);
        model.setModelMatrixOffset(9f, 0f, -1f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass02.obj", shader);
        model.setModelMatrixOffset(8f, 0f, 10f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass01.obj", shader);
        model.setModelMatrixOffset(13f, 0f, 1f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass02.obj", shader);
        model.setModelMatrixOffset(4f, 0f, 20f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        //////////////////////////////
        model = new GLModel(gl, "Grass01.obj", shader);
        model.setModelMatrixOffset(-5f, 0f, -20f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass02.obj", shader);
        model.setModelMatrixOffset(-3f, 0f, -10f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass02.obj", shader);
        model.setModelMatrixOffset(0f, 0f, -15f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass01.obj", shader);
        model.setModelMatrixOffset(-15f, 0f, -25f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass02.obj", shader);
        model.setModelMatrixOffset(12f, 0f, -10f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass03.obj", shader);
        model.setModelMatrixOffset(-1f, 0f, -25f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass03.obj", shader);
        model.setModelMatrixOffset(-9f, 0f, -40f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass02.obj", shader);
        model.setModelMatrixOffset(-22f, 0f, -35f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass03.obj", shader);
        model.setModelMatrixOffset(-0f, 0f, -30f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass03.obj", shader);
        model.setModelMatrixOffset(22f, 0f, -45f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass02.obj", shader);
        model.setModelMatrixOffset(8f, 0f, -10f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass01.obj", shader);
        model.setModelMatrixOffset(13f, 0f, -15f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "Grass02.obj", shader);
        model.setModelMatrixOffset(4f, 0f, -20f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);


        return modelList;
    }

    private static List<GLObject> loadPalmTrees(GL3 gl, Shader shader, Shader shadowShader) {

        List<GLObject> modelList = new LinkedList<>();
        GLModel model;
        GLShadow shadow;

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-5f, 0f, -40f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-20f, 0f, -10f);
        model.setModelMatrixRotation(1, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-2f, 0f, -60f);
        model.setModelMatrixRotation(2, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(5f, 0f, -10f);
        model.setModelMatrixRotation(3, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(25f, 0f, -30f);
        model.setModelMatrixRotation(3, 0, 1, 0);
        modelList.add(model);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(30f, 0f, -25f);
        model.setModelMatrixRotation(2, 0, 1, 0);
        modelList.add(model);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(20f, 0f, -35f);
        model.setModelMatrixRotation(1.5f, 0, 1, 0);
        modelList.add(model);

        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(8f, 0f, -30f);
        model.setModelMatrixRotation(0.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(20f, 0f, -32f);
        model.setModelMatrixRotation(1.0f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(35f, 0f, -34f);
        model.setModelMatrixRotation(1.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(16f, 0f, -48f);
        model.setModelMatrixRotation(2.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-20f, 0f, -20f);
        model.setModelMatrixRotation(3.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-40f, 0f, -28f);
        model.setModelMatrixRotation(1.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-3f, 0f, -24f);
        model.setModelMatrixRotation(2.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-8f, 0f, -18f);
        model.setModelMatrixRotation(0.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(0f, 0f, -18f);
        model.setModelMatrixRotation(2.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-11f, 0f, -50f);
        model.setModelMatrixRotation(3.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-14f, 0f, -22f);
        model.setModelMatrixRotation(1.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(2f, 0f, -30f);
        model.setModelMatrixRotation(2.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-14f, 0f, -18f);
        model.setModelMatrixRotation(0.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(12f, 0f, -30f);
        model.setModelMatrixRotation(3.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-4f, 0f, -22f);
        model.setModelMatrixRotation(1.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-30f, 0f, -24f);
        model.setModelMatrixRotation(2.5f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        model = new GLModel(gl, "palm02.obj", shader);
        model.setModelMatrixOffset(-8f, 0f, -18f);
        model.setModelMatrixRotation(0.8f, 0, 1, 0);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        return modelList;

    }
}
