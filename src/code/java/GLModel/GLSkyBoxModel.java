package code.java.GLModel;

import code.java.Shader;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.GLBuffers;
import org.opencv.imgcodecs.Imgcodecs;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

/**
 * @author peter
 */
public class GLSkyBoxModel extends GLModelAbstract {

    static float vertices[] =
    {
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f
    };

    static short indicesMap[] = {
            1,1,
            2,2,
            3,3,
            4,4,
            5,5,
            6,6,
            7,7,
            8,8,
            2,9,
            6,10,
            1,11,
            5,12,
            5,13,
            6,14
    };

    static short indices[] = {
            2,1,3
          /*  2,3,4,
            5,6,7,
            6,7,8,*/
          //  9,4,10,
          //  4,10,8,
           /* 11,3,12,
            3,12,7,
            3,4,7,
            4,7,8,
            1,2,13,
            2,13,14,*/
    };

    static float texCoords[] = {
            0f, 0f,
            0f, 1f,
            1f, 1f,


            0.75f, 1/3f,    //1
            0.75f, 2/3f,
            0.5f, 1/3f,
            0.5f, 2/3f,
            0f, 1/3f,       //5
            0f, 2/3f,
            0.25f, 1/3f,
            0.25f, 2/3f,
            0.5f, 1f,
            0.25f, 1,       //10
            0.5f, 0,
            0.25f, 0,
            1, 1/3f,
            1, 2/3f
    };

    /*static float texCoords[] = {
            0.75f, 1/3f,    //1
            0.75f, 2/3f,
            0.5f, 1/3f,
            0.5f, 2/3f,
            0f, 1/3f,       //5
            0f, 2/3f,
            0.25f, 1/3f,
            0.25f, 2/3f,
            0.5f, 1f,
            0.25f, 1,       //10
            0.5f, 0,
            0.25f, 0,
            1, 1/3f,
            1, 2/3f
    };*/




  /*  static float texCoords[] = {
            0.75f, 1/3f,    0.75f, 2/3f,    0.5f, 1/3f,
            0.75f, 2/3f,    0.5f, 2/3f,     0.5f, 2/3f,
            0f, 1/3f,       0f, 2/3f,       0.25f, 1/3f,
            0f, 2/3f,       0.25f, 1/3f,    0.25f, 2/3f,
            0.5f, 1,        0.5f, 2/3f,     0.25f, 1f,
            0.5f, 2/3f,     0.25f, 1f,      0.25f, 2/3f,
            0.5f, 0f,       0.5f, 1/3f,     0.25f, 0f,
            0.5f, 1/3f,     0.25f, 0f,      0.25f, 1/3f,
            0.5f, 1/3f,     0.5f, 2/3f,     0.25f, 1/3f,
            0.5f, 2/3f,     0.25f, 1/3f,    0.25f, 2/3f,
            0.75f, 1/3f,    0.75f, 2/3f,    1f, 1/3f,
            0.75f, 2/3f,    1f, 1/3f,       1f, 2/3f
    };

    static short indicesVertices[] = {
        1,2,3,
        2,3,4,
        5,6,7,
        6,7,8,
        2,4,6,
        4,6,8,
        1,3,5,
        3,5,7,
        3,4,7,
        4,7,8,
        1,2,5,
        2,5,6,
    };
    */

    public GLSkyBoxModel(GL3 gl) {
        this(gl, new Shader(gl, "/src/code/glsl/", "skybox_vertex_shader.glsl", "skybox_fragment_shader.glsl"));
    }

    public GLSkyBoxModel(GL3 gl, Shader shader) {
        super();
        // ("imgFile = System.getProperty(\"user.dir\").replaceAll(\"\\\\\\\\\", \"/\") + \"/src/res/Day_Skybox.png\"");
        buildGLData();
        loadTextureImage();

        setShader(shader);
      //  Matrix4 projection = new Matrix4();
      //  projection.makeOrtho(-100f, 100f, -100f, 100f, 0.1f, 100f);
      //  setShaderUniform("projection", projection);
      //  setShaderUniform("skybox", 0);

        setVertexCount(glVertexData.size() * 3);
        setTextureCount(glTextureData.size() * 2);
        setIndexCount(glIndexData.size());

        initializeBuffers(gl);
        // temp
        setShaderUniform("tex_1", 0);


    }

    private void buildGLData(){
        for (int i = 0; i < indicesMap.length / 2; i++) {
            int vertexIndex = (indicesMap[2*i] - 1) * 3;
            int texIndex = (indicesMap[2*i+1] - 1) * 2;
            Point3f vertexPoint = new Point3f(vertices[vertexIndex], vertices[vertexIndex+1], vertices[vertexIndex+2]);
            glVertexData.add(vertexPoint);
            Point2f texPoint = new Point2f(texCoords[texIndex], texCoords[texIndex+1]);
            glTextureData.add(texPoint);
        }
        for (int i = 0; i < indices.length / 3; i++) {
            glIndexData.add((short) (indices[3*i] - 1));
            glIndexData.add((short) (indices[3*i+1] - 1));
            glIndexData.add((short) (indices[3*i+2] - 1));
        }
    }


