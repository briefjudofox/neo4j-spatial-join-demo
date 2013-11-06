package com.briefjudofox;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import org.neo4j.gis.spatial.Layer;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.SpatialRecord;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.util.Map;

public class QueryTest {

    private static String graphLocationsPath = "/data/output";
    private static String targetLayerName = "BG"; //Block Groups

    /**
     * Quick demo of spatial query.  Finds the feature in the specified target layer
     * that contains the point at -118,34.  Prints out the user defined attributes
     * of the result feature.
     *
     * @param args
     * @throws ParseException
     */
    public static void main(String[] args) throws ParseException {
        GraphDatabaseService database = new GraphDatabaseFactory().newEmbeddedDatabase(graphLocationsPath);
        try {

            SpatialDatabaseService spatialService = new SpatialDatabaseService(database);
            Layer layer = spatialService.getLayer(targetLayerName);

            GeometryFactory gf = new GeometryFactory();
            Geometry point = gf.createPoint(new Coordinate(-118,34));

            System.out.println("Starting Query...");
            GeoPipeline geoPipeline = GeoPipeline.startContainSearch(layer, point).copyDatabaseRecordProperties();

            for (SpatialRecord result : geoPipeline) {
               System.out.println("\tResult Properties:");
                for (Map.Entry<String, Object> entry : result.getProperties().entrySet()) {
                    System.out.println("\t" +entry.getKey() +" : " + entry.getValue().toString());
                }

            }
            System.out.println("Query finished.");

        } finally {
        database.shutdown();
        }
    }


}
