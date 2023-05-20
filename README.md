# Spatial Hotspot Analysis on Geo-Spatial Data using Getis-Ord Statistic
A major peer-to-peer taxi cab firm has hired your team to develop and run multiple spatial queries on their large database that contains geographic data as well as real-time location data of their customers. A spatial query is a special type of query supported by geodatabases and spatial databases. The queries differ from traditional SQL queries in that they allow for the use of points, lines, and polygons. The spatial queries also consider the relationship between these geometries. Since the database is large and mostly unstructured, your client wants you to use a popular Big Data software application, SparkSQL. The goal of the project is to extract data from this database that will be used by your client for operational (day-to-day) and strategic level (long term) decisions.

## Description
This task will focus on applying spatial statistics to spatio-temporal big data in order to identify statistically significant spatial hot spots using Apache Spark. 

## To Get Started

#### Install Apache Spark and SparkSQL on Computer

You will be using Apache Spark and SparkSQL in this project. Apache Spark is a sophisticated Big Data software application. Each team member needs to install Apache Spark and SparkSQL on his/her computer by carefully following the instructions on the page  [https://spark.apache.org/docs/latest/](https://spark.apache.org/docs/latest/)

To get started, team members will need to do some research about Apache SparkSQL and spatial queries.

#### Required Resource:

[https://www.tutorialspoint.com/spark_sql/spark_sql_quick_guide.htm](https://www.tutorialspoint.com/spark_sql/spark_sql_quick_guide.htm)
 

## Coding template specification

### Input parameters

1. Output path (Mandatory)
2. Task name: "hotzoneanalysis" or "hotcellanalysis"
3. Task parameters: (1) Hot zone (2 parameters): nyc taxi data path, zone path(2) Hot cell (1 parameter): nyc taxi data path

Example
```
test/output hotzoneanalysis src/resources/point-hotzone.csv src/resources/zone-hotzone.csv hotcellanalysis src/resources/yellow_trip_sample_100000.csv
```

### Input data format
#### Hot zone analysis
The input point data can be any small subset of NYC taxi dataset.

#### Hot cell analysis
The input point data is a monthly NYC taxi trip dataset (2009-2012) like "yellow\_tripdata\_2009-01\_point.csv"

### Output data format

#### Hot zone analysis
All zones with their count, sorted by "rectangle" string in an ascending order. 

```
"-73.795658,40.743334,-73.753772,40.779114",1
"-73.797297,40.738291,-73.775740,40.770411",1
"-73.832707,40.620010,-73.746541,40.665414",20
```


#### Hot cell analysis
The coordinates of top 50 hotest cells sorted by their G score in a descending order.

```
-7399,4075,15
-7399,4075,29
-7399,4075,22
```

As I have created a dockerfile, you can download and run using the following script:

1. Run `docker pull shikharg1997/group6-project1-phase2-bonus:v0` to download image 
2. Run `docker run -it --rm shikharg1997/group6-project1-phase2-bonus:v0` to start the container. 
3. Run `sbt assembly`
4. Run `spark-submit target/scala-2.12/CSE511-Hotspot-Analysis-assembly-0.1.0.jar result/output hotzoneanalysis src/resources/point-hotzone.csv src/resources/zone-hotzone.csv hotcellanalysis src/resources/yellow_tripdata_2009-01_point.csv`

Docker Image: `docker pull shikharg1997/group6-project1-phase2-bonus:v0`