    private void loadTextureImage() {
        image = Imgcodecs.imread(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/" + "Day_Skybox_small.png", Imgcodecs.IMREAD_UNCHANGED);
       // image = Imgcodecs.imread(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/" + "volleyball_dirty.png", Imgcodecs.IMREAD_UNCHANGED);

        //if(image.rows() == 0){
      image = Imgcodecs.imread(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/replacement.png", Imgcodecs.IMREAD_UNCHANGED);
      //  }
        byte[] bytes = new byte[image.rows() * image.cols() * image.channels()];
        image.get(0, 0, bytes);
        textureImage = GLBuffers.newDirectByteBuffer(bytes);
    }

    //@Override
    protected void initializeBuffers(GL3 gl) {

        // generate Index Buffer Object
        gl.glGenBuffers(1, IntBuffer.wrap(ibo));
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, getIndexCount() * 4, getIndexBuffer(), GL3.GL_STATIC_DRAW);
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, 0);



        // generate vertex buffer object
        gl.glGenBuffers(1, IntBuffer.wrap(vbo));
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, (getVertexCount() +  getTextureCount()) * 4, getComboBuffer(), GL3.GL_STATIC_DRAW);
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);

        // skyBoxTexture
    /*    gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glGenTextures(1, IntBuffer.wrap(tbo));
        gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, tbo[0]);
      /*  gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR_MIPMAP_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_R, GL3.GL_CLAMP_TO_EDGE);
        gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, 0);*/

        // texture
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glGenTextures(1, IntBuffer.wrap(tbo));
        gl.glBindTexture(GL3.GL_TEXTURE_2D, tbo[0]);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
        //gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGB, image.cols(), image.rows(), 0, GL3.GL_BGR, GL3.GL_UNSIGNED_BYTE, getTextureImage());
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA, image.cols(), image.rows(), 0, GL3.GL_BGR, GL3.GL_UNSIGNED_BYTE, getTextureImage());
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);


    }

    public void bindBuffer(GL3 gl) {

       // gl.glDisable(GL3.GL_DEPTH_TEST);

        // index buffer
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);

        // texture image buffer
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, tbo[0]);

    //    gl.glActiveTexture(GL3.GL_TEXTURE0);//gl.glEnable(GL3.GL_TEXTURE_CUBE_MAP);
    //    gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, tbo[0]);



        // vertex+texture buffer
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 20, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 2, GL3.GL_FLOAT, false, 20, 12);
     //   gl.glEnableVertexAttribArray(2);
     //   gl.glVertexAttribPointer(2, 3, GL3.GL_FLOAT, false, 32, 24);

       // gl.glEnable(GL3.GL_DEPTH_TEST);
    }

    @Override
    public void display(GL3 gl) {
        bindBuffer(gl);

        // view
        // shader.setUniform(gl, "modelMatrix", getModelMatrix());
        //
      //  getShader().bind(gl);
        for (Map.Entry<String, Object> entry : getShaderUniforms().entrySet()) {
            getShader().setUniform(gl, entry.getKey(), entry.getValue());
        }



        /*     gl.glEnable(GL3.GL_TEXTURE_CUBE_MAP);
        gl.glGenTextures(1, &skyBoxTexture);
        gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, skyBoxTexture);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_NEAREST);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_R, GL3.GL_CLAMP_TO_EDGE);*/

        // draw the triangles
        gl.glDrawElements(GL3.GL_TRIANGLES, getIndexCount(), GL3.GL_UNSIGNED_SHORT, 0);

        unbindBuffer(gl);
    }

    public void unbindBuffer(GL3 gl) {
    /*    // texture image buffer
        //gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, 0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
        // index buffer
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, 0);
        // vertex+normal+texture buffer
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);

        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);*/
    }

    @Override
    public FloatBuffer getComboBuffer() {
        FloatBuffer comboBuffer = FloatBuffer.allocate(getVertexCount() + getTextureCount());
        for (int i = 0; i<glVertexData.size(); i++) {
            GLBuffers.putf(comboBuffer, glVertexData.get(i).x);
            GLBuffers.putf(comboBuffer, glVertexData.get(i).y);
            GLBuffers.putf(comboBuffer, glVertexData.get(i).z);
            GLBuffers.putf(comboBuffer, glTextureData.get(i).x);
            GLBuffers.putf(comboBuffer, glTextureData.get(i).y);
        }

        comboBuffer.rewind();
        return comboBuffer;
    }
}
