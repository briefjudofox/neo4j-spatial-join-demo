package com.briefjudofox;

import org.geotools.data.shapefile.shp.ShapefileException;
import org.neo4j.gis.spatial.ShapefileImporter;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

public class ShapeToNeo4J {

    private static String targetLayerName = "BG";
    private static String shapeFilePath = "data/GCS_WGS84_BG/BG.shp";
    private static String targetGraphPath = "data/output";

    /**
     * Creates or appends features from specified shape file to specified Neo4j Graph.
     *
     * @param args
     */
    public static void main(String[] args) {

        GraphDatabaseService database = new GraphDatabaseFactory().newEmbeddedDatabase(targetGraphPath);
        try {
            ShapefileImporter importer = new ShapefileImporter(database);
            importer.importFile(shapeFilePath, targetLayerName, Charset.forName("UTF-8"));

        } catch (ShapefileException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            database.shutdown();
        }
    }
}
