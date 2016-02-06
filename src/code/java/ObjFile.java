/*
 *
 */
package code.java;

import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;

/**
 *
 * @author Robert
 * @author peter
 */
public class ObjFile {
    private String objName;

    private ArrayList<Point3f> vertexData;
    private ArrayList<Point3f> normalData;
    private ArrayList<Point2f> textureData;
    private ArrayList<Short> faceIndexData;
    private ArrayList<Short> normalIndexData;
    private ArrayList<Short> textureIndexData;

    private boolean normal = false;
    private boolean texture = false;

    public ObjFile(String objFilename) throws IOException {
        vertexData = new ArrayList<>();
        normalData = new ArrayList<>();
        textureData = new ArrayList<>();
        faceIndexData = new ArrayList<>();
        normalIndexData = new ArrayList<>();
        textureIndexData = new ArrayList<>();
        //TODO normalize?
        parseObjFile(objFilename);
    }

    private void parseObjFile(String objFilename) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(objFilename)));

        String line;
        String[] values;
        while (true) {
            line = bufferedReader.readLine();
            if (null == line) {
                break;
            }
            if (line.length() == 0 || line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("vn")) {
                normal = true;
                values = line.split("\\s+");
                normalData.add(new Point3f(Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3])));
            } else if (line.startsWith("vt")) {
                texture = true;
                values = line.split("\\s+");
                textureData.add(new Point2f(Float.parseFloat(values[1]), Float.parseFloat(values[2])));
            } else if (line.startsWith("v")) {
                values = line.split("\\s+");
                vertexData.add(new Point3f(Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3])));
            } else if (line.startsWith("f")) {
                processFace(line);
            } else if (line.startsWith("o")) {
                values = line.split("\\s+");
                objName = values[1];
            }
        }
        bufferedReader.close();
    }

    private void processFace(String line) {
        String[] values = line.split("\\s+");
        String[] val;
        for (int i = 1; i < values.length; i++) {
            val = values[i].split("/");
            faceIndexData.add((short) (Short.parseShort(val[0]) - 1));
            if (val.length >= 2 && !val[1].isEmpty()) {
                textureIndexData.add((short) (Short.parseShort(val[1]) - 1));
            }
            if (val.length == 3 && !val[2].isEmpty()) {
                normalIndexData.add((short) (Short.parseShort(val[2]) - 1));
            }
        }
    }

    public String getObjName() { return objName; }

    public ArrayList<Point3f> getVertexData(){
        return vertexData;
    }

    public ArrayList<Point3f> getNormalData(){
        return normalData;
    }

    public ArrayList<Point2f> getTextureData(){ return textureData; }

    public ArrayList<Short> getFaceIndexData(){
        return faceIndexData;
    }

    public ArrayList<Short> getNormalIndexData(){
        return normalIndexData;
    }

    public ArrayList<Short> getTextureIndexData(){ return textureIndexData; }

    public boolean normal() {
        return normal;
    }
    public boolean texture() { return texture; }

}
