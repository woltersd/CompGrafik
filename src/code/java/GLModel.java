package code.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @author Robert
 * @author peter
 */
public class GLModel {

    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;
    private ShortBuffer faceIndexBuffer;


    private ObjFile objFile;

    GLModel(String objPath) {
        //TODO better error handling
        try {
            this.objFile = new ObjFile(objPath);
        } catch (FileNotFoundException fnf) {
            System.err.println("FileNotFoundException " + objPath);
        } catch (IOException io) {
            System.err.println("IOException " + objPath);
        }
        vertexBuffer = objFile.getVertexBuffer();
        normalBuffer = objFile.getNormalBuffer();
        faceIndexBuffer = objFile.getFaceIndexBuffer();
    }

    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public FloatBuffer getNormalBuffer() {
        return normalBuffer;
    }

    public ShortBuffer getFaceIndexBuffer() {
        return objFile.getFaceIndexBuffer();
    }

    public int getVertexCount() {
        return vertexBuffer.limit();
    }

    public int getNormalCount(){
        return normalBuffer.limit();
    }

    public int getFaceIndexCount() {
        return faceIndexBuffer.limit();
    }
}