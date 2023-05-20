package cse511

import org.apache.spark.sql.SparkSession
import scala.math._

object SpatialQuery extends App{

  def ST_Contains(queryRectangle:String, queryPoint:String): Boolean = {
    val rectangleCoords = queryRectangle.split(',').map (x => x.toDouble)
    val pointCoords = queryPoint.split(',').map (x => x.toDouble)

    val x1 = rectangleCoords(0).min(rectangleCoords(2))
    val x2 = rectangleCoords(0).max(rectangleCoords(2))
    val y1 = rectangleCoords(1).min(rectangleCoords(3))
    val y2 = rectangleCoords(1).max(rectangleCoords(3))
	
    // for point (x,y) to lie inside rectangle (x1,y1,x2,y2) -> x1<=x<=x2 and y1<=y<=y2 
    if (pointCoords(0) < x1 || pointCoords(0) > x2 || pointCoords(1) < y1 || pointCoords(1) > y2)
      return false
    else
      return true 
  }
  
  def ST_Within(pointString1: String, pointString2: String, distance: Double): Boolean = {
    val pointOne = pointString1.split(",").map(_.trim.toDouble)
    val pointTwo = pointString2.split(",").map(_.trim.toDouble)

    val xOne = pointOne(0)
    val yOne = pointOne(1)
    val xTwo = pointTwo(0)
    val yTwo = pointTwo(1)

    var calculatedDistance = math.sqrt(math.pow((yTwo - yOne), 2) + math.pow((xTwo - xOne), 2))

    if(calculatedDistance <= distance) {
      return true
    } 
    return false
  }

  def runRangeQuery(spark: SparkSession, arg1: String, arg2: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Contains",ST_Contains _)

    val resultDf = spark.sql("select * from point where ST_Contains('"+arg2+"',point._c0)")
    resultDf.show()

    return resultDf.count()
  }

  def runRangeJoinQuery(spark: SparkSession, arg1: String, arg2: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point")

    val rectangleDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg2);
    rectangleDf.createOrReplaceTempView("rectangle")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Contains",ST_Contains _)

    val resultDf = spark.sql("select * from rectangle,point where ST_Contains(rectangle._c0,point._c0)")
    resultDf.show()

    return resultDf.count()
  }

  def runDistanceQuery(spark: SparkSession, arg1: String, arg2: String, arg3: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Within",(pointString1:String, pointString2:String, distance:Double)=>((SpatialQuery.ST_Within(pointString1, pointString2, distance))))

    val resultDf = spark.sql("select * from point where ST_Within(point._c0,'"+arg2+"',"+arg3+")")
    resultDf.show()

    return resultDf.count()
  }

  def runDistanceJoinQuery(spark: SparkSession, arg1: String, arg2: String, arg3: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point1")

    val pointDf2 = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg2);
    pointDf2.createOrReplaceTempView("point2")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Within",(pointString1:String, pointString2:String, distance:Double)=>(SpatialQuery.ST_Within(pointString1, pointString2, distance)))
    val resultDf = spark.sql("select * from point1 p1, point2 p2 where ST_Within(p1._c0, p2._c0, "+arg3+")")
    resultDf.show()

    return resultDf.count()
  }
}