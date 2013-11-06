# Neo4j Spatial Demos

This is a quick and simple demo that uses [Neo4j Spatial](https://github.com/neo4j/spatial) to manipulate some spatial data.

[ShapeToNeo4J](https://github.com/briefjudofox/neo4j-spatial-join-demo/blob/master/src/main/java/com/briefjudofox/ShapeToNeo4J.java) imports features from a shape file into a Neo4j graph.

[QueryTest](https://github.com/briefjudofox/neo4j-spatial-join-demo/blob/master/src/main/java/com/briefjudofox/QueryTest.java) shows a simple point in polygon spatial query.

[Enrich](https://github.com/briefjudofox/neo4j-spatial-join-demo/blob/master/src/main/java/com/briefjudofox/Enrich.java) reads a CSV file with at least X and Y columns. It iterates rows in the csv (points) and spatially queries against features in a specified layer of the graph.  Attributes from both features are joined and written to a specified CSV file.  An example use case is appending attributes to a collection of points from the corresponding polygon feature (e.g. Census block group, postal code, etc.) that contains them.

Build with Maven: `mvn clean install`

Each class has a main method `main(String[] args)`