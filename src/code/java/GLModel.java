package code.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @author Robert
 */
public class GLModel {

    private int vertexCount = 0;
    private int normalCount = 0;
    private int indexCount = 0;

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
        vertexCount = objFile.getVertexCount();
        normalCount = objFile.getNomalCount();
        indexCount = objFile.getIndexCount();
    }

    public FloatBuffer getVertexBuffer() {
        return objFile.getVertexBuffer();
    }

    public FloatBuffer getNormalBuffer() {
        return objFile.getNormalBuffer();
    }

    public ShortBuffer getIndexBuffer() {
        return objFile.getIndexBuffer();
    }

    public FloatBuffer getComboBuffer(){
        return objFile.getComboBuffer();
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getNormalCount(){
        return normalCount;
    }

    public int getIndexCount() {
        return indexCount;
    }

    public boolean bufferContains(float item, FloatBuffer buffer) {
        for (int i = 0; i < buffer.position(); i++) {
            if (buffer.get(i) == item) {
                return true;
            }
        }
        return false;
    }

    public boolean normal(){
        return objFile.normal();
    }

}