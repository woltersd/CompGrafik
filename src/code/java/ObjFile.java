package code.java;

import com.jogamp.opengl.util.GLBuffers;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.vecmath.Point3f;

/**
 *
 * @author Robert
 * @author Peter
 */
public class ObjFile {

    private ArrayList<Point3f> vertexData;
    private ArrayList<Point3f> normalData;
    private LinkedList<Short> faceIndexData;
    private LinkedList<Short> normalIndexData;

    public ObjFile(String objFilename) throws IOException {
        vertexData = new ArrayList<>();
        normalData = new ArrayList<>();
        faceIndexData = new LinkedList<>();
        normalIndexData = new LinkedList<>();
        parseObjFile(objFilename);
    }

    private void parseObjFile(String objFilename) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(objFilename)));

        String line;
        while (true) {
            line = bufferedReader.readLine();
            if (null == line) {
                break;
            }
            // empty or comment
            if (line.length() == 0 || line.startsWith("#"))
            {
                continue;
            }
            String[] values = line.split("\\s+");
            // vertex
            switch (values[0]) {
                case "v":
                    vertexData.add(new Point3f(Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3])));
                    break;
                // vertexNormal
                case "vn":
                    normalData.add(new Point3f(Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3])));
                    break;
                // face
                case "f":
                    processFace(values);
                    break;
            }
        }
        bufferedReader.close();
    }

    private void processFace(String[] values) {
            for (int i = 1; i < values.length; i++) {
                String[] index = values[i].split("/");
                faceIndexData.add((short) (Short.parseShort(index[0]) - 1));
                // vt 2nd value not used right now
                // if (val.length >= 1 && !val[1].isEmpty()) {
                //
                // }
                if (index.length == 2 && !index[2].isEmpty()) {
                    normalIndexData.add((short) (Short.parseShort(index[2]) - 1));
                }
            }
    }

    public FloatBuffer getVertexBuffer() {
        FloatBuffer vertexBuffer = FloatBuffer.allocate(vertexData.size() * 3);

        for (Point3f each : vertexData) {
            GLBuffers.putf(vertexBuffer, each.x);
            GLBuffers.putf(vertexBuffer, each.y);
            GLBuffers.putf(vertexBuffer, each.z);
        }
        vertexBuffer.rewind();
        return vertexBuffer;
    }

    public FloatBuffer getNormalBuffer() {
        FloatBuffer normalBuffer = FloatBuffer.allocate(normalData.size() * 3) ;

        for (Point3f each : normalData) {
            GLBuffers.putf(normalBuffer, each.x);
            GLBuffers.putf(normalBuffer, each.y);
            GLBuffers.putf(normalBuffer, each.z);
        }
        normalBuffer.rewind();
        return normalBuffer;
    }

    public ShortBuffer getFaceIndexBuffer() {
        ShortBuffer faceIndexBuffer = ShortBuffer.allocate(faceIndexData.size() * 3);
        for (short each : faceIndexData) {
            GLBuffers.puts(faceIndexBuffer, each);
        }
        faceIndexBuffer.rewind();
        return faceIndexBuffer;
    }
}