package com.briefjudofox;


import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.neo4j.gis.spatial.Layer;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.SpatialRecord;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Enrich {

    private static String inputLocationsPath = "data/input_locations_10.csv";
    private static int csvLongColIndex = 2;  //0 based index of x column in csv
    private static int csvLatColIndex = 1;  //0 based index of y column in csv
    private static String outputLocationsPath = "data/ENRICH_OUT.csv";
    private static String graphLocationsPath = "/data/output";
    private static String targetLayerName = "BG"; //Block Groups


    /**
     * Spatially joins attributes from the polygon feature that
     * contains a given point feature.
     *
     * Reads CSV at inputLocationsPath, iterates rows (points), and spatially queries the
     * features in the specified layer (targetLayerName) in the specified graph (graphLocationsPath).
     * Attributes from both features are joined and written to the CSV at outputLocationsPath;
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        CSVWriter writer = getWriter();
        CSVReader reader = getReader();
        GeometryFactory gf = new GeometryFactory();
        GraphDatabaseService database = new GraphDatabaseFactory().newEmbeddedDatabase(graphLocationsPath);

        SpatialDatabaseService spatialService = new SpatialDatabaseService(database);

        Layer layer = spatialService.getLayer(targetLayerName);
        GeoPipeline geoPipeline;
        String [] row;
        System.out.println("Starting Enrichment...");
        int count = 0;
        Date start = new Date();
        while ((row = reader.readNext()) != null) {
            geoPipeline = GeoPipeline.startContainSearch(layer, rowToPoint(gf,row)).copyDatabaseRecordProperties();
            resultToCSV(writer,row,geoPipeline);
            if(++count % 1000 == 0){
                System.out.println("Enriched so far: " + count);
            }
        }

        System.out.println("Finished Enrichment in " + getRuntimeStats(start, new Date()));

        writer.close();
        reader.close();

    }

    private static void resultToCSV(CSVWriter writer,String [] row,GeoPipeline geoPipeline){
        int count = 0;
        for (SpatialRecord result : geoPipeline) {
           if(count++ == 0) {
              String[] enrichedRow = Arrays.copyOf(row,row.length + result.getProperties().size());
              int index = row.length;
              for (Object val : result.getProperties().values()) {
                      enrichedRow[index++] = val.toString();
              }
              writer.writeNext(enrichedRow);
           }else {
               System.out.println("WARNING: Spatial query returned more that 1 result.  Expected 1 or 0.  Only first was used.");
           }
        }
    }

    private static Point rowToPoint( GeometryFactory gf, String[] row){
      return gf.createPoint(new Coordinate(Double.parseDouble(row[csvLongColIndex]),Double.parseDouble(row[csvLatColIndex])));
    }

    private static CSVWriter getWriter(){
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(outputLocationsPath), '\t', CSVWriter.NO_QUOTE_CHARACTER);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }


    private static CSVReader getReader(){
        CSVReader reader = null;
         try {
              reader = new CSVReader(new FileReader(inputLocationsPath), '\t');
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         }
        return reader;
    }

    private static String getRuntimeStats(Date start, Date end) {
        long runTime = end.getTime() - start.getTime();
        String runTimeStr = String.format("%d min, %d sec",
            TimeUnit.MILLISECONDS.toMinutes(runTime),
            TimeUnit.MILLISECONDS.toSeconds(runTime) -
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runTime))
        );
       return runTimeStr;
    }
}
